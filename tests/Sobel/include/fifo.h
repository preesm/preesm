#ifndef FIFO_H
#define FIFO_H

/**
 * Item of the FIFO
 */
typedef struct fifo_item{
	void* item;
	struct fifo_item *next;
} fifo_item;

/**
 * FIFO structure
 */
typedef struct fifo{
	fifo_item* head;
	int count;
} fifo;

/**
 * Initialize multiples fifo structures.
 * @param fifo Array of structures to initialize
 * @param size Count of structures to initialize
 */
void fifos_init(fifo *fifo, int size);

/**
 * Push a data into the FIFO
 * @param fifo FIFO pointer
 * @param item Pointer of the item to store.
 * @param size Size of the item.
 */
void push(fifo *fifo, void* item, int size);

/**
 * Pull a data into the FIFO
 * @param fifo FIFO pointer
 * @param item Pointer of the destination of the data to retrieve.
 * @param size Size of the item.
 */
void pull(fifo *fifo, void* item, int size);

#endif//FIFO_H
