#include "x86.h"
#include "pentium_tcp.h"

Media_TCP tcpmedia[8][8][2];

void pipeInit(int comId, int id1, int id2, int clientOrServer){
	TCHAR pipeName[15] = "mediumtcp  ";
	itoa (id1,pipeName+9,10);
	itoa (id2,pipeName+10,10);

	if(clientOrServer){
		init_TCP_server( tcpmedia[id1][id2][1] );
	}
	else{
		init_TCP_client ( tcpmedia[id1][id2][0], "localhost" );
	}
}

void sendInit(int comId, int coreId, int targetId){
	pipeInit(comId, coreId, targetId, coreId < targetId);
}

void receiveInit(int comId, int sourceId, int coreId){
	pipeInit(comId, sourceId, coreId, coreId < sourceId);
}

void sendData(int comId, int coreId, int targetId, void* buffer, int size){

	Send_TCP (tcpmedia[coreId][targetId][coreId < targetId], buffer, size );
}

void receiveData(int comId, int sourceId, int coreId, void* buffer, int size){

	Receive_TCP (tcpmedia[sourceId][coreId][coreId < sourceId], buffer, size );
}
