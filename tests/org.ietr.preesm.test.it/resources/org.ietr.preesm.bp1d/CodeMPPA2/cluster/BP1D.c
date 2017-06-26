// Created by JF Nezan

// retravailler les type et la valeur de F_BIG

#include <stdio.h>
#include <stdlib.h>
#include <float.h>
#include <math.h>
#include <float.h>
#include "define.h"
#include <assert.h>

#ifdef __k1__
#include <HAL/hal/hal_ext.h>
#include <utask.h>
#include <HAL/hal/board/boot_args.h>
#endif

static inline NODE_TYPE min2(NODE_TYPE val1, NODE_TYPE val2)
{
	if (val1<val2)
		return (val1);
	else
		return (val2);
}

static inline NODE_TYPE min3(NODE_TYPE val1, NODE_TYPE val2, NODE_TYPE val3)
{
	if (val1<val2)
	{
		if(val1 < val3)
			return (val1);
		else
			return (val3);
	}
	else if(val2 < val3)
		return (val2);
	else
		return (val3);
}

// BP_ComputePhi_B computes messages between the two images
// there messages are stored in the Phi_B table
//static inline void BP_ComputePhi_B(unsigned char *leftR, unsigned char *leftG, unsigned char *leftB, unsigned char *rightR, unsigned char *rightG, unsigned char *rightB, PHI_B_TYPE *Phi_B)
//{
//	short x, dd, diffR, diffG, diffB ;
//	short posL, posR, posL2;
//
//	for (x=0; x< WIDTH_PHI_B; x++)				
//	{
//		for (dd=0; dd < HALFDISPARITY + 1; dd++)
//		{
//			posL = dd + x;
//			posR = HALFDISPARITY + x - dd;
//			// compute Phi_B pour d = 2*dd
//
//			diffR = leftR[posL] - rightR[posR];
//			diffG = leftG[posL] - rightG[posR];
//			diffB = leftB[posL] - rightB[posR];
//			Phi_B[x*(NB_DISP+1) + dd*2] = abs(diffR) + abs(diffG) + abs(diffB);
//
//			posL2 = posL + 1;				
//			// compute Phi_B pour d = 2*dd + 1  			
//			diffR = leftR[posL2] - rightR[posR];
//			diffG = leftG[posL2] - rightG[posR];
//			diffB = leftB[posL2] - rightB[posR];
//			Phi_B[x*(NB_DISP+1) + dd*2 + 1] = abs(diffR) + abs(diffG) + abs(diffB); 
//		}		
//	}
//}

void BP_ComputePhi_B(unsigned char *rgbLeft, unsigned char *rgbRight, PHI_B_TYPE *Phi_B)
{
	int x, dd, diffR, diffG, diffB ;
	int posL, posR, posL2;

	rgbLeft += 3*SHIFT;

	for (x=0; x< WIDTH_PHI_B; x++)				
	{
		for (dd=0; dd < HALFDISPARITY + 1; dd++)
		{
			posL = dd + x;							
			posR = HALFDISPARITY + x - dd;
			posL2 = posL + 1;

			diffR = rgbLeft[3*posL+0] - rgbRight[3*posR+0];
			diffG = rgbLeft[3*posL+1] - rgbRight[3*posR+1];
			diffB = rgbLeft[3*posL+2] - rgbRight[3*posR+2];
			Phi_B[x*(NB_DISP+1) + dd*2] = abs(diffR) + abs(diffG) + abs(diffB);
			
			// compute Phi_B pour d = 2*dd + 1  			
			diffR = rgbLeft[3*posL2+0] - rgbRight[3*posR+0];
			diffG = rgbLeft[3*posL2+1] - rgbRight[3*posR+1];
			diffB = rgbLeft[3*posL2+2] - rgbRight[3*posR+2];
			Phi_B[x*(NB_DISP+1) + dd*2 + 1] = abs(diffR) + abs(diffG) + abs(diffB); 
		}
	}
}

