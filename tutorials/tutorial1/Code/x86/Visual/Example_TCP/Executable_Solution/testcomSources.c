/*
 ============================================================================
 Name        : testcomSources.c
 Author      : mpelcat
 Version     :
 Copyright   : functions called by the communication tester project
 Description :
 ============================================================================
 */


#define TESTVALUE 1000

#define SIZE_LOCAL 1000

int nbrand =6;

void sensor_init(char* o1, char* o2, char* o3){

}

void sensor2_init(char* o1){
}

void parallel_init(char* i1, char* o1){

}

void gen_int_init(char* i1,char* o1,char* o2){

}

void copy_init(char* i1, char* o1){

}

void actuator_init(char* i1,char* i2,char* i3){

}

void circular_init(char* i1,char* o1){

}
void circular6_init(char* i1,char* o1){

}

void sensor(char* o1, char* o2, char* o3){
	//printf("sensor");
	int i = 0;

	for(i=0;i<TESTVALUE;i++){
		o3[i] = o2[i] = o1[i] = i%128;
	}
}

void sensor2(char* o1){
	//printf("sensor");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = -i%128;
	}
}

void parallel(char* i1, char* o1){
	//printf("parallel");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
	}
}

void gen_int(char* i1,char* i2,char* o1,char* o2){
	//printf("gen_int");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}

void copy(char* i1, char* o1, int size){
	//printf("copy");
	int i = 0;

	for(i=0;i<TESTVALUE;i++){
		o1[i] = i1[i];
	}
}

void circular(char* i1, char* i2, char* o1, char* o2){
	//printf("circular");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
		o2[i] = i2[i];
	}
}
void circular6(char* i1, char* o1){
	//printf("circular6");
	int i = 0;

	for(i=0;i<SIZE_LOCAL;i++){
		o1[i] = i1[i];
	}
}

void actuator(char* i1, int size,char* i2,char* i3){
	//printf("actuator");
	int i = 0;
	int bSuccess = 1;

	for(i=0;i<TESTVALUE;i++){
		if(i1[i] != nbrand){
			bSuccess = 0;
			break;
		}
		if(i2[i] != nbrand){
			bSuccess = 0;
			break;
		}
		if(i3[i] != -i){
			bSuccess = 0;
			break;
		}
	}

	if(bSuccess){
		printf("success\n");
	}
	else{
		printf("failure\n");
	}
}


