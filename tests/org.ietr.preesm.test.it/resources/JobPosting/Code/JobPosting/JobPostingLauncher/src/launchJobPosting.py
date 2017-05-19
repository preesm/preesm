import os, signal

seq1 = [0, 1, 2, 3]
slavepid = []
   
args = "-master -nbSlaves 4"     
masterpid = os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args)

os.system('pause')

for id in seq1:     
    args = "-slave -id %d" % (id)
    slavepid.append(os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args))

os.system('pause')

