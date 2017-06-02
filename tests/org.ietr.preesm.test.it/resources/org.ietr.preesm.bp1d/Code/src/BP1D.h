#ifndef BP1D_H
#define BP1D_H

void grayScaleConversion(int height, int width, unsigned char *rgbLeft, unsigned char *rgbRight, unsigned char *grayLeft, unsigned char *grayRight);

void DispSelect(ENERGY_TYPE *Energy, unsigned char *DepthMap);

void BP_Compute_Energy2(PHI_B_TYPE *Phi_B, ENERGY_TYPE *Energy, FI_TYPE *Fi, FI_TYPE *GrandPhi, unsigned char *DepthMap);

void BP_ComputeGrandPhi(PHI_B_TYPE *Phi_B, FI_TYPE *GrandPhi);

void BP_Compute_Energy(PHI_B_TYPE *Phi_B, ENERGY_TYPE *Energy, FI_TYPE *Fi, unsigned char *DepthMap);

void BP_ComputeFi(PHI_B_TYPE *Phi_B, FI_TYPE *Fi);

//void BP_ComputePhi_B(unsigned char *grayLeft, unsigned char *grayRight, PHI_B_TYPE *Phi_B);
void BP_ComputePhi_B(unsigned char *rgbLeft, unsigned char *rgbRight, PHI_B_TYPE *Phi_B);

void BP1D_image(unsigned char *leftR, unsigned char *leftG, unsigned char *leftB, unsigned char *rightR, unsigned char *rightG, unsigned char *rightB, unsigned char *disparity);

void BP_ComputeFiEnergy(PHI_B_TYPE *Phi_B, unsigned char *disparity);

#endif
