/*
 * init.c
 *
 *  Created on: July 17, 2013
 *      Author: rparois
 */

#include "init.h"


int readSize(FILE* f){
    int readChar;
    int Size = 1;
    int res = 0;
    char tmp[100];
    rewind(f);
    while(readChar!=EOF){
        readChar = fgetc(f);
        if(readChar == ';')
            fgets(tmp,100,f);
        if(readChar == 'i'){
            fgets(tmp, 4, f);
            if(strcmp(tmp,"mage")){
                while(readChar!='='){
                    readChar=fgetc(f);
                }
                fgets(tmp,100,f);
                Size*=strtol(tmp,NULL,10);
            }
        }
        if(readChar == 'c'){
            fgets(tmp, 6, f);
            if(strcmp(tmp,"olored")){
                while(readChar!='='){
                    readChar=fgetc(f);
                }
                fgets(tmp,100,f);
                if(strcmp(tmp,"yes"))
                    Size*=3;
                res = Size;
            }
        }
    }
    return res;
}


int readHeap(FILE* f){
    int readChar;
    int Size = 0;
    int res = 0;
    char tmp[100];
    rewind(f);
    while(readChar!=EOF){
        readChar = fgetc(f);
        if(readChar == ';')
            fgets(tmp,100,f);
        if(readChar == 'h'){
            fgets(tmp, 3, f);
            if(strcmp(tmp,"eap")){
                while(readChar!='='){
                    readChar=fgetc(f);
                }
                fgets(tmp,100,f);
                Size = strtol(tmp,NULL,16);
                res = Size;
            }
        }
    }
    return res;
}


int readBuffer(long alloc, FILE* f){
    char c;
    char tmp[100];
    alloc = 0;
    rewind(f);
        	  while(c!=EOF){
        		c = fgetc(f);
        		if(c==';'){
        			fgets(tmp,100,f);
        		}else if(c=='b'){
        			fgets(tmp,6,f);
        			if(strncmp(tmp,"uffer",6)==0){
        				while(c!='='){
        					c =fgetc(f);
        				}
        				fgets(tmp,100,f);
        				alloc=strtoll(tmp,NULL,16);
        				printf("%lx\n",alloc);
        			}
        		}
           }
    return 1;
}

int readAddress(char * res, FILE* f){
    int readChar;
//    int i = 0;
    char tmp[100];
    rewind(f);
    while(readChar!=EOF){
        readChar = fgetc(f);
        if(readChar == ';')
            fgets(tmp,100,f);
        if(readChar == 'a'){
            fgets(tmp, 7, f);
            if(strcmp(tmp,"ddress")==0){
                while(readChar!='='){
                    readChar=fgetc(f);
                }
                fgets(res,12,f);
            }
        }
    }
    return 1;
}
