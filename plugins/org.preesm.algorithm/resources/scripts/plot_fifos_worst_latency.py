#!/usr/bin/env python3
"""
plot_fifos_worst_latency.py

Plots the cumulative amount of data (tokens) flowing through each FIFO
buffer over time, alongside the physical execution schedules of the
producer and consumer actors.

MODEL:
- Consumers consume `cons_rate` tokens (1 per cycle) at the BEGINNING of their firing.
- Producers produce `prod_rate` tokens (1 per cycle) at the END of their firing (latency).
- Rates can be specified per edge using "prod_rates" and "cons_rates" dicts in the input file.
"""

import argparse
import math
import os
import sys
import numpy as np
import matplotlib.gridspec as gridspec
from matplotlib.ticker import MaxNLocator
import matplotlib

# on se débarasse des fontes type 3, haïes par DATE
matplotlib.rcParams['pdf.fonttype'] = 42

MAX_INDIVIDUAL_RELEASES = 400
NBHYPERPERIODS = 2

def load_tasks(path):
    with open(path, "r") as f:
        src = f.read()
    ns = {}
    try:
        exec(compile(src, path, "exec"), ns)
    except SyntaxError as e:
        sys.exit(f"Could not parse {path} as Python: {e}")
    if "tasks" not in ns:
        sys.exit(f"{path} must define a 'tasks' variable.")

    tasks = ns["tasks"]
    hyperperiod = ns.get("hyperperiod")
    by_name = {t["name"]: t for t in tasks}

    fifos = []
    for t in tasks:
        u = t["name"]
        prod_rates = t.get("prod_rates", {})
        # Initial tokens are keyed the same way as prod_rates: an edge's
        # initial-token count is declared on its SOURCE task, by the
        # target's name -- i.e. "this task's outgoing edge to X starts
        # with N tokens already in it" (matches SDF delay/cycle-breaking
        # semantics: exactly one edge in a cycle needs initial tokens).
        initial_tokens = t.get("initial-tokens", {})
        
        for v in t.get("successors", []):
            if v not in by_name:
                print(f"warning: {u} lists unknown successor '{v}'")
                continue
                
            task_v = by_name[v]
            cons_rates = task_v.get("cons_rates", {})
            
            prod_rate = prod_rates.get(v, 1)
            cons_rate = cons_rates.get(u, 1)
            init_tok = initial_tokens.get(v, 0)
            
            fifos.append((u, v, prod_rate, cons_rate, init_tok))

    return tasks, by_name, fifos, hyperperiod

def get_duration(task):
    """How long a single firing actually takes to compute (its latency)."""
    return task.get("duration", task.get("latency"))

def firing_starts(task, hyperperiod, nbIterations):
    """Start times at which this actor fires."""
    times = []
    p = task.get("period")
    rc = task.get("RC", 1)
    # The true repeating interval is whichever is larger: the hyperperiod or the RC burst span
    step_interval = max(hyperperiod, rc * p)

    for it in range(nbIterations):
        offset = it * step_interval
        for i in range(rc):
            t = task["start"] + offset + i * p
            times.append(t)
    return times


def cumulative_ramps_streamed(event_intervals, t_lo, t_hi, initial=0):
    """Builds continuous diagonal ramps for streamed events over (t_start, t_end) intervals.

    `initial` is a constant token count already present before t_lo (e.g. an
    SDF edge's initial tokens / delay), used to bootstrap cyclic dependencies.
    """
    if not event_intervals:
        return [t_lo, t_hi], [initial, initial]
    
    sorted_events = sorted(event_intervals, key=lambda x: x[0])
    
    xs = [t_lo]
    ys = [initial]
    cumulative = initial
    
    for t_start, t_end in sorted_events:
        # Hold previous token level flat if there is a gap before the next token starts
        if xs[-1] < t_start:
            xs.append(t_start)
            ys.append(cumulative)
        
        # Ramp linearly across [t_start, t_end]
        cumulative += 1
        xs.append(t_end)
        ys.append(cumulative)
        
    xs.append(t_hi)
    ys.append(cumulative)
    return xs, ys

