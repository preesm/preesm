#include <windows.h>
#include <stdio.h>
#include <stdlib.h>

DWORD WINAPI Thread1 (LPVOID lpParam);
DWORD Id_Thread1; 
HANDLE hThread1;

DWORD WINAPI Thread2 (LPVOID lpParam);
DWORD Id_Thread2; 
HANDLE hThread2;


HANDLE sem_idct2d_data_generate_empty;
HANDLE sem_idct2d_data_generate_full;

void idct2d_data_generate_write_data(struct idct2d_data_generate_variables *_actor_variables ,
									 int DATA[64] ) ;
void idct2d_data_generate_write_sign(struct idct2d_data_generate_variables *_actor_variables ,
									 int *S ) ;
struct idct2d_data_generate_variables {
	int dummy;
};
struct idct2d_data_generate_variables idct2d_data_generate[1];
int idct2d_data_generate_DATA[64];
int idct2d_data_generate_S[1];




void idct2d_print_untagged_action_0(struct idct2d_print_variables *_actor_variables ,
									int values[64] ) ;
struct idct2d_print_variables {
	int dummy;
};
struct idct2d_print_variables idct2d_print[1];


struct Scale_variables {
	int A ;
	int B ;
	int C ;
	int D ;
	int E ;
	int F ;
	int G ;
	int H ;
	int K ;
	int L ;
	int Scale_factor[64] ;
};
struct Scale_variables Scale[1];

void Scale_untagged_action_0(struct Scale_variables *_actor_variables , int x[64] ,
							 int SOut[64] ) ;

int Scale_SOut[64];


struct Transpose_variables {
	int dummy;
};
struct Transpose_variables Transpose[1];

int Transpose_Y[64];

void Transpose_untagged_action_0(struct Transpose_variables *_actor_variables ,
								 int x[64] , int Y[64] ) ;

struct scaled_1d_idct_variables {
	int dummy;
};




struct scaled_1d_idct_variables scaled_1d_idct[1];

int scaled_1d_idct_Y[64];

void scaled_1d_idct_untagged_action_0(struct scaled_1d_idct_variables *_actor_variables ,
									  int x[8] , int Y[8] ) ;


struct Clip_variables {
	int sflag ;
	int count ;
};
struct Clip_variables Clip[1];

int Clip_O[64];

void Clip_limit(struct Clip_variables *_actor_variables , int i ,
				int *O ) ;
void Clip_read_signed(struct Clip_variables *_actor_variables ,
					  int s ) ;


struct RightShift_variables {
	int dummy;
};

struct RightShift_variables RightShift[1];
void RightShift_untagged_action_0(struct RightShift_variables *_actor_variables ,
								  int x[64] , int Out[64] ) ;

int RightShift_Out[64];


int argc;
char** argv;
int main(int main_argc, char* main_argv[]) { /* for link with C runtime boot */
	argc = main_argc;
	argv = main_argv;
	sem_idct2d_data_generate_empty=CreateSemaphore(NULL,0,1,NULL);
	sem_idct2d_data_generate_full=CreateSemaphore(NULL,0,1,NULL);

	hThread1=CreateThread(NULL,0,Thread1,(LPVOID)0,0,&Id_Thread1);
	hThread2=CreateThread(NULL,0,Thread2,(LPVOID)0,0,&Id_Thread2);
	/* `endIdle_task_()' */
	while(1);// Sleep(1000);
} /* end of main */