// BP_ComputeFi computes the forward pass
// forward messages are stored in the Fi table of node structure 
void BP_ComputeFi(PHI_B_TYPE *Phi_B, FI_TYPE *Fi)
{
	short x, dd;
	NODE_TYPE MR_MR, B_MR;
	NODE_TYPE ML_B, B_B, MR_B;

	for (dd=0; dd < NB_DISP + 2; dd++)						// initialisation of the Fi first colomn to 0
	{
		Fi[dd].M = 0;																					
		Fi[dd].B  = 0;											
	}

	for (x=1; x < WIDTH_PHI_B; x++)						// initialisation of the Fi first lign and last lign
	{
		Fi[x*(NB_DISP + 2) + NB_DISP + 1].M = F_BIG;		// normalement inutile	=> pas d'après les tests !
		Fi[x*(NB_DISP + 2) + NB_DISP + 1].B = F_BIG;		// normalement inutile	=> pas d'après les tests !	
		Fi[x*(NB_DISP + 2)].M				= F_BIG;						// normalement inutile	=> pas d'après les tests !	
		Fi[x*(NB_DISP + 2)].B				= F_BIG;						// peut être inutile ? 			
	}


	for (x=1; x < WIDTH_PHI_B; x++)

	{
		// compute Fi for the disparity d = 0	 
		// Fi->ML
		MR_MR	= Fi[(x-1)*(NB_DISP+2) + 2].M	+ PHI_0;
		B_MR	= Fi[(x-1)*(NB_DISP+2) + 2].B	+ Phi_B[(x-1)*(NB_DISP+1) + 1];
		Fi[x*(NB_DISP+2)+1].M  = min2 (MR_MR , B_MR); 

		// Fi->B
		MR_B	= Fi[(x-1)*(NB_DISP+2)+1].M		+ PHI_0 ;			
		B_B		= Fi[(x-1)*(NB_DISP+2)+1].B		+ Phi_B[(x-1)*(NB_DISP+1)];
		ML_B	= Fi[(x-1)*(NB_DISP+2)].B		+ PHI_0;										
		Fi[x*(NB_DISP+2)+1].B  = min3( ML_B , B_B , MR_B);											

		for (dd=0; dd < HALFDISPARITY; dd++)
		{
			// compute Fi even disparities (d = dd*2) using previous Fi and Phi_B
			// values for d = 0 and d = 2 are required to compute d = 1
			// Fi->ML
			MR_MR	= Fi[(x-1)*(NB_DISP+2) + dd*2 + 4].M	+ PHI_0;
			B_MR	= Fi[(x-1)*(NB_DISP+2) + dd*2 + 4].B	+ Phi_B[(x-1)*(NB_DISP+1) + dd*2 + 3];
			Fi[x*(NB_DISP+2) + dd*2 + 3].M  = min2 (MR_MR , B_MR); 

			// Fi->B
			MR_B	= Fi[(x-1)*(NB_DISP+2) + 2*dd + 3].M 	+ PHI_0;
			B_B		= Fi[(x-1)*(NB_DISP+2) + 2*dd + 3].B	+ Phi_B[(x-1)*(NB_DISP+1) + 2*dd + 2];
			ML_B	= Fi[(x-1)*(NB_DISP+2) + 2*dd + 2].B	+ PHI_0;										
			Fi[x*(NB_DISP+2) + 2*dd + 3].B  = min3( ML_B , B_B , MR_B);				


			// compute Fi for odd disparities (d = 2*dd + 1) using previous Fi and Phi_B	
			// Fi->ML
			MR_MR	= Fi[x*(NB_DISP+2) + (dd * 2 + 1) + 2].M	+ PHI_0;
			B_MR = Fi[x*(NB_DISP + 2) + (dd * 2 + 1) + 2].B		+ Phi_B[x*(NB_DISP + 1) + (dd * 2 + 1) + 1];
			Fi[x*(NB_DISP + 2) + (dd * 2 + 1) + 1].M = min2(MR_MR, B_MR);

			// Fi->B
			MR_B = Fi[(x - 1)*(NB_DISP + 2) + (dd * 2 + 1) + 1].M	+ PHI_0;
			B_B = Fi[(x - 1)*(NB_DISP + 2)	+ (dd * 2 + 1) + 1].B	+ Phi_B[(x - 1)*(NB_DISP + 1) + (2 * dd) + 1];
			ML_B = Fi[x*(NB_DISP + 2)		+ (dd * 2 + 1)].B		+ PHI_0;
			Fi[x*(NB_DISP + 2) + (dd * 2 + 1) + 1].B = min3(ML_B, B_B, MR_B);
		}		
	}
}

