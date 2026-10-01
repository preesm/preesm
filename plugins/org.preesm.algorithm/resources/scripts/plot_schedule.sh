#!/bin/bash
folder="" 
if [ "$#" -ne 1 ]; then
    folder=$(ls -d */)
else
	folder=$(dirname "$(realpath "$1")")
fi




echo ""
echo "Collapsing dot file chains"
python3 collapse_dot_chains.py ${folder}/ && echo "done" || echo "failed"

echo ""
echo "Converting dot file to svg"
dot -Tsvg $folder/Collapsed_choco_solver_logs.dot > $folder/recherche.svg && echo "done" || echo "failed"

echo ""
echo "Plotting gantt"
python3 plot_gantt.py $folder/gantt_data.py && echo "done"  || echo "failed"

echo ""
echo "profiling choco variables"
python3 choco_log_stats.py -a ${folder}/choco_solver_logs.txt >> profiled_choco_variables.txt -o profiled_choco_variables.csv && echo "done"  || echo "failed"

echo ""
echo "Plotting fifo buffers for worst latency"
python3 plot_fifos_worst_latency.py $folder/gantt_data.py --check-underflow && echo "done"  || echo "failed"

echo ""
echo "Plotting fifo buffers for worst buffer sizes"
python3 plot_fifos_worst_buffer_sizes.py $folder/gantt_data.py --check-underflow && echo "done"  || echo "failed"

