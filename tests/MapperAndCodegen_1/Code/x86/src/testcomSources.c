/*
 ============================================================================
 Name        : testcomSources.c
 Author      : mpelcat
 Version     :
 Copyright   : functions called by the communication tester project
 Description :
 ============================================================================
 */

void sensor_init(char* o1, char* o2){

}

void parallel_init(char* i1, char* o1){

}

void gen_int_init(char* i1,char* o1,char* o2){

}

void copy_init(char* i1, char* o1){

}

void actuator_init(char* i1,char* i2,char* i3){

}

void sensor(char* o1, char* o2){
	int i = 0;

	for(i=0;i<1000;i++){
		o2[i] = o1[i] = 0;
	}
}

void parallel(char* i1, char* o1){
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void gen_int(char* i1,char* o1,char* o2){
	int i = 0;

	for(i=0;i<1000;i++){
		o2[i] = o1[i] = i1[i];
	}
}

void copy(char* i1, char* o1){
	int i = 0;

	for(i=0;i<1000;i++){
		o1[i] = i1[i];
	}
}

void actuator(char* i1,char* i2,char* i3, int size){
	int i = 0;

	for(i=0;i<size;i++){
		printf("%d,%d,%d\n",i1,i2,i3);
	}
}