def draw_task_bars(ax, y_pos, event_starts, task, base_color):
    """Draws Gantt-chart style execution blocks for an actor with pipelining support."""
    if not event_starts: 
        return
    import matplotlib.colors as mcolors
    
    task_latency = get_duration(task)
    period = task.get("period", task.get("II", 1))

    num_lanes = max(1, math.ceil(task_latency / period)) if period > 0 else 1
    lane_height = min(0.6 / num_lanes, 0.3)

    n_releases = len(event_starts)
    
    # Check if firings exceed the individual release threshold
    if n_releases <= MAX_INDIVIDUAL_RELEASES:
        bold_color = tuple(c * 0.25 for c in mcolors.to_rgb(base_color))
        clear_color = tuple(c * 0.75 for c in mcolors.to_rgb(base_color))
        
        cons_span = max(task.get("cons_rates", {}).values(), default=0)
        prod_span = max(task.get("prod_rates", {}).values(), default=0)
        
        releases = np.array(event_starts)
        lanes_used = np.arange(n_releases) % num_lanes
        ys = (y_pos - 0.3) + lanes_used * lane_height
        half = lane_height / 2
        
        # Main execution bar
        ax.barh(ys, task_latency, left=releases, height=lane_height, 
                color=base_color, edgecolor="black", align="edge")
        
        # Bold start edge
        ax.vlines(releases, ys, ys + lane_height, color="black", linewidth=2.0, zorder=3)
        
    else:
        # High-repetition condensed rendering (matches gantt.py logic)
        lane_lefts, lane_widths, lane_ys = [], [], []
        for lane in range(num_lanes):
            count_in_lane = (n_releases - 1 - lane) // num_lanes + 1
            if count_in_lane <= 0:
                continue
            first_t = event_starts[lane]
            last_t = event_starts[lane + (count_in_lane - 1) * num_lanes]
            lane_lefts.append(first_t)
            lane_widths.append(last_t + task_latency - first_t)
            lane_ys.append((y_pos - 0.3) + lane * lane_height)
        
        ax.barh(lane_ys, lane_widths, left=lane_lefts, height=lane_height,
                color=base_color, edgecolor="black", align="edge", hatch="||")
        ax.vlines(lane_lefts, lane_ys, [y + lane_height for y in lane_ys], color="black", linewidth=2.0, zorder=3)

def render_fifo(ax_task, ax_fifo, producer, consumer, prod_rate, cons_rate, by_name, starts, color_map, global_hyperperiod, initial_tokens=0, check_underflow=False):
    task_producer = by_name[producer]
    task_consumer = by_name[consumer]
    duration_producer = get_duration(task_producer)
    duration_consumer = get_duration(task_consumer)

    # Calculate local FIFO hyperperiod (LCM of producer and consumer periods)
    p_prod = int(task_producer.get("period"))
    p_cons = int(task_consumer.get("period"))
    fifo_hyperperiod = math.lcm(p_prod, p_cons) if (p_prod and p_cons) else global_hyperperiod

    # Calculate local bounds capped strictly to 3 local hyperperiods
    periodic_phase_start = task_consumer["start"]
    fifo_t_max = periodic_phase_start + (NBHYPERPERIODS * fifo_hyperperiod if fifo_hyperperiod else 100)
    
    pad = (fifo_t_max - periodic_phase_start) * 0.02 or 1
    t_lo = task_producer["start"]
    t_hi = fifo_t_max + pad

    # -------------------------------------------------------------
    # 1. TOP SUBPLOT: Task Executions
    # -------------------------------------------------------------
    ax_task.set_title(f"FIFO: {producer} \u2192 {consumer}", fontsize=10, fontweight="bold")
    ax_task.set_ylim(-2, 1.6)
    ax_task.set_yticks([0, 1])
    ax_task.set_yticklabels([f"T={p_cons}\nL={duration_consumer}", f"T={p_prod}\nL={duration_producer}"], fontsize=8)
    ax_task.grid(alpha=0.2, axis="x")
    ax_task.tick_params(labelbottom=False)

    draw_task_bars(ax_task, 1, starts[producer], task_producer, "tab:red")
    draw_task_bars(ax_task, 0, starts[consumer], task_consumer, "tab:blue")

    # -------------------------------------------------------------
    # 2. BOTTOM SUBPLOT: Cumulative Tokens
    # -------------------------------------------------------------
    prod_events = []
    cons_events = []

    # Expand Production: record (t_start, t_end) for 1 cycle per token
    for t in starts[producer]:
        if t <= fifo_t_max:
            for i in range(int(prod_rate)):
                t_start = t + duration_producer - int(prod_rate) + i
                prod_events.append((t_start, t_start + 1))

    # Expand Consumption: record (t_start, t_end) for 1 cycle per token
    for t in starts[consumer]:
        if t <= fifo_t_max:
            for i in range(int(cons_rate)):
                t_start = t + i
                cons_events.append((t_start, t_start + 1))

    # Plot tokens using ax_fifo.plot (creates diagonal lines across each cycle)
    pxs, pys = cumulative_ramps_streamed(prod_events, t_lo, t_hi, initial=initial_tokens)
    prod_label = f"Producer {producer} (Rate: {prod_rate})"
    if initial_tokens:
        prod_label += f" [+{initial_tokens} initial]"
    ax_fifo.plot([0] + pxs, [0] + pys, label=prod_label, color="tab:red", linewidth=1.5,  linestyle=(0, (2, 4)))

    cxs, cys = cumulative_ramps_streamed(cons_events, t_lo, t_hi)
    ax_fifo.plot([0] + cxs, [0] + cys, label=f"Consumer {consumer} (Rate: {cons_rate})", color="tab:blue", linewidth=1.5,  linestyle=(3, (2, 4)))

    if check_underflow:
        # Guarantee strictly increasing arrays for numpy.interp
        p_idx = np.argsort(pxs)
        c_idx = np.argsort(cxs)
        all_xs = np.unique(np.concatenate((pxs, cxs)))
        
        p_interp = np.interp(all_xs, np.array(pxs)[p_idx], np.array(pys)[p_idx])
        c_interp = np.interp(all_xs, np.array(cxs)[c_idx], np.array(cys)[c_idx])
        
        if np.any(c_interp > p_interp + 1e-9):
            print(f"[!] WARNING: Underflow detected in FIFO {producer} -> {consumer}. Cumulative consumption exceeds production.")

    if fifo_hyperperiod:
        g = periodic_phase_start
        while g <= t_hi:
            ax_task.axvline(g, color="gray", linestyle=":", linewidth=0.5)
            ax_fifo.axvline(g, color="gray", linestyle=":", linewidth=0.5)
            g += fifo_hyperperiod

    ax_fifo.set_xlabel("Time (cycles)")
    ax_fifo.set_ylabel("Tokens")
    ax_fifo.grid(alpha=0.2)
    ax_fifo.legend(fontsize=8, loc="upper left")
    
    # Align X limits specifically for this FIFO
    ax_fifo.set_xlim(0, t_hi)
    
    # Force whole integer ticks
    ax_fifo.yaxis.set_major_locator(MaxNLocator(integer=True))
    ax_fifo.xaxis.set_major_locator(MaxNLocator(integer=True))

