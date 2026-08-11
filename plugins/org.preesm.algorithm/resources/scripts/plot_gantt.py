import matplotlib.pyplot as plt
import matplotlib.colors as mcolors
from matplotlib.ticker import MaxNLocator
import numpy as np
import sys
import os
import importlib.util
import math
import matplotlib

# on se débarasse des fontes type 3, haïes par DATE
matplotlib.rcParams['pdf.fonttype'] = 42

spec = importlib.util.spec_from_file_location("gantt_data", os.path.abspath(sys.argv[1]))
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

output_name = os.path.abspath(sys.argv[1]).split("/")[-1].split(".")[0].replace("gantt_data_", "")

tasks = module.tasks
tasks.sort(key=lambda t: t["name"], reverse=True)
# tasks.sort(key=lambda t: t["start"], reverse=True)

hyperperiod = module.hyperperiod

# One base color per task
cmap = plt.get_cmap("tab20")

# ---------------------------------------------------------
# Plot Gantt chart
# ---------------------------------------------------------
num_hyperperiods = 3
# phase transitoire : entre 0 et le firing du dernier acteur à démarrer
periodic_phase_start_cycle = max([t["start"] for t in tasks])
latency = max([(t["start"] + (t["RC"] - 1) * t["II"] + t["duration"]) for t in tasks])

cycle_limit = periodic_phase_start_cycle + hyperperiod * num_hyperperiods

fig, ax = plt.subplots(figsize=(12, len(tasks) + 1))


# la latence du graphe
ax.axvline(
    latency,
    linestyle='dashed',
    linewidth=1.3,
    color="black"
)

# les périodes
for x in range(num_hyperperiods):
    ax.axvline(
    periodic_phase_start_cycle + hyperperiod * x,
    linestyle='dashdot',
    linewidth=0.9,
    color="black"
)

# Beyond this many releases in a single lane, individual bars would be
# packed sub-pixel-tight anyway (gap between consecutive same-lane releases
# is always < period, by construction -- see below) -- draw one merged
# block per lane instead of thousands of indistinguishable ones.
MAX_INDIVIDUAL_RELEASES_PER_LANE = 400

