/*******************************************************************************
 * Copyright or © or Copr. 2009 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jérôme Croizer <jerome.croizer@insa-rennes.fr> (2009)
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
#ifndef TESTCOMSOURCE_H_
#define TESTCOMSOURCE_H_

extern void sensor_init(char* o1, char* o2);
extern void parallel_init(char* i1, char* o1);
extern void gen_int_init(char* i1,char* o1,char* o2);
extern void copy_init(char* i1, char* o1);
extern void actuator_init(char* i1,char* i2,char* i3);
extern void sensor(char* o1, char* o2, char* o3);
extern void parallel(char* i1, char* o1);
extern void gen_int(char* i1,char* i2,char* o1,char* o2);
extern void copy(char* i1, char* o1);
extern void actuator(char* i1,char* i2,char* i3,int size);
#endif