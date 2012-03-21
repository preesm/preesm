
#ifndef PREESM_FIFOS
#define PREESM_FIFOS

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
	A structure to represent Fifos
*/
typedef struct{
  void* data ;		/**< the data the fifo carries*/
  unsigned int read_index ;	/**< the index in data where to read from */
  unsigned int write_index ;	/**< the index in data where to write to */
  unsigned int token_size ;	/**< the size of the token data type */
  unsigned int size ;		/**< the total size of the fifo in tokens */
} Fifo ;

/**
* Initialize a fifo with the given token size, and fifo size
* @param fifo Pointer on the fifo structure to initialize
* @param token_size Size of the type of the token the fifo carry
* @param fifo_size The total size of the fifo
*/
int new_fifo(Fifo * fifo, unsigned int token_size, unsigned int fifo_size);

/**
* Reads the given number of tokens from the fifo
* @param fifo Pointer on the fifo structure to read
* @param read_buffer Pointer on the buffer in which to read data
* @param nb_token Number of tokens to read from the fifo
*/
int pull(Fifo * fifo, void * read_buffer, unsigned int nb_token);
/**
* Writes the given number of tokens in the fifo
* @param fifo Pointer on the fifo structure to write
* @param read_buffer Pointer on the buffer in which to write data
* @param nb_token Number of tokens to write into the fifo
*/
int push(Fifo * fifo, void * write_buffer, unsigned int nb_token);

#endif