def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("input", help="path to the python file defining `tasks`")
    parser.add_argument("--outdir", help="directory to write PDF output(s)")
    parser.add_argument("--max-per-page", type=int, default=9, help="max FIFOs to plot per page (default 9)")
    parser.add_argument("--check-underflow", action="store_true", help="Check if cumulative consumption is ever superior to cumulative production")
    args = parser.parse_args()

    if not args.outdir:
        args.outdir = os.path.dirname(args.input) or "."

    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    tasks, by_name, fifos, hyperperiod = load_tasks(args.input)
    if not fifos:
        sys.exit("No FIFOs (edges) found. Make sure actors define 'successors'.")

    os.makedirs(args.outdir, exist_ok=True)

    # Establish consistent colors
    tasks_sorted = sorted(tasks, key=lambda x: x["name"], reverse=True)
    cmap = plt.get_cmap("tab20")
    color_map = {t["name"]: cmap(i % cmap.N) for i, t in enumerate(tasks_sorted)}

    starts = {t["name"]: firing_starts(t, hyperperiod, NBHYPERPERIODS) for t in tasks}
    
    # Starts are generated up to 3 global hyperperiods to ensure full coverage

    # Pagination logic
    n = len(fifos)
    chunk_size = args.max_per_page
    chunks = [fifos[i:i + chunk_size] for i in range(0, n, chunk_size)]
    
    for chunk_idx, chunk in enumerate(chunks):
        ncols = min(3, len(chunk))
        nrows = math.ceil(len(chunk) / ncols)
        
        # Create figure
        fig = plt.figure(figsize=(6.5 * ncols, 5.0 * nrows))
        
        # Outer grid for the grid of FIFOs
        outer_grid = gridspec.GridSpec(nrows, ncols, figure=fig, hspace=0.35, wspace=0.25)

        for idx, (u, v, prod_rate, cons_rate, init_tok) in enumerate(chunk):
            r, c = divmod(idx, ncols)
            
            # Inner grid: 2 rows (top: tasks, bottom: tokens) with a 1:2.5 height ratio
            inner_grid = gridspec.GridSpecFromSubplotSpec(
                2, 1, subplot_spec=outer_grid[r, c], 
                height_ratios=[1, 2.5], hspace=0.08
            )
            
            ax_task = fig.add_subplot(inner_grid[0])
            ax_fifo = fig.add_subplot(inner_grid[1], sharex=ax_task)
            
            render_fifo(ax_task, ax_fifo, u, v, prod_rate, cons_rate, by_name, starts, color_map, hyperperiod, init_tok, args.check_underflow)

        file_suffix = f"_{chunk_idx+1}" if len(chunks) > 1 else ""
        combined_path = os.path.join(args.outdir, f"fifo_buffers__worst_latency{file_suffix}.pdf")
        fig.savefig(combined_path, bbox_inches="tight")
        print(f"wrote {combined_path}")
        plt.close(fig)

if __name__ == "__main__":
    main()
