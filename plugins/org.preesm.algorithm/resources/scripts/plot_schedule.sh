#!/bin/bash
folder=$(dirname "$(realpath "$1")")

echo ""
echo "Collapsing dot file chains"
python3 collapse_dot_chains.py ${folder}/ && echo "done" || echo "failed"

echo ""
echo "Converting dot file to svg"
dot -Tsvg $folder/Collapsed_choco_solver_logs.dot > $folder/recherche.svg && echo "done" || echo "failed"

echo ""
echo "Plotting gantt"
python3 plot_gantt.py $1 && echo "done"  || echo "failed"

echo ""
echo "Plotting fifo buffers for worst latency"
python3 plot_fifos_worst_latency.py $1 --check-underflow && echo "done"  || echo "failed"

echo ""
echo "Plotting fifo buffers for worst buffer sizes"
python3 plot_fifos_worst_buffer_sizes.py $1 --check-underflow && echo "done"  || echo "failed"

