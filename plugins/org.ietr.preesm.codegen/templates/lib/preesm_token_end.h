#ifndef PREESM_TOKEN_END_H
#define PREESM_TOKEN_END_H

#include <stdio.h>
#include <stdlib.h>
#include <systemc>

using namespace std ;

template<class T, int input_size = 1>
SC_MODULE (preesm_token_end) {

	sc_core::sc_out<bool> enable_port;
	sc_core::sc_in<bool> invoke_port;

	sc_core::sc_fifo_in<T> end_in;
	sc_core::sc_fifo_out<T> end_out;

	void enable() {
		bool isEnable;
		while (1) {
			sc_core::wait();
		}
	}
	void invoke() {
		int i ;
		while (1) {
			end_out.write(end_in.read());
		}
	}

	SC_CTOR(preesm_token_end) {
		SC_THREAD(invoke);
		SC_THREAD(enable);
	}

};

#endif
