#!/usr/bin/env python3
"""
collapse_dot_chains.py

Collapses long "value by value" enumeration chains in a Choco Solver
search-tree .dot file into a single yellow summary node.

Why this exists
----------------
Choco's default search often tries a variable's domain value by value:

    d_4: X != 12800   -> (fail)  -> d_5: X != 12801 -> (fail) -> d_6: X != 12802 -> ...

Each of these nodes only ever leads to one immediate failure (a red point)
and one continuation node testing the *next* consecutive value for the
*same* variable. This produces a long, visually useless straight line in
the rendered graph. The moment a node's "continuation" child is NOT simply
"the next value of the same variable tested the same way", that's a real
branching point (a "functional embranchement") and is left untouched.

This script finds every such run of >= --min-chain nodes and replaces it
with a single yellow circle labelled with the first and last value tested,
reconnecting the graph so nothing else changes.

Usage
-----
    python3 collapse_dot_chains.py input.dot output.dot [--min-chain 3]

Assumptions about the input format (matches Choco's dot export)
-----------------------------------------------------------------
    <id> [label = "d_<depth>: <var><op><value>" shape = circle];
    <parent> -> <id>;
    <id> [shape = point, color = red];   # failure leaf

Nodes/edges that don't match these patterns are copied through unchanged.
"""

import argparse
import re
import sys
from collections import OrderedDict, defaultdict
from pathlib import Path
from decimal import Decimal

NODE_RE = re.compile(r'^\s*(\d+)\s*\[(.*)\]\s*;?\s*$')
EDGE_RE = re.compile(r'^\s*(\d+)\s*->\s*(\d+)\s*;?\s*$')
ATTR_RE = re.compile(r'(\w+)\s*=\s*("(?:[^"\\]|\\.)*"|[^\s,\]]+)')

# "d_4: end_Image_Add_var!=12800"  ->  prefix="d_4", var="end_Image_Add_var", op="!=", value=12800
DECISION_LABEL_RE = re.compile(
    r'^(d_\d+):\s*(.*?)\s*(!=|==|<=|>=|<|>)\s*(-?\d+)\s*$'
)


def strip_quotes(s):
    if len(s) >= 2 and s[0] == '"' and s[-1] == '"':
        return s[1:-1]
    return s


def parse_attrs(bracket_content):
    attrs = OrderedDict()
    for m in ATTR_RE.finditer(bracket_content):
        key, val = m.group(1), m.group(2)
        attrs[key] = val
    return attrs


def format_attrs(attrs):
    return ', '.join(f'{k} = {v}' for k, v in attrs.items())


def _looks_like_complete_line(line):
    """Heuristic: does this line look like a finished dot statement, rather
    than something a writer got cut off in the middle of?"""
    s = line.strip()
    if s == '' or s in ('{', '}'):
        return True
    if s.startswith('//') or s.startswith('#'):
        return True
    if NODE_RE.match(line) or EDGE_RE.match(line):
        return True
    # graph/digraph header line, or any statement properly terminated
    if s.endswith(';') or s.endswith('{') or 'digraph' in s or 'graph' in s:
        return True
    return False


def repair_truncated_dot(text):
    """Makes a best-effort repair of a .dot file whose writer was killed or
    interrupted before finishing: drops a dangling half-written last line (if
    any) and appends whatever closing '}' are needed to balance braces
    (counted outside of quoted label strings, since a label could itself
    contain a literal brace character).

    Returns (repaired_text, dropped_incomplete_line, num_braces_added).
    """
    lines = text.splitlines()
    while lines and lines[-1].strip() == '':
        lines.pop()

    dropped = False
    if lines and not _looks_like_complete_line(lines[-1]):
        lines.pop()
        dropped = True

    joined = '\n'.join(lines)
    no_strings = re.sub(r'"(?:[^"\\]|\\.)*"', '', joined)
    missing = no_strings.count('{') - no_strings.count('}')
    if missing > 0:
        lines.extend(['}'] * missing)

    return '\n'.join(lines) + '\n', dropped, max(missing, 0)


