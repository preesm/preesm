/*
 * sendReceiveData.h
 *
 *  Created on: 3 oct. 2013
 *      Author: kdesnos
 */

#ifndef SEND_RECEIVE_DATA_H_
#define SEND_RECEIVE_DATA_H_

void initSendData();
void initReceiveData();

void sendData(int size, unsigned char *data);
void receiveData(int size, unsigned char *data);


#endif /*  SEND_RECEIVE_DATA_H_ */
