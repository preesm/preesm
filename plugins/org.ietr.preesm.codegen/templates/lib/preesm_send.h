/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2012 - 2013)
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
#ifndef SEND_H
#define SEND_H

#include <systemc>

using namespace std;

template<class T, int input_size = 1>
SC_MODULE (preesm_send) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_out<T> out;
	sc_core::sc_fifo_in<T> in;

	void enable() {
		int i;
		bool isEnable = true;
		while (1) {
			isEnable = true;
			enable_port.write(false);
			sc_core::wait(10, sc_core::SC_NS);
			isEnable &= (in.num_available() == input_size);
			enable_port.write(isEnable);
			sc_core::wait(10, sc_core::SC_NS);
		}

	}

	void invoke() {
		int i, j;
		while (1) {
			wait(invoke_port.posedge_event());
			cout << "invoking actor: " << "preesm_send" << endl;
			for (j = 0; j < input_size; j++) {
				out.write(in.read());
			}
		}
	}

	SC_CTOR(preesm_send) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
