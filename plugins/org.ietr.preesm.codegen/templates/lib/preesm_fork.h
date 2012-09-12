
#include <stdio.h>
#include <stdlib.h>
#include "systemc.h"


template <class T, int input_size = 1, int nb_output = 2>
SC_MODULE (preesm_fork) {

	sc_out<bool> enable_port ;
	sc_in<bool> invoke_port ;

	sc_fifo_in<T> in ;
	sc_fifo_out<T> outs [nb_output] ;

	void enable(){
		bool isEnable = true ;
		isEnable &= (in.num_available() == input_size);
		enable_port.write(isEnable);
	}

	void invoke(){
		int i, j ;
		for(i = 0 ; i < nb_output ; i ++){
			for( j = 0 ; j < input_size ; j ++){
				outs[j].write(in.read()/nb_output);
			}
		}
	}


	SC_CTOR(preesm_fork) {
		int i ;
		SC_METHOD(invoke);
		sensitive << invoke_port.pos();
		SC_METHOD(enable);
		sensitive << in;
	}

};