DWORD WINAPI Thread1 (LPVOID lpParam){
	Clip->count=-1;

	Scale->A = 1024;
	Scale->B = 1138;
	Scale->C = 1730;
	Scale->D = 1609;
	Scale->E = 1264;
	Scale->F = 1922;
	Scale->G = 1788;
	Scale->H = 2923;
	Scale->K = 2718;
	Scale->L = 2528;
	Scale->Scale_factor[0 + 0] = 1024;
	Scale->Scale_factor[0 + 1] = 1138;
	Scale->Scale_factor[0 + 2] = 1730;
	Scale->Scale_factor[0 + 3] = 1609;
	Scale->Scale_factor[0 + 4] = 1024;
	Scale->Scale_factor[0 + 5] = 1609;
	Scale->Scale_factor[0 + 6] = 1730;
	Scale->Scale_factor[0 + 7] = 1138;
	Scale->Scale_factor[0 + 8] = 1138;
	Scale->Scale_factor[0 + 9] = 1264;
	Scale->Scale_factor[0 + 10] = 1922;
	Scale->Scale_factor[0 + 11] = 1788;
	Scale->Scale_factor[0 + 12] = 1138;
	Scale->Scale_factor[0 + 13] = 1788;
	Scale->Scale_factor[0 + 14] = 1922;
	Scale->Scale_factor[0 + 15] = 1264;
	Scale->Scale_factor[0 + 16] = 1730;
	Scale->Scale_factor[0 + 17] = 1922;
	Scale->Scale_factor[0 + 18] = 2923;
	Scale->Scale_factor[0 + 19] = 2718;
	Scale->Scale_factor[0 + 20] = 1730;
	Scale->Scale_factor[0 + 21] = 2718;
	Scale->Scale_factor[0 + 22] = 2923;
	Scale->Scale_factor[0 + 23] = 1922;
	Scale->Scale_factor[0 + 24] = 1609;
	Scale->Scale_factor[0 + 25] = 1788;
	Scale->Scale_factor[0 + 26] = 2718;
	Scale->Scale_factor[0 + 27] = 2528;
	Scale->Scale_factor[0 + 28] = 1609;
	Scale->Scale_factor[0 + 29] = 2528;
	Scale->Scale_factor[0 + 30] = 2718;
	Scale->Scale_factor[0 + 31] = 1788;
	Scale->Scale_factor[0 + 32] = 1024;
	Scale->Scale_factor[0 + 33] = 1138;
	Scale->Scale_factor[0 + 34] = 1730;
	Scale->Scale_factor[0 + 35] = 1609;
	Scale->Scale_factor[0 + 36] = 1024;
	Scale->Scale_factor[0 + 37] = 1609;
	Scale->Scale_factor[0 + 38] = 1730;
	Scale->Scale_factor[0 + 39] = 1138;
	Scale->Scale_factor[0 + 40] = 1609;
	Scale->Scale_factor[0 + 41] = 1788;
	Scale->Scale_factor[0 + 42] = 2718;
	Scale->Scale_factor[0 + 43] = 2528;
	Scale->Scale_factor[0 + 44] = 1609;
	Scale->Scale_factor[0 + 45] = 2528;
	Scale->Scale_factor[0 + 46] = 2718;
	Scale->Scale_factor[0 + 47] = 1788;
	Scale->Scale_factor[0 + 48] = 1730;
	Scale->Scale_factor[0 + 49] = 1922;
	Scale->Scale_factor[0 + 50] = 2923;
	Scale->Scale_factor[0 + 51] = 2718;
	Scale->Scale_factor[0 + 52] = 1730;
	Scale->Scale_factor[0 + 53] = 2718;
	Scale->Scale_factor[0 + 54] = 2923;
	Scale->Scale_factor[0 + 55] = 1922;
	Scale->Scale_factor[0 + 56] = 1138;
	Scale->Scale_factor[0 + 57] = 1264;
	Scale->Scale_factor[0 + 58] = 1922;
	Scale->Scale_factor[0 + 59] = 1788;
	Scale->Scale_factor[0 + 60] = 1138;
	Scale->Scale_factor[0 + 61] = 1788;
	Scale->Scale_factor[0 + 62] = 1922;
	Scale->Scale_factor[0 + 63] = 1264;
	{int i; for(;;){ 

		WaitForSingleObject(sem_idct2d_data_generate_full,INFINITE);
		if(Clip->count>=0){
			idct2d_data_generate_write_data(idct2d_data_generate,idct2d_data_generate_DATA);
		}
		ReleaseSemaphore(sem_idct2d_data_generate_empty,1,NULL);
		if(Clip->count>=0){
			Scale_untagged_action_0(Scale , idct2d_data_generate_DATA ,Scale_SOut) ;

			Transpose_untagged_action_0(Transpose, Scale_SOut , Transpose_Y ) ;
			{int j; for(j=0; j<8; j++){ 
				scaled_1d_idct_untagged_action_0(scaled_1d_idct,&(Transpose_Y[j*8]),&(scaled_1d_idct_Y[j*8]));
			}}

			Transpose_untagged_action_0(Transpose, scaled_1d_idct_Y , Transpose_Y ) ;
			{int j; for(j=0; j<8; j++){ 
				scaled_1d_idct_untagged_action_0(scaled_1d_idct,&(Transpose_Y[j*8]),&(scaled_1d_idct_Y[j*8]));
			}}

			RightShift_untagged_action_0( RightShift , scaled_1d_idct_Y,  RightShift_Out) ;

			{int j; for(j=0; j<64; j++){ 
				Clip_limit( Clip , (RightShift_Out[j]) ,&(Clip_O[j]) ) ;
			}}

			idct2d_print_untagged_action_0(idct2d_print, Clip_O);
		}

	}} 

	return(0);
}

DWORD WINAPI Thread2 (LPVOID lpParam){
	ReleaseSemaphore(sem_idct2d_data_generate_empty,1,NULL);
	{int i; for(;;){

		WaitForSingleObject(sem_idct2d_data_generate_empty,INFINITE);
		if(Clip->count<0){
			idct2d_data_generate_write_sign(idct2d_data_generate,idct2d_data_generate_S);
		}
		ReleaseSemaphore(sem_idct2d_data_generate_full,1,NULL);
		if(Clip->count<0){
			Clip_read_signed(Clip, *idct2d_data_generate_S) ;
		}
	}} 
	return(0);
}