import os, signal

seq1 = [1, 2, 3, 4]
slavepid = []
   
args = "-master"     
masterpid = os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args)

os.system('pause')

for id in seq1:     
    args = "-slave -id %d" % (id)
    slavepid.append(os.spawnl(os.P_NOWAIT, "../../Visual/debug/Queues.exe", args))

os.system('pause')
