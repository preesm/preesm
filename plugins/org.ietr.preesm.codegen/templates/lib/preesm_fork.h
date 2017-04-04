/*******************************************************************************
 * Copyright or © or Copr. 2012 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Jonathan Piat <jpiat@laas.fr> (2012 - 2013)
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
#ifndef FORK_H
#define FORK_H

#include <systemc>

using namespace std;

template<class T, int input_size = 1, int nb_output = 2>
SC_MODULE (preesm_fork) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_in<T> in;
	sc_core::sc_fifo_out<T> outs[nb_output];

	void enable() {
		bool isEnable;
		while (1) {
			isEnable = true;
			enable_port.write(false);
			sc_core::wait(10, sc_core::SC_NS);
			sc_core::wait(100, sc_core::SC_NS, in.data_written_event());
			isEnable &= (in.num_available() == input_size);
			enable_port.write(isEnable);
			sc_core::wait(10, sc_core::SC_NS);
		}
	}

	void invoke() {
		int i, j;
		while (1) {
			wait(invoke_port.posedge_event());
			cout << "invoking actor: " << "Fork" << endl;
			for (i = 0; i < nb_output; i++) {
				for (j = 0; j < input_size/nb_output; j++) {
					outs[i].write(in.read());
				}
			}
		}
	}

	SC_CTOR(preesm_fork) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
