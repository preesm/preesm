#ifndef JOB_LIST
#define JOB_LIST

job_descriptor jobs[JOB_NUMBER] = {
    /*Sensor*/{0,10000,sensor,{},{Sensoro1Gen_inti1,Sensoro2Gen_inti2,Sensoro3s_SensorPa_0},{}},
    /*Sensor2*/{3,100,sensor2,{},{Sensor2o1ParallelT_0},{}},
    /*Gen_int*/{4,10000,gen_int,{0},{Sensoro1Gen_inti1,Sensoro2Gen_inti2,Gen_into1Copyi1,Gen_into2s_Gen_int_0},{}},
    /*ParallelTest*/{7,10000,parallel,{3,0},{Sensoro3s_SensorPa_0,ParallelTesto1Para_0},{}},
    /*Copy*/{8,10000,copy,{4},{Gen_into1Copyi1,Copyo1s_CopyActuat_0},{}},
    /*ParallelTest2*/{11,10000,parallel,{7},{ParallelTesto1Para_0,ParallelTest2o1Par_0},{}},
    /*ParallelTest3*/{12,100,parallel,{11},{ParallelTest2o1Par_0,ParallelTest3o1Act_0},{}},
    /*Actuator*/{13,10000,actuator,{12,8,4},{Copyo1s_CopyActuat_0,Gen_into2s_Gen_int_0,ParallelTest3o1Act_0},{1000/*size*/}}
};

#endif
