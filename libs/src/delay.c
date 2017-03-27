/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
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
#include "delay.h"


int new_delay(Delay * delay, size_t elt_size, int delay_size){
	delay -> data = (char *) malloc(delay_size * elt_size);
	if(delay -> data == NULL){
		return -1;
	}
	memset(delay -> data, 0, delay_size * elt_size);
	delay -> size = delay_size * (int) elt_size ;
	delay -> elt_size = elt_size ;
	delay -> read_index = 0 ;
	delay -> write_index = 0 ;
	return 0 ;
}

int read_delay(Delay * delay, void * read_buffer, int nb_elt){
	int read_size = nb_elt* (int) delay -> elt_size ;
	int base_index = 0 ;
	if((delay -> read_index) + read_size > delay -> size){
		int to_read = delay -> size - delay -> read_index;
		if(memcpy(read_buffer , &(delay -> data[delay -> read_index]), to_read) == NULL) return -1 ;
		delay -> read_index  = (delay -> read_index + to_read)  % (delay -> size * delay ->elt_size);
		read_delay(delay, &((char *)read_buffer)[to_read], nb_elt - to_read);
	}else {
		if (memcpy(&((char *)read_buffer)[base_index], &(delay -> data [delay -> read_index]), read_size) == NULL) return -1;
		delay -> read_index  = (delay -> read_index + read_size) % (delay -> size * delay ->elt_size) ;
	}
	
	return 0 ;
}




int write_delay(Delay * delay, void * write_buffer, int nb_elt){
	int write_size = nb_elt* (int) delay -> elt_size ;
	int base_index = 0 ;
	if((delay -> write_index) + write_size > delay -> size){
		int to_write = delay -> size - delay -> write_index;
		if(memcpy(&(delay -> data [delay -> write_index]), &((char *)write_buffer)[base_index], to_write) == NULL) return -1;
		delay -> write_index  = (delay -> write_index + to_write) % (delay -> size * delay ->elt_size);
		write_delay(delay, &((char *)write_buffer)[to_write], nb_elt - to_write);
	}else{
		if(memcpy(&(delay -> data [delay -> write_index]), &((char *)write_buffer)[base_index], write_size) == NULL) return -1;
		delay -> write_index  = (delay -> write_index + write_size) % (delay -> size * delay ->elt_size) ;
	}
	return 0 ;
}
