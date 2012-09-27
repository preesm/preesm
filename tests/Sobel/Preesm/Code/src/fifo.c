/************************************************************************
*
*	Managing FIFOs in Preesm execution
* 
*	@author jpiat
*	@author mpelcat
*
************************************************************************/
#include "fifo.h"

/**
* Initialize a fifo with the given token size, and fifo size
* @param fifo Pointer on the fifo structure to initialize
* @param token_size Size of the type of the token the fifo carry
* @param fifo_size The total size of the fifo
*/
int new_fifo(Fifo * fifo, unsigned int token_size, unsigned int fifo_size){
	unsigned int fifo_byte_size = fifo_size * token_size;

	fifo->data = (char *) malloc(fifo_byte_size);
	if(fifo->data == NULL){
		return -1;
	}
	// initializing data to zero for debug
	memset(fifo->data, 0, (size_t) fifo_byte_size);
	fifo->size = fifo_size ;
	fifo->token_size = token_size ;
	fifo->read_index = 0 ;
	fifo->write_index = 0 ;
	return 0 ;
}

/**
* Reads the given number of tokens from the fifo
* @param fifo Pointer on the fifo structure to read
* @param read_buffer Pointer on the buffer in which to read data
* @param nb_token Number of tokens to read from the fifo
*/
int pull(Fifo * fifo, void * read_buffer, unsigned int nb_token){
	// computing sizes in bytes
	unsigned int read_byte_size = nb_token * fifo->token_size ;
	unsigned int fifo_byte_size = fifo->size * fifo->token_size ;

	// empty fifo
	if(fifo_byte_size == 0){
		return -1;
	}
	// reading with modulo
	else if(fifo->read_index + read_byte_size > fifo_byte_size){
		unsigned int to_read = fifo_byte_size - fifo->read_index;
		if(memcpy(read_buffer , (char*)fifo->data + fifo->read_index, to_read) == NULL) return -1 ;
		if(memcpy((char*)read_buffer + to_read , fifo->data, read_byte_size - to_read) == NULL) return -1 ;
		fifo->read_index  = read_byte_size - to_read;
	}
	// reading without modulo
	else {
		if (memcpy(read_buffer, (char*)fifo->data + fifo->read_index, read_byte_size) == NULL) return -1;
		fifo->read_index  = fifo->read_index + read_byte_size;
	}

	return 0 ;
}

/**
* Writes the given number of tokens in the fifo
* @param fifo Pointer on the fifo structure to write
* @param read_buffer Pointer on the buffer in which to write data
* @param nb_token Number of tokens to write into the fifo
*/
int push(Fifo * fifo, void * write_buffer, unsigned int nb_token){
	// computing sizes in bytes
	unsigned int write_byte_size = nb_token * fifo->token_size ;
	unsigned int fifo_byte_size = fifo->size * fifo->token_size ;
	
	// writing with modulo
	if(fifo->write_index + write_byte_size > fifo_byte_size){
		unsigned int to_write = fifo_byte_size - fifo->write_index;
		if(memcpy((char*)fifo->data + fifo->write_index, write_buffer, to_write) == NULL) return -1;
		if(memcpy(fifo->data, (char*)write_buffer + to_write, write_byte_size - to_write) == NULL) return -1;
		fifo->write_index  = write_byte_size - to_write;
	}
	// writing without modulo
	else{
		if(memcpy((char*)fifo->data + fifo->write_index, write_buffer, write_byte_size) == NULL) return -1;
		fifo->write_index  = fifo->write_index + write_byte_size;
	}
	return 0 ;
}
