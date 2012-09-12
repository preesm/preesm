
#include <stdio.h>
#include <stdlib.h>
#include "systemc.h"


template <class T, int input_size = 1, int nb_input = 2>
SC_MODULE (preesm_join) {

	sc_out<bool> enable_port ;
	sc_in<bool> invoke_port ;

	sc_fifo_out<T> out ;
	sc_fifo_in<T> ins [nb_input] ;

	void enable(){
		bool isEnable = true ;
		int i ;
		for(i = 0 ; i < nb_input ; i ++ ){
			isEnable &= (ins[i].num_available() == input_size);
		}
		enable_port.write(isEnable);
	}


	void invoke(){
		int i, j ;
		for(i = 0 ; i < nb_input ; i ++){
			for( j = 0 ; j < input_size ; j ++){
				out.write(ins[i].read());
			}
		}
	}


	SC_CTOR(preesm_join) {
		int i ;
		SC_METHOD(invoke);
		sensitive << invoke_port.pos();
		SC_METHOD(enable);
		for(i = 0 ; i < nb_input ; i ++ ){
			sensitive << ins[i];
		}
	}

};


