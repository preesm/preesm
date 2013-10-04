#include "com.h"
#include "define.h"
#include "init.h"

#if defined (WIN32)
    #include <winsock2.h>
    typedef int socklen_t;
#elif defined (linux)
    #include <sys/types.h>
    #include <sys/socket.h>
    #include <netinet/in.h>
    #include <arpa/inet.h>
    #include <unistd.h>
#endif

#define PORT_IN		2013
#define PORT_OUT	567

    static unsigned char image_in[BUFFER_SIZE_MAX];
    static unsigned char image_out[BUFFER_SIZE_MAX];

    void printValue(FILE * file, unsigned char image[], int start, int end){
    	int i;
    	for(i=start; i<end; i++){
    		fprintf(file,"0x%x\n", image[i]);
    	}
    }



/***********************************************************/

/******************** From messageQAppOS.c *****************/

/***********************************************************/

int main (int argc, char ** argv)
{
    #if defined (WIN32)
        WSADATA WSAData;
        int erreur = WSAStartup(MAKEWORD(2,2), &WSAData);
    #else
        int erreur = 0;
    #endif
        Int     status = 0, size_split=0, rest=0;
        int image_size;
        int dataSize[7];
        UInt32 i = 0;
        UInt16 Cores[7];
        UInt16  MessageQApp_numProcs  	 = 0;
        char addressW[16];		//windows address
//        char	MESSAGE_Q_NAME[8][16];
//        char 	REMOTE_Q_NAME[8][16];
        LocalQueue     		messageQ[8];
        RemoteQueue    		remoteQueueId[8];
        Heap    			heapHandle;
        SOCKET sock1;
        SOCKADDR_IN sin1;
        SOCKET sock2;
        SOCKADDR_IN sin2;
        char fileName[7][10];
        FILE * file = NULL;
        char s[20];
        long alloc;
        char c;

       int heapSize = 0;
       //unsigned long bufferAlloc = 2;

       file = fopen("/config1.ini","r");
       rewind(file);
       if(file==NULL){
    	   printf("Error while opening configuration file\n");
       }else{
    	   printf("Success while opening configuration file\n");
    	   i= readAddress(addressW, file);
    	   printf("Address Windows: %s\n",addressW);
    	   heapSize = readHeap(file);
    	   printf("Heap size = 0x%x\n",heapSize);
    	  rewind(file);
    	  while(c!=EOF){
    		c = fgetc(file);
    		if(c==';'){fgets(s,100,file);}else if(c=='b'){
    			fgets(s,6,file);
    			if(strncmp(s,"uffer",6)==0){
    				while(c!='='){c =fgetc(file);
    				}
    				fgets(s,100,file);
    				alloc=strtoll(s,NULL,16);
    				printf("%lx\n",alloc);
    			}
    		}
    	  }
       }
       fclose(file);

       printf ("MessageQApp sample application\n");

        if (argc > 1) {
        	MessageQApp_numProcs = strtol (argv [1], NULL, 10);
        }
        else {
            /* If no special run instructions are given, run for all procs. */
            Osal_printf ("You must specify the number of procs\n%s (procNum)\n", argv[0]);
            return -1;
        }

        SysLink_setup ();
        printf("Syslink Initialized\n");

        /* If there is more than 2 arguments, select cores in an order defined by user */
        /* Else select from Core1 to Core7 by default */
        printf("argc : %d, argv[2] : %s", argc, argv[2]);
        if(argc>2){
        	for(i=2;i<argc;i++)
        		Cores[i-2] = strtol (argv [i], NULL, 10);
        }else{
        	for(i=0;i<MessageQApp_numProcs;i++)
        		Cores[i] = i+1;
        }

        comInit(MessageQApp_numProcs,Cores,&heapHandle,messageQ,remoteQueueId);

/*******************************************************/

/*********************** Sockets ***********************/

/*******************************************************/

		if(!erreur)
		{

			sock1 = socket(AF_INET, SOCK_STREAM, 0);
			sock2 = socket(AF_INET, SOCK_STREAM, 0);
			/* Configuration socket1 */
			sin1.sin_addr.s_addr = inet_addr(addressW);
			sin1.sin_family = AF_INET;
			sin1.sin_port = htons(PORT_IN);
//			connectSocket(&sock1,sin1,PORT_IN,addressW);

			/* Communication on socket1 */
			if((status=connect(sock1, (SOCKADDR*)&sin1, sizeof(sin1))) != SOCKET_ERROR)
			{
				printf("Connected to %s on port %d\n", inet_ntoa(sin1.sin_addr), htons(sin1.sin_port));

				/* Receiving information from server */

					/* Receive image size */
				if(recv(sock1,&image_size,sizeof(int),MSG_WAITALL)!=0){
					image_size = htonl(image_size);
					Osal_printf("Image size: %d\n",image_size);
				}
					/* Receive an image */
				if ((i=recv(sock1,image_in,image_size,MSG_WAITALL))){
					Osal_printf("Image received %d bytes\n", i);
				}

			}else
				{printf("Error when trying to connect server with socket1: %d\n",status);}

			/* Socket1 closing */
			closesocket(sock1);
			printf("Connection on port %d closed\n", htons(sin1.sin_port));
			size_split = image_size/MessageQApp_numProcs;
			rest = image_size%MessageQApp_numProcs;

			for(i=0;i<MessageQApp_numProcs;i++)
			{
				dataSize[i]=size_split;
				if(rest!=0){
					dataSize[i]++;
					rest--;
				}
				/* Send data to Core i */
				sendQ(remoteQueueId[i], &i,sizeof(int));	//rank of core
				sendQ(remoteQueueId[i], &alloc, sizeof(long));
				sendQ(remoteQueueId[i], &dataSize[i], sizeof(int));
				sendQ(remoteQueueId[i], image_in+i*dataSize[i], dataSize[i]);
				printf("Image split send to core %d\n",i+1);
			}

			/* Receive data from cores */
			for(i=0;i<MessageQApp_numProcs;i++)
			{
				sprintf(fileName[i], "/LogFiles/Core%d.log", Cores[i]);
				file = fopen(fileName[i],"w+");
				/* Receive data from Core i */
				recvQ(messageQ[i],image_out+i*dataSize[i], dataSize[i]);
				/* Print value in a file */
				if(file != NULL){
					printValue(file, image_out, i*dataSize[i], (i+1)*dataSize[i]);
					fclose(file);
				}
			}

			/* Configuration socket2 */
			sin2.sin_addr.s_addr = inet_addr(addressW);
			sin2.sin_family = AF_INET;
			sin2.sin_port = htons(PORT_OUT);

			/* Communication on socket2 */
			if(connect(sock2, (SOCKADDR*)&sin2, sizeof(sin2)) != SOCKET_ERROR){
				printf("Connected to %s on port %d\n", inet_ntoa(sin2.sin_addr), htons(sin2.sin_port));
				send(sock2,&MessageQApp_numProcs,sizeof(int),MSG_WAITALL);

				usleep(1000);

				/* Send result to server */
				send(sock2,image_out,image_size,0);
				printf("Image processed sent\n");
			}else
				{printf("Error when trying to connect server with socket2\n");}

			/* Socket2 closing */
			closesocket(sock2);
			printf("Connection on port %d closed\n", htons(sin2.sin_port));

			comEnd(MessageQApp_numProcs,messageQ,remoteQueueId,heapHandle);
			SysLink_destroy ();

			printf("Fini!\n");
			#if defined (WIN32)
				WSACleanup();
			#endif
		}

		return EXIT_SUCCESS;
}
