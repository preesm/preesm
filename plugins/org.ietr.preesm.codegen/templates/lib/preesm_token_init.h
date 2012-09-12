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