// BP_Compute_Energy computes the backward pass = energy for each node of the BVP
// backward messages are stored in the GrandPhi table of node structure 
// the energies are stored in the table Energy but it is not required 
void BP_Compute_Energy(PHI_B_TYPE *Phi_B, ENERGY_TYPE *Energy, FI_TYPE *Fi, unsigned char *DepthMap)
{
	short x, dd;
	struct bp1d_node GrandPhi[NB_DISP+2];
	NODE_TYPE ML_ML, B_ML;
	NODE_TYPE MR_B, B_B, ML_B;
	ENERGY_TYPE Energy_B, Energy_ML, Energy_MR, min;


	for (dd=0; dd < NB_DISP + 1; dd++)						// initialisation of the GrandPhi backward message first colomn to 0 
	{
		GrandPhi[dd+1].M	= 0;																					
		GrandPhi[dd+1].B	= 0;		
	}

	GrandPhi[0].M			= F_BIG;	
	GrandPhi[0].B			= F_BIG;

	GrandPhi[NB_DISP+1].M	= F_BIG;	
	GrandPhi[NB_DISP+1].B	= F_BIG;


	for (dd=0; dd < NB_DISP; dd++)						// compute energies for the last column "WIDTH_PHI_B"
	{
		Energy_B	= Phi_B[(WIDTH_PHI_B - 1)*(NB_DISP + 1) + dd]	+ Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].B;
		Energy_ML	= PHI_0											+ Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].B;
		Energy_MR	= PHI_0											+ Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].M;	
		Energy[(WIDTH_PHI_B - 1)* NB_DISP + dd]  = min3( Energy_B , Energy_ML , Energy_MR);		
	}

	for (x=WIDTH_PHI_B-2; x>=0; x--)															
	{
		for (dd= 0; dd < HALFDISPARITY ; dd++)
		{
			// compute GrandPhi message for odd disparities (d = 2*dd + 1) using previous GrandPhi and Phi_B		
			// should start with odd disparities before even ones  
			// GrandPhi->MR
			ML_ML	=	GrandPhi[dd*2 + 3].M		+	PHI_0;
			B_ML	=	GrandPhi[dd*2 + 3].B		+	Phi_B[(x+1)*(NB_DISP+1) + dd*2 + 2];


			// GrandPhi->B
			MR_B	=	GrandPhi[dd*2 + 1 ].B 		+	PHI_0;												
			B_B		=	GrandPhi[dd*2 + 2 ].B		+	Phi_B[(x+1)*(NB_DISP+1) + dd*2 + 1];
			ML_B	=	GrandPhi[dd*2 + 2 ].M 		+	PHI_0;	

			GrandPhi[dd * 2 + 2].M = min2(ML_ML, B_ML); // Update : GrandPhi after computing ML_B
			GrandPhi[dd*2 + 2].B  = min3( ML_B , B_B , MR_B);	


			Energy_B	=	GrandPhi[dd*2 + 2].B	+ Phi_B[x*(NB_DISP+1) + dd*2 + 1]	+ Fi[x * (NB_DISP + 2) + dd*2 + 2].B;
			Energy_ML	= GrandPhi[dd*2 + 2].M		+ PHI_0								+ Fi[x * (NB_DISP + 2) + dd*2 + 2].B;
			Energy_MR	= GrandPhi[dd*2 + 2].B		+ PHI_0								+ Fi[x * (NB_DISP + 2) + dd*2 + 2].M;
			Energy[x * NB_DISP + 2*dd + 1]  = min3( Energy_B , Energy_ML , Energy_MR);

			// compute GrandPhi message for even disparities (d = 2*dd) using previous GrandPhi and Phi_B
			// GrandPhi->MR
			ML_ML	=	GrandPhi[dd*2 + 2].M		+	PHI_0;
			B_ML	=	GrandPhi[dd*2 + 2].B		+	Phi_B[x*(NB_DISP+1) + dd*2 + 1];


			// GrandPhi->B
			MR_B	= GrandPhi[dd*2].B 			+	PHI_0;												
			B_B		= GrandPhi[dd*2 + 1].B		+	Phi_B[(x+1)*(NB_DISP+1) + dd*2];
			ML_B	= GrandPhi[dd*2 + 1].M 		+	PHI_0;	

			GrandPhi[dd * 2 + 1].M = min2(ML_ML, B_ML); // Update : GrandPhi after computing ML_B
			GrandPhi[dd*2 + 1].B  = min3( ML_B , B_B , MR_B);		

			Energy_B =	GrandPhi[dd*2 + 1].B	+ Phi_B[x*(NB_DISP+1) + dd*2]		+ Fi[x * (NB_DISP + 2) + dd*2 + 1].B;
			Energy_ML = GrandPhi[dd*2 + 1].M	+ PHI_0								+ Fi[x * (NB_DISP + 2) + dd*2 + 1].B;
			Energy_MR = GrandPhi[dd*2 + 1].B	+ PHI_0								+ Fi[x * (NB_DISP + 2) + dd*2 + 1].M;

			Energy[x * NB_DISP + 2*dd] = min3( Energy_B , Energy_ML , Energy_MR);

		}

		// compute GrandPhi for the last disparity d = 2 * HALFDISPARITY+1
		// 2 * dd + 1 = NB_DISP
		// GrandPhi->MR
		{
			ML_ML	=	GrandPhi[NB_DISP + 1].M		+ PHI_0;
			B_ML	=	GrandPhi[NB_DISP + 1].B		+ Phi_B[x*(NB_DISP+1) + NB_DISP];


			// GrandPhi->B
			MR_B	=	GrandPhi[NB_DISP - 1].B		+	PHI_0;
			B_B		=	GrandPhi[NB_DISP].B			+	Phi_B[(x+1)*(NB_DISP+1) + NB_DISP];	
			ML_B	=	GrandPhi[NB_DISP].M 		+	PHI_0;										
			GrandPhi[NB_DISP].M = min2(ML_ML, B_ML);  // Update : GrandPhi after computing ML_B
			GrandPhi[NB_DISP].B  = min3( ML_B , B_B , MR_B);

			Energy_B	=	GrandPhi[NB_DISP].B		+ Phi_B[x*(NB_DISP+1) + NB_DISP - 1]	+ Fi[x * (NB_DISP + 2) + NB_DISP].B;
			Energy_ML	=	GrandPhi[NB_DISP].M		+ PHI_0									+ Fi[x * (NB_DISP + 2) + NB_DISP].B;
			Energy_MR	=	GrandPhi[NB_DISP].B		+ PHI_0									+ Fi[x * (NB_DISP + 2) + NB_DISP].M;
			Energy[x * NB_DISP + NB_DISP - 1] = min3( Energy_B , Energy_ML , Energy_MR);

		}
	}

	for (x = WIDTH_PHI_B-1; x >= 0; x--)
	{
		min = Energy[x * NB_DISP];		
		DepthMap[x] = 0;
		for (dd= 1; dd < NB_DISP; dd++)
		{
			if (Energy[x * NB_DISP + dd] < min)
			{ 
				min = Energy[x * NB_DISP + dd];
				DepthMap[x] = dd  ; 
				DepthMap[x] = DepthMap[x] * (255 / NB_DISP);			
			}
		}
	}
	// patch to 0 last pixels
	for(x = WIDTH_PHI_B; x<WIDTH;x++)
	{
		DepthMap[x] = 0;
	}
}

