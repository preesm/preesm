#ifndef DEFINE
#define DEFINE

#if 1
// ref
#define WIDTH				450
#define HEIGHT				375
#endif
#if 0
#define WIDTH				434
#define HEIGHT				380
#endif


#define NB_DISP				61								//impair !!!
#define HALFDISPARITY		((NB_DISP-1)/2) 		
#define WIDTH_PHI_B			(WIDTH-(HALFDISPARITY+1))

#define	PHI_0				40
#define	F_BIG				30000

#define SHIFT				HALFDISPARITY

/*typedef struct node
{
unsigned long ML;
unsigned long MR;
unsigned long B;
}node;*/

#define NODE_TYPE			unsigned short int

typedef struct bp1d_node
{
	NODE_TYPE M;
	NODE_TYPE B;
}bp1d_node;


#define PHI_B_TYPE			unsigned short int
#define FI_TYPE				struct bp1d_node
#define ENERGY_TYPE			unsigned short int


#endif
