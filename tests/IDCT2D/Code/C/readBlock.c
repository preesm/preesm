

int test = 0 ;

void readBlock(int * in_1, int * in_2, int * trig, int * out_1);

void readBlock(int * in_1, int * in_2, int * trig, int * out_1){
	int i = 0 ;
	if(test == 0){
		while(i < 64){
			out_1[i] = in_1[i];
			i ++ ;
		}
		test = 1 ;
	}else{
		while(i < 64){
			out_1[i] = in_2[i];
			i ++ ;
		}
		test = 0 ;
	}
}