#!/usr/bin/env python3
"""Count variable updates per phase in a Choco solver log.

Streams the file in binary, no regex on the hot path.
Usage:
    python3 choco_log_stats.py solver.log [-o stats.csv] [-n 30] [-a]
"""

import argparse
import io
from collections import Counter

# Event tokens emitted by Choco's propagation trace.
EVENTS = (b"INSTANTIATE", b"DECUPP", b"INCLOW", b"REMOVE", b"INSTANTIATED")

HEADER_MARKER = b"All vars :"
PROP_MARKER = b"Starting initial propagation"
SEARCH_MARKER = b"Starting schedule search"

ARROW = b" -> "
SEP = b" : "


def strip_ansi_prefix(tok: bytes) -> bytes:
    """Lines can start with a leftover ANSI reset, e.g. b'\\x1b[0mINSTANTIATE'."""
    i = tok.rfind(b"m")
    return tok[i + 1:] if i != -1 else tok


def header_name(line: bytes):
    """Extract the variable name from a declaration in the 'All vars :' block.

    'period_X_var = {129..10358}'          -> 'period_X_var'
    '99.(period_Y_var) + 0[99,10197]'      -> '99.(period_Y_var) + 0'
    """
    line = line.strip()
    if not line:
        return None
    name, sep, _ = line.partition(b" = ")
    if sep:
        return name.strip()
    if line.endswith(b"]"):
        i = line.rfind(b"[")
        if i > 0:
            return line[:i].strip()
    return None


def parse(path, bufsize=1 << 22):
    # counts[phase][(event, var)] -> int ; phase in {0: pre, 1: propagation, 2: search}
    counts = [Counter(), Counter(), Counter()]
    declared = []          # names from the 'All vars :' block, in file order
    seen = set()
    in_header = False

    with open(path, "rb") as raw:
        f = io.BufferedReader(raw, buffer_size=bufsize)
        cur = counts[0]
        for line in f:
            head, arrow, _ = line.partition(ARROW)
            if not arrow:
                # Only phase markers and header declarations matter here.
                if PROP_MARKER in line:
                    in_header = False
                    cur = counts[1]
                elif SEARCH_MARKER in line:
                    in_header = False
                    cur = counts[2]
                elif HEADER_MARKER in line:
                    in_header = True
                elif in_header:
                    name = header_name(line)
                    if name and name not in seen:
                        seen.add(name)
                        declared.append(name)
                continue

            ev, sep, var = head.partition(SEP)
            if not sep:
                continue
            ev = strip_ansi_prefix(ev.lstrip())
            if ev not in EVENTS:
                continue
            cur[(ev, var.strip())] += 1

    return counts, declared


NAMES = ("before", "initial_propagation", "schedule_search")


def aggregate(c):
    per_var, per_event, breakdown = Counter(), Counter(), {}
    for (ev, var), n in c.items():
        per_var[var] += n
        per_event[ev] += n
        breakdown.setdefault(var, []).append((ev, n))
    return per_var, per_event, breakdown


def report(counts, declared, top, show_all, out_csv):
    for phase_idx in (1, 2):
        c = counts[phase_idx]
        per_var, per_event, breakdown = aggregate(c)

        total = sum(per_event.values())
        print(f"\n=== {NAMES[phase_idx]} : {total} updates, "
              f"{len(per_var)} distinct vars ===")
        print("  by event: " + ", ".join(
            f"{e.decode()}={n}" for e, n in per_event.most_common()))

        if show_all:
            rows = per_var.most_common()
            # Declared but never touched in this phase -> count 0.
            untouched = [v for v in declared if v not in per_var]
            rows += [(v, 0) for v in untouched]
            print(f"  all vars ({len(rows)}, incl. {len(untouched)} never updated):")
        else:
            rows = per_var.most_common(top)
            print(f"  top {top} vars:")

        for var, n in rows:
            detail = ", ".join(
                f"{e.decode()}:{k}"
                for e, k in sorted(breakdown.get(var, ()), key=lambda t: -t[1])
            )
            print(f"    {n:>10}  {var.decode('utf-8', 'replace')}"
                  + (f"   [{detail}]" if detail else ""))

    if out_csv:
        with open(out_csv, "w", encoding="utf-8") as fh:
            fh.write("phase,event,variable,count\n")
            for idx in (1, 2):
                for (ev, var), n in counts[idx].most_common():
                    v = var.decode("utf-8", "replace").replace('"', '""')
                    fh.write(f'{NAMES[idx]},{ev.decode()},"{v}",{n}\n')
                if show_all:
                    touched = {var for _, var in counts[idx]}
                    for var in declared:
                        if var not in touched:
                            v = var.decode("utf-8", "replace").replace('"', '""')
                            fh.write(f'{NAMES[idx]},NONE,"{v}",0\n')
        print(f"\nwritten: {out_csv}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("log")
    ap.add_argument("-o", "--csv", default=None)
    ap.add_argument("-n", "--top", type=int, default=25)
    ap.add_argument("-a", "--all", action="store_true",
                    help="list every variable, including those declared in the "
                         "'All vars :' block that were never updated")
    a = ap.parse_args()
    counts, declared = parse(a.log)
    report(counts, declared, a.top, a.all, a.csv)


if __name__ == "__main__":
    main()