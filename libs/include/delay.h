#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/**
	A structure to represent Delays
*/
typedef struct{
  char * data ; /**< the data the delay carries*/
  int read_index ; /**< the index in data where to read from*/
  int write_index ; /**< the index in data where to write to*/
  size_t elt_size ; /**< the size of the token data type*/
  int size ; /**< the total size of the delay*/
} Delay ;


#define DELAY_INIT(delay_size, type_size) { [delay_size] , 0, 0,type_size, delay_size}

/**
	Initialize a delay with the given elemenent size, and delay size
	@param delay Pointer on the delay structure to initialize
	@param elt_size Size of the type of the data the delay carry
	@param delay_size The total size of the delay
*/
int new_delay(Delay * delay, size_t elt_size, int delay_size);

/**
	Reads the given number of tokens from the delay
	@param delay Pointer on the delay structure to read
	@param read_buffer Pointer on the buffer in which to read data
	@param nb_elt Number of tokens to read from the delay
*/
int read_delay(Delay * delay, void * read_buffer, int nb_elt);

/**
	Writes the given number of tokens in the delay
	@param delay Pointer on the delay structure to write
	@param read_buffer Pointer on the buffer in which to write data
	@param nb_elt Number of tokens to write into the delay
*/
int write_delay(Delay * delay, void * write_buffer, int nb_elt);