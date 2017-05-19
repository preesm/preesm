/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Maxime Pelcat <Maxime.Pelcat@insa-rennes.fr> (2009 - 2010)
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
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