class DotGraph:
    def __init__(self):
        # Ordered list of raw statements as encountered, so we can
        # regenerate the file preserving original ordering/formatting
        # for anything we don't touch.
        self.statements = []  # list of dicts: {'kind': 'node'/'edge'/'other', ...}
        self.nodes = {}       # id -> attrs dict
        self.children = defaultdict(list)  # id -> [child ids] in appearance order
        self.parents = {}     # id -> parent id (tree assumption: single parent)

    def load(self, text):
        for line in text.splitlines():
            m = NODE_RE.match(line)
            if m:
                node_id, bracket = m.group(1), m.group(2)
                attrs = parse_attrs(bracket)
                self.nodes[node_id] = attrs
                self.statements.append({'kind': 'node', 'id': node_id})
                continue
            m = EDGE_RE.match(line)
            if m:
                src, dst = m.group(1), m.group(2)
                self.children[src].append(dst)
                self.parents[dst] = src
                self.statements.append({'kind': 'edge', 'src': src, 'dst': dst})
                continue
            self.statements.append({'kind': 'other', 'text': line})

    def is_fail_leaf(self, node_id):
        attrs = self.nodes.get(node_id)
        if attrs is None:
            return False
        shape = strip_quotes(attrs.get('shape', ''))
        color = strip_quotes(attrs.get('color', ''))
        return shape == 'point' and color == 'red'

    def decision_info(self, node_id):
        """Return (prefix, var, op, value:int) if node_id is a decision node
        whose label matches the expected pattern, else None."""
        attrs = self.nodes.get(node_id)
        if attrs is None or 'label' not in attrs:
            return None
        label = strip_quotes(attrs['label'])
        m = DECISION_LABEL_RE.match(label)
        if not m:
            return None
        prefix, var, op, value = m.group(1), m.group(2), m.group(3), int(m.group(4))
        return prefix, var, op, value


def find_chain(g, start_id, decisions_cache):
    """Return the maximal chain [start_id, ..., last_id] of nodes that each
    test the same variable/operator at a constant value step, where every
    node except the last only has a fail-leaf child plus the next-value
    child."""
    chain = [start_id]
    step = None
    while True:
        cur = chain[-1]
        kids = g.children.get(cur, [])
        other_kids = [k for k in kids if not g.is_fail_leaf(k)]
        if len(other_kids) != 1:
            break  # 0 or >=2 non-fail children => real branching, stop here
        nxt = other_kids[0]
        nxt_info = decisions_cache.get(nxt)
        if nxt_info is None:
            break
        cur_info = decisions_cache[cur]
        if nxt_info[1] != cur_info[1] or nxt_info[2] != cur_info[2]:
            break  # different variable or operator
        diff = nxt_info[3] - cur_info[3]
        if diff == 0:
            break
        if step is None:
            step = diff
        elif diff != step:
            break
        chain.append(nxt)
    return chain