for row, task in enumerate(tasks):
    # Task-level latency: how long one firing occupies the resource.
    # Falls back to "duration" if no explicit "latency" is given.
    task_latency = task["duration"]
    period = task["period"]

    # Number of pipeline lanes needed so overlapping firings don't
    # visually collide: ceil(latency / period), min 1.
    num_lanes = max(1, math.ceil(task_latency / period))
    lane_height = 0.6 / num_lanes

    # Release times: release_k = start + k * period. Get the count directly
    # by division rather than looping cycle-by-cycle up to cycle_limit.
    n_releases = max(0, (cycle_limit - task["start"]) // task["period"] + 1)

    color = cmap(row % cmap.N)

    # A bolder/darker shade of the task's own color, used to highlight the
    # consuming and producing phases against the normal (lighter) bar.
    bold_color = tuple(c * 0.25 for c in mcolors.to_rgb(color))
    clear_color = tuple(c * 0.75 for c in mcolors.to_rgb(color))

    # Same convention as plot_fifos_prod_cons.py: consuming happens as EARLY
    # as possible (first cycles of the firing), producing as LATE as
    # possible (last cycles, ending exactly when the firing completes).
    cons_span = max(task.get("cons_rates", {}).values(), default=0)
    prod_span = max(task.get("prod_rates", {}).values(), default=0)

    if n_releases == 0:
        continue

    releases_per_lane = math.ceil(n_releases / num_lanes)

    if releases_per_lane <= MAX_INDIVIDUAL_RELEASES_PER_LANE:
        # Few enough releases to draw (and highlight) individually.
        releases = task["start"] + task["period"] * np.arange(n_releases)
        # Lane assignment: arrivals are uniformly spaced and each occupies a
        # lane for exactly `task_latency`. For uniform arrivals, plain
        # round-robin (release i -> lane i % num_lanes) is provably conflict-free
        # with num_lanes = ceil(task_latency / period): release i reuses the lane
        # last held by release i - num_lanes, which by that definition has
        # already finished (num_lanes * period >= task_latency).
        lanes_used = np.arange(n_releases) % num_lanes
        ys = row - 0.3 + lanes_used * lane_height
        half = lane_height / 2

        # One barh call per phase-type draws every release at once, instead
        # of one call per release.
        ax.barh(ys, task_latency, left=releases, height=lane_height,
                color=color, edgecolor="black", align="edge")
        if cons_span:
            ax.barh(ys + half, cons_span, left=releases, height=half,
                    color=clear_color, edgecolor="black", align="edge", hatch="/"*3)
        if prod_span:
            ax.barh(ys, prod_span, left=releases + task_latency - prod_span, height=half,
                    color=bold_color, edgecolor="black", align="edge", hatch="."*3)
    else:
        # Too many releases per lane to draw (or usefully see) individually.
        # Consecutive same-lane releases are spaced num_lanes*period apart
        # and each occupies task_latency; since num_lanes = ceil(task_latency
        # / period), the gap between them is num_lanes*period - task_latency,
        # which is < period by construction -- negligible at this density.
        # So: get each lane's first/last release by arithmetic (no array of
        # n_releases ever gets built, however large n_releases is) and draw
        # one merged rectangle per lane. Per-release consuming/producing
        # highlights are skipped here -- individual bursts aren't visually
        # resolvable at this density anyway.
        lane_lefts = []
        lane_widths = []
        lane_ys = []
        for lane in range(num_lanes):
            count_in_lane = (n_releases - 1 - lane) // num_lanes + 1
            if count_in_lane <= 0:
                continue
            first_t = task["start"] + lane * task["period"]
            last_t = task["start"] + (lane + (count_in_lane - 1) * num_lanes) * task["period"]
            lane_lefts.append(first_t)
            lane_widths.append(last_t + task_latency - first_t)
            lane_ys.append(row - 0.3 + lane * lane_height)
        # Distinct hatch ("|" = many repeated events, vs. "/" consuming and
        # "." producing used elsewhere) flags this at a glance as a condensed
        # view, not a single long firing. Cheap now: only num_lanes patches,
        # not one per release.
        ax.barh(lane_ys, lane_widths, left=lane_lefts, height=lane_height,
                color=color, edgecolor="black", align="edge", hatch="||")
        # Exact numbers, once per task (not per lane) so it stays readable
        # regardless of how many lanes or how extreme the repetition is.
        ax.text(min(lane_lefts), row + 0.32, f" period={period}  (\u00d7{n_releases} firings)",
                fontsize=7, va="bottom", ha="left", color="black",
                bbox=dict(facecolor="white", alpha=0.75, edgecolor="none", pad=1))
# ---------------------------------------------------------
# Formatting
# ---------------------------------------------------------
ax.set_yticks(range(len(tasks)))
ax.set_yticklabels([t["name"] for t in tasks])

ax.xaxis.set_major_locator(MaxNLocator(integer=True))
ax.set_xlabel("Clock Cycles")
ax.set_ylabel("Tasks")
ax.set_title("Periodic Task Schedule")

ax.grid(True, axis="x", linestyle="--", alpha=0.3)

plt.xlabel("dashed top half: consuming   |   dotted bottom half: producing   |   horizontal hatch : high-repetition actor \ndashdotted line : periods   |   bold dashed line : latency", loc="left")

# we crop the graph past the number of full periods we want to display
plt.xlim(left=0, right=cycle_limit)

plt.tight_layout()
combined_path = os.path.join(os.path.dirname(sys.argv[1]), "gantt_" + output_name + ".pdf")
fig.savefig(combined_path, dpi=150)
print(f"wrote {combined_path}")
#plt.show()