void BP_ComputeGrandPhi(PHI_B_TYPE *Phi_B, FI_TYPE *GrandPhi)
{
	short x, dd;
	NODE_TYPE ML_ML, B_ML;
	NODE_TYPE MR_B, B_B, ML_B;

	for (dd = 0; dd < NB_DISP + 1; dd++)						// initialisation of the Fi first colomn to 0
	{
		GrandPhi[(WIDTH_PHI_B - 1)*(NB_DISP + 2) + dd + 1].M = 0;
		GrandPhi[(WIDTH_PHI_B - 1)*(NB_DISP + 2) + dd + 1].B = 0;
	}

	for (x = 0; x < WIDTH_PHI_B; x++)						// initialisation of the Fi first lign and last lign
	{
		GrandPhi[x*(NB_DISP + 2) + NB_DISP + 1].M = F_BIG;		// normalement inutile	=> pas d'après les tests !
		GrandPhi[x*(NB_DISP + 2) + NB_DISP + 1].B = F_BIG;		// normalement inutile	=> pas d'après les tests !	
		//GrandPhi[x*(NB_DISP + 2)].M = F_BIG;						// normalement inutile	=> pas d'après les tests !	
		GrandPhi[x*(NB_DISP + 2)].B = F_BIG;						// peut être inutile ? 			
	}


	for (x = WIDTH_PHI_B - 2; x >= 0; x--)
	{
		for (dd = 0; dd < HALFDISPARITY; dd++)
		{
			// compute GrandPhi message for odd disparities (d = 2*dd + 1) using previous GrandPhi and Phi_B		
			// should start with odd disparities before even ones  
			// GrandPhi->MR			
			ML_ML = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 3].M + PHI_0;
			B_ML = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 3].B + Phi_B[(x + 1)*(NB_DISP + 1) + dd * 2 + 2];
			GrandPhi[x*(NB_DISP + 2) + dd * 2 + 2].M = min2(ML_ML, B_ML);


			// GrandPhi->B
			MR_B = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 1].B + PHI_0;
			B_B = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 2].B + Phi_B[(x + 1)*(NB_DISP + 1) + dd * 2 + 1];
			ML_B = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 2].M + PHI_0;
			GrandPhi[x*(NB_DISP + 2) + dd * 2 + 2].B = min3(ML_B, B_B, MR_B);

			// compute GrandPhi message for even disparities (d = 2*dd) using previous GrandPhi and Phi_B
			// GrandPhi->MR
			ML_ML = GrandPhi[x*(NB_DISP + 2) + dd * 2 + 2].M + PHI_0;
			B_ML = GrandPhi[x*(NB_DISP + 2) + dd * 2 + 2].B + Phi_B[x*(NB_DISP + 1) + dd * 2 + 1];
			GrandPhi[x*(NB_DISP + 2) + dd * 2 + 1].M = min2(ML_ML, B_ML);

			// GrandPhi->B
			MR_B = GrandPhi[x*(NB_DISP + 2) + dd * 2].B + PHI_0;
			B_B = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 1].B + Phi_B[(x + 1)*(NB_DISP + 1) + dd * 2];
			ML_B = GrandPhi[(x + 1)*(NB_DISP + 2) + dd * 2 + 1].M + PHI_0;
			GrandPhi[x*(NB_DISP + 2) + dd * 2 + 1].B = min3(ML_B, B_B, MR_B);
		}

		// compute GrandPhi for the last disparity d = 2 * HALFDISPARITY+1
		// 2 * dd + 1 = NB_DISP
		// GrandPhi->MR
		ML_ML = GrandPhi[x*(NB_DISP + 2) + NB_DISP + 1].M + PHI_0;
		B_ML = GrandPhi[x*(NB_DISP + 2) + NB_DISP + 1].B + Phi_B[x*(NB_DISP + 1) + NB_DISP];
		GrandPhi[x*(NB_DISP + 2) + NB_DISP].M = min2(ML_ML, B_ML);

		// GrandPhi->B
		MR_B = GrandPhi[x*(NB_DISP + 2) + NB_DISP - 1].B + PHI_0;
		B_B = GrandPhi[(x + 1)*(NB_DISP + 2) + NB_DISP].B + Phi_B[(x + 1)*(NB_DISP + 1) + NB_DISP];
		ML_B = GrandPhi[(x + 1)*(NB_DISP + 2) + NB_DISP].M + PHI_0;
		GrandPhi[x*(NB_DISP + 2) + NB_DISP].B = min3(ML_B, B_B, MR_B);
	}
}

