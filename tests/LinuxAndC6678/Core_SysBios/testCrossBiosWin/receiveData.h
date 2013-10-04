/*
 * receiveData.h
 *
 *  Created on: 3 oct. 2013
 *      Author: kdesnos
 */

#ifndef RECEIVEDATA_H_
#define RECEIVEDATA_H_

void initSendData();
void initReceiveData();

void sendData(int size, unsigned char *data);
void receiveData(int size, unsigned char *data);


#endif /* RECEIVEDATA_H_ */
