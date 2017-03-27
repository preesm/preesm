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