void BP_Compute_Energy2(PHI_B_TYPE *Phi_B, ENERGY_TYPE *Energy, FI_TYPE *Fi, FI_TYPE *GrandPhi, unsigned char *DepthMap)
{
	short x, dd;
	ENERGY_TYPE Energy_B, Energy_ML, Energy_MR;

	for (x = 0; x < WIDTH_PHI_B - 1; x++)
	{
		for (dd = 0; dd < NB_DISP; dd++)
		{
			Energy_B = GrandPhi[x*(NB_DISP + 2) + dd + 1].B + Phi_B[x*(NB_DISP + 1) + dd] + Fi[x * (NB_DISP + 2) + dd + 1].B;
			Energy_ML = GrandPhi[x*(NB_DISP + 2) + dd + 1].M + PHI_0 + Fi[x * (NB_DISP + 2) + dd + 1].B;
			Energy_MR = GrandPhi[x*(NB_DISP + 2) + dd + 1].B + PHI_0 + Fi[x * (NB_DISP + 2) + dd + 1].M;
			Energy[x * NB_DISP + dd] = min3(Energy_B, Energy_ML, Energy_MR);
		}
	}
	for (dd = 0; dd < NB_DISP; dd++)						// compute energies for the last column "WIDTH_PHI_B"
	{
		Energy_B = Phi_B[(WIDTH_PHI_B - 1)*(NB_DISP + 1) + dd] + Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].B;
		Energy_ML = PHI_0 + Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].B;
		Energy_MR = PHI_0 + Fi[(WIDTH_PHI_B - 1) * (NB_DISP + 2) + dd].M;
		Energy[(WIDTH_PHI_B - 1)* NB_DISP + dd] = min3(Energy_B, Energy_ML, Energy_MR);
	}
}


