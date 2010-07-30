#ifndef JOB_LIST
#define JOB_LIST

job_descriptor jobs[JOB_NUMBER] = {
    /*Sensor*/{0,10000,sensor,0,{},{Sensoro1Gen_inti1,Sensoro2Gen_inti2,Sensoro3s_SensorPa_0},{}},
    /*Gen_int*/{3,10000,gen_int,1,{0},{Sensoro1Gen_inti1,Sensoro2Gen_inti2,Gen_into1Copyi1,Gen_into2s_Gen_int_0},{}},
    /*Copy*/{6,10000,copy,1,{3},{Gen_into1Copyi1,Copyo1s_CopyActuat_0},{}},
    /*Sensor2*/{9,100,sensor2,0,{},{Sensor2o1ParallelT_0},{}},
    /*ParallelTest*/{10,10000,parallel,2,{9,0},{Sensoro3s_SensorPa_0,ParallelTesto1Para_0},{}},
    /*ParallelTest2*/{11,10000,parallel,1,{10},{ParallelTesto1Para_0,ParallelTest2o1Par_0},{}},
    /*ParallelTest3*/{12,100,parallel,1,{11},{ParallelTest2o1Par_0,ParallelTest3o1Act_0},{}},
    /*Actuator*/{13,10000,actuator,3,{12,6,3},{Copyo1s_CopyActuat_0,Gen_into2s_Gen_int_0,ParallelTest3o1Act_0},{1000/*size*/}}
};

#endif
