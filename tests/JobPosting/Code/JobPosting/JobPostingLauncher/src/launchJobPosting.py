import os

seq1 = [1, 2, 3, 4]
   
for id in seq1:     
    args = "-slave -id %d" % (id)
    os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args)
      
args = "-master"     
os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args)
                   