void DispSelect(ENERGY_TYPE *Energy, unsigned char *DepthMap)
{
	short x, dd;
	ENERGY_TYPE min;

	for (x = 0; x < WIDTH_PHI_B; x++)
	{
		min = Energy[x * NB_DISP];
		DepthMap[x] = 0;
		for (dd = 1; dd < NB_DISP; dd++)
		{
			if (Energy[x * NB_DISP + dd] < min)
			{
				min = Energy[x * NB_DISP + dd];
				DepthMap[x] = dd;
				DepthMap[x] = DepthMap[x] * (255 / NB_DISP);
			}
		}
	}
}

void grayScaleConversion(int height, int width, unsigned char *rgbLeft, unsigned char *rgbRight, short *grayLeft, short *grayRight){
	int idxPxl;
	int write = 0;
	for(idxPxl = 0; idxPxl < 3*height*width; idxPxl+=3) // well let's use the fpu...
	{
		#if 0
		const float cr=0.2126f;
		const float cg=0.7152f;
		const float cb=0.0722f;
		grayLeft[write] = (int) (cr*(float)rgbLeft[idxPxl+0] + cg*(float)rgbLeft[idxPxl+1] + cb*(float)rgbLeft[idxPxl+2]);
		grayRight[write] = (int) (cr*(float)rgbRight[idxPxl+0] + cg*(float)rgbRight[idxPxl+1] + cb*(float)rgbRight[idxPxl+2]);
		#endif
		#if 1
		grayLeft[write] = abs(rgbLeft[idxPxl+0]) + abs(rgbLeft[idxPxl+1]) + abs(rgbLeft[idxPxl+2]);
		grayRight[write] = abs(rgbRight[idxPxl+0]) + abs(rgbRight[idxPxl+1]) + abs(rgbRight[idxPxl+2]);
		#endif
		write++;
	}
}

#if 0
void BP1D_image(unsigned char *leftR, unsigned char *leftG, unsigned char *leftB, unsigned char *rightR, unsigned char *rightG, unsigned char *rightB, unsigned char *disparity)
{
	int i;
	PHI_B_TYPE	*Phi_B_message = malloc(sizeof(PHI_B_TYPE) * WIDTH_PHI_B * (NB_DISP + 1));
	assert(Phi_B_message != NULL);
	FI_TYPE		*Fi = malloc(sizeof(FI_TYPE) *WIDTH_PHI_B * (NB_DISP + 2));
	assert(Fi != NULL);
	ENERGY_TYPE *Energy_values = malloc(sizeof(ENERGY_TYPE) * WIDTH_PHI_B * NB_DISP);
	assert(Energy_values != NULL);
	printf("PHI_B_TYPE %d FI_TYPE %d ENERGY_TYPE %d\n", sizeof(PHI_B_TYPE) * WIDTH_PHI_B * (NB_DISP + 1), sizeof(FI_TYPE) *WIDTH_PHI_B * (NB_DISP + 2), sizeof(ENERGY_TYPE) * WIDTH_PHI_B * NB_DISP);
	//int max_val = 0;

	for (i = 0; i < HEIGHT; i++)
	{
		BP_ComputePhi_B(leftR + i*WIDTH + SHIFT, leftG + i*WIDTH + SHIFT, leftB + i*WIDTH + SHIFT, rightR + i*WIDTH, rightG + i*WIDTH, rightB + i*WIDTH, Phi_B_message);
		BP_ComputeFi(Phi_B_message, Fi);
		BP_Compute_Energy(Phi_B_message, Energy_values, Fi, disparity + i*WIDTH);
		//printf("IO%d lign %d / %d\n", __k1_get_cluster_id()/192, i, HEIGHT);
	}
	free(Phi_B_message);
	free(Fi);
	free(Energy_values);
}
#endif

void BP_ComputeFiEnergy(PHI_B_TYPE *Phi_B, unsigned char *disparity)
{
	//printf("size %d byte type %d\n", WIDTH_PHI_B * (NB_DISP + 1));
	FI_TYPE		*Fi = malloc(sizeof(FI_TYPE) *WIDTH_PHI_B * (NB_DISP + 2));
	assert(Fi != NULL);
	ENERGY_TYPE *Energy_values = malloc(sizeof(ENERGY_TYPE) * WIDTH_PHI_B * NB_DISP);
	assert(Energy_values != NULL);
	BP_ComputeFi(Phi_B, Fi);
	BP_Compute_Energy(Phi_B, Energy_values, Fi, disparity);
	free(Fi);
	free(Energy_values);
}

