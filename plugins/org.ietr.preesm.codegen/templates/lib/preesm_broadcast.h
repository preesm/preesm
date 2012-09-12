#ifndef PREESM_BROADCAST_H
#define PREESM_BROADCAST_H

#include <stdio.h>
#include <stdlib.h>
#include <systemc>

using namespace std ;

template<class T, int input_size = 1, int output_size = 1, int nb_output = 2>
SC_MODULE (preesm_broadcast) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_in<T> in;
	sc_core::sc_fifo_out<T> out[nb_output];

	void enable() {

		bool isEnable;
		while (1) {
			isEnable = true;
			enable_port.write(false);
			sc_core::wait(1, sc_core::SC_NS);
			sc_core::wait(100, sc_core::SC_NS, in.data_written_event());
			isEnable &= (in.num_available() >= input_size);
			enable_port.write(isEnable);
			sc_core::wait(1, sc_core::SC_NS);
		}
	}

	void invoke() {
		int i, j, k;
		T * buffer = (T *) malloc(input_size *sizeof(T)) ;

		while (1) {
			wait(invoke_port.posedge_event());
			cout << "invoking " << this->name() << endl ;
			for (i = 0; i < input_size; i++) {
				T value = in.read();
				buffer[i] = value;
			}
			for (k = 0; k < (output_size/input_size); k++) {
				for (j = 0; j < nb_output; j++) {
					for (i = 0; i < input_size; i++) {
						out[j].write(buffer[i]);
					}
				}
			}
			}

	}

	SC_CTOR(preesm_broadcast) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
