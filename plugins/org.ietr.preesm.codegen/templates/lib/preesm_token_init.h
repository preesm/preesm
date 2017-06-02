/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2012)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
#ifndef PREESM_TOKEN_INIT_H
#define PREESM_TOKEN_INIT_H

#include <stdio.h>
#include <stdlib.h>
#include <systemc>

using namespace std ;

template<class T, int output_size = 1>
SC_MODULE (preesm_token_init) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_out<T> init_out;
	sc_core::sc_fifo_in<T> init_in;

	void enable() {
		while (1) {
			sc_core::wait();
		}
	}
	void invoke() {
		int i ;
		T * dummy = (T*) malloc(sizeof(T));
		for (i = 0; i < output_size; i++) {
			init_out.write(*dummy);
		}
		while (1) {
			for (i = 0; i < output_size; i++) {
				init_out.write(init_in.read());
			}
		}
	}

	SC_CTOR(preesm_token_init) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