def collapse(g, min_chain, verbose=True):
    decisions_cache = {}
    for nid in g.nodes:
        info = g.decision_info(nid)
        if info is not None:
            decisions_cache[nid] = info

    visited = set()
    chains = []
    # Deterministic order: sort numeric ids so results are reproducible
    for nid in sorted(g.nodes.keys(), key=lambda x: int(x)):
        if nid in visited or nid not in decisions_cache:
            continue
        chain = find_chain(g, nid, decisions_cache)
        for c in chain:
            visited.add(c)
        if len(chain) >= min_chain:
            chains.append(chain)

    nodes_to_remove = set()
    edges_to_remove = set()   # set of (src, dst)
    edge_rewrites = {}        # (old_src, dst) -> new_src
    summary_updates = {}      # node_id -> new attrs dict

    for chain in chains:
        d0 = chain[0]
        dn = chain[-1]
        prefix0, var, op, val0 = decisions_cache[d0]
        prefixn, _, _, valn = decisions_cache[dn]

        # Remove interior nodes D1..Dn (everything except D0, which is reused
        # as the summary node id).
        for nid in chain[1:]:
            nodes_to_remove.add(nid)

        # For every chain node except Dn, drop its fail-leaf child and the
        # edge to the next chain node.
        for i, nid in enumerate(chain[:-1]):
            for k in g.children.get(nid, []):
                if g.is_fail_leaf(k):
                    nodes_to_remove.add(k)
                    edges_to_remove.add((nid, k))
            nxt = chain[i + 1]
            edges_to_remove.add((nid, nxt))

        # Dn's real children (fail leaf and/or actual branch) survive, but
        # the edges must now originate from D0 (the reused summary id).
        for k in g.children.get(dn, []):
            edge_rewrites[(dn, k)] = d0

        count = len(chain)
        label = f'{prefix0}..{prefixn}: {var}{op}[{val0}..{valn}] ({ "~" + "{:.2E}".format(Decimal(count)) if count > 1000 else count } values tested)'
        summary_updates[d0] = OrderedDict([
            ('label', f'"{label}"'),
            ('shape', 'circle'),
            ('style', 'filled'),
            ('fillcolor', 'yellow'),
        ])

        if verbose:
            print(f'Collapsed {count} nodes: {var}{op}[{val0}..{valn}] '
                  f'({prefix0}..{prefixn}) -> node {d0}', file=sys.stderr)

    # Regenerate output, preserving original statement order.
    out_lines = []
    for stmt in g.statements:
        if stmt['kind'] == 'node':
            nid = stmt['id']
            if nid in nodes_to_remove:
                continue
            attrs = summary_updates.get(nid, g.nodes[nid])
            out_lines.append(f'\t{nid} [{format_attrs(attrs)}];')
        elif stmt['kind'] == 'edge':
            src, dst = stmt['src'], stmt['dst']
            if (src, dst) in edges_to_remove:
                continue
            if (src, dst) in edge_rewrites:
                # Edge survives but its source node was replaced by the
                # summary node, so redirect it even though `src` is in
                # nodes_to_remove.
                new_src = edge_rewrites[(src, dst)]
                out_lines.append(f'\t{new_src} -> {dst};')
                continue
            if src in nodes_to_remove or dst in nodes_to_remove:
                continue
            out_lines.append(f'\t{src} -> {dst};')
        else:
            out_lines.append(stmt['text'])

    return '\n'.join(out_lines) + '\n', len(chains)


def main():
    parser = argparse.ArgumentParser(
        description='Collapse consecutive value-enumeration chains in a Choco '
                    'search-tree .dot file into single yellow summary nodes.')
    parser.add_argument('input', help='Path to the folder containing the choco_solver_logs.dot file')
    parser.add_argument('--output', help='Path to write the collapsed .dot file')
    parser.add_argument('--min-chain', type=int, default=3,
                         help='Minimum number of consecutive values tested '
                              'before collapsing (default: 3)')
    parser.add_argument('--quiet', action='store_true',
                         help='Suppress the per-chain summary printed to stderr')
    parser.add_argument('--no-repair', action='store_true',
                         help='Do not attempt to auto-repair a truncated/'
                              'unclosed input file (missing trailing "}", '
                              'e.g. because the solving run was interrupted)')
    args = parser.parse_args()

    args.input += "choco_solver_logs.dot"
    
    if not args.output:
        # if no output is specified, print to the same folder as the input with an appended name.
        original = Path(args.input)
        args.output = original.with_name("Collapsed_" + original.name)

    with open(args.input, 'r', encoding='utf-8') as f:
        text = f.read()

    if not args.no_repair:
        text, dropped, added = repair_truncated_dot(text)
        if dropped:
            print('Warning: last line of input looked cut off mid-write; '
                  'it was dropped.', file=sys.stderr)
        if added:
            print(f'Warning: input was missing {added} closing brace(s) '
                  f'("}}"); this usually means the solving run was '
                  f'interrupted before Choco finished writing the file. '
                  f'Added automatically.', file=sys.stderr)

    g = DotGraph()
    g.load(text)
    result, n_chains = collapse(g, args.min_chain, verbose=not args.quiet)

    with open(args.output, 'w', encoding='utf-8') as f:
        f.write(result)

    print(f'Done. Collapsed {n_chains} chain(s) (min-chain={args.min_chain}). ', file=sys.stderr)


if __name__ == '__main__':
    main()