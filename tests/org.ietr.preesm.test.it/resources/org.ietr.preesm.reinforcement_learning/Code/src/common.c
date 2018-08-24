//
// Created by farresti on 30/11/17.
//

// std lib
#include <stdlib.h>
#include <stdio.h>
// math lib
#include <math.h>
// file header
#include "../include/common.h"

float normalSampler(float mu, float sigma) {
    // Gaussian function:
    //                            1                     (x - mu)²
    // PDF(x|mu, sigma) = -------------------- * exp(- ------------)
    //                     sigma * sqrt(2*pi)           2 * sigma²
    //
    // X = mu + sigma * value
    //
    // This functions uses the Box-Muller transforms
    // X = sqrt(-2 * ln(U1)) * cos(2 * pi * U2)
    // with U1 and U2 two random uniform values in [0, 1]


    // Get a random value and project it on the gaussian curve
    // Perform N sampling and take a value randomly in the sampled array
    float array_samples[N_SAMPLING_SIZE];
    float array_uniform[2];
    for (int i = 0; i < 2; ++i) {
        // Pre-compute constant value
        for (int n = 0; n < N_SAMPLING_SIZE; ++n) {
            array_samples[n] = (float)(rand()) / (float)(RAND_MAX);
        }
        // Choose uniformly a random sample among the sampled ones
        int sample = (int)((float)(N_SAMPLING_SIZE) * (float)(rand()) / (float)(RAND_MAX));
        sample = MIN(N_SAMPLING_SIZE - 1, sample);
        array_uniform[i] = array_samples[sample];
    }
    double U1 = array_uniform[0];
    double U2 = array_uniform[1];
    float U1_part = (float)(sqrt(-2. * log(U1)));
    float U2_part = (float)(cos(-2. * M_PI * U2));
    float standard_normal = U1_part * U2_part;
    return standard_normal * sigma + mu;
}

int randomSign(void) {
    // Samples 10 values
    float array_samples[10];
    for (int n = 0; n < 10; ++n) {
        float value = (float)(rand()) / (float)(RAND_MAX);
        array_samples[n] = value;
    }
    int sample = (int)(10.f * (float)(rand()) / (float)(RAND_MAX));
    sample = MIN(9, sample);
    float value = array_samples[sample];
    if (value > 0.5f) {
        return 1;
    }
    return -1;
}

void randomVectorInitializer(int size, float mu, float sigma, float *vector) {
    for (int i = 0; i < size; ++i) {
        vector[i] = normalSampler(mu, sigma);
    }
}

void constantVectorInitializer(int size, float value, float *vector) {
    for (int i = 0; i < size; ++i) {
        vector[i] = value;
    }
}

void clipValues(int size,
                IN float *input, IN float *limits,
                OUT float *output) {
    float limit_inf = limits[0];
    float limit_sup = limits[1];
    for (int i = 0; i < size; ++i) {
        output[i] = MIN(MAX(input[i], limit_inf), limit_sup);
    }
}


void valid(OUT int *valid) {
    valid[0] = 1;
}
