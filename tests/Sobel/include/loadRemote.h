#ifndef LOADDSP_H
#define LOADDSP_H


#include <IpcUsr.h>

typedef struct{
	unsigned short   	procId;
	ProcMgr_Handle 		procMgrHandle;
    unsigned int 		entryPoint;
}remoteProc;

int loadRemote_init();
remoteProc* loadRemote_connect(char* dest);
int loadRemote_load(remoteProc* proc, char* file);
int loadRemote_start(remoteProc* proc);
int loadRemote_stop(remoteProc* proc);
int loadRemote_close(remoteProc* proc);
int loadRemote_clean();

#endif /* LOADDSP_H */
