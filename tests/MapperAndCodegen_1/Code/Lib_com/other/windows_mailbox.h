/*
 ============================================================================
 Name        :
 Author      : mpelcat
 Version     :
 Copyright   : Creating a mailbox system from a windows pipe
 Description :
 ============================================================================
 */

#ifndef WINDOWS_MAILBOX
#define WINDOWS_MAILBOX

void sendInit(int comId, int coreId, int targetId);
void receiveInit(int comId, int sourceId, int coreId);
void sendData(int comId, int coreId, int targetId, void* buffer, int size);
void receiveData(int comId, int sourceId, int coreId, void* buffer, int size);

#endif
