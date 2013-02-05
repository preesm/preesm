#ifndef JOIN_H
#define JOIN_H

#include <systemc>

using namespace std;

template<class T, int input_size = 1, int nb_input = 2>
SC_MODULE (preesm_join) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_out<T> out;
	sc_core::sc_fifo_in<T> ins[nb_input];

	void enable() {
		int i;
		bool isEnable = true;
		while (1) {
			isEnable = true;
			enable_port.write(false);
			sc_core::wait(10, sc_core::SC_NS);
			for (i = 0; i < nb_input; i++) {
				isEnable &= (ins[i].num_available() == input_size);
			}
			enable_port.write(isEnable);
			sc_core::wait(10, sc_core::SC_NS);
		}

	}

	void invoke() {
		int i, j;
		while (1) {
			wait(invoke_port.posedge_event());
			cout << "invoking actor: " << "Join" << endl;
			for (i = 0; i < nb_input; i++) {
				for (j = 0; j < input_size; j++) {
					out.write(ins[i].read());
				}
			}
		}
	}

	SC_CTOR(preesm_join) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
