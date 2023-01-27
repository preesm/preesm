// std libs
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// math lib
#include <math.h>
// file header
#include "actor.h"
#include "../include/common.h"



void actorWeightGenInit(int id, OUT float *weights_out, OUT float *bias_out) {
    switch(id) {
        case 0:
            weights_out[0] = 6.103481f;
            weights_out[1] = 1.893838f;
            weights_out[2] = -1.572179f;
            weights_out[3] = -5.537895f;
            weights_out[4] = -22.776283f;
            weights_out[5] = 5.881247f;
            weights_out[6] = 9.541479f;
            weights_out[7] = 0.835319f;
            weights_out[8] = -0.716378f;
            weights_out[9] = 1.838112f;
            weights_out[10] = -1.196883f;
            weights_out[11] = -0.191414f;
            weights_out[12] = 7.464032f;
            weights_out[13] = -0.540303f;
            weights_out[14] = 2.970581f;
            weights_out[15] = 4.629503f;
            weights_out[16] = -1.491894f;
            weights_out[17] = -0.326144f;
            weights_out[18] = 19.572704f;
            weights_out[19] = 4.679736f;
            weights_out[20] = 2.745120f;
            weights_out[21] = 4.864576f;
            weights_out[22] = 0.606207f;
            weights_out[23] = 0.546748f;
            weights_out[24] = -16.661718f;
            weights_out[25] = 3.891967f;
            weights_out[26] = 1.528937f;
            weights_out[27] = 15.199368f;
            weights_out[28] = 6.877313f;
            weights_out[29] = -6.355995f;
            weights_out[30] = 10.890668f;
            weights_out[31] = 1.175869f;
            weights_out[32] = 0.999845f;
            weights_out[33] = 18.462982f;
            weights_out[34] = -11.406674f;
            weights_out[35] = -2.009816f;
            weights_out[36] = 1.943656f;
            weights_out[37] = -1.155777f;
            weights_out[38] = -0.202455f;
            weights_out[39] = -7.205004f;
            weights_out[40] = -16.424196f;
            weights_out[41] = 5.741399f;
            weights_out[42] = 3.878333f;
            weights_out[43] = 1.877566f;
            weights_out[44] = 0.595556f;
            weights_out[45] = 5.965015f;
            weights_out[46] = -1.435848f;
            weights_out[47] = -0.401948f;
            weights_out[48] = 5.883052f;
            weights_out[49] = 0.894002f;
            weights_out[50] = 0.760022f;
            weights_out[51] = 16.377398f;
            weights_out[52] = 5.833582f;
            weights_out[53] = 5.615663f;
            weights_out[54] = 5.925700f;
            weights_out[55] = 0.644531f;
            weights_out[56] = -1.162811f;
            weights_out[57] = 16.239910f;
            weights_out[58] = -2.811123f;
            weights_out[59] = -3.597381f;
            bias_out[0] = -20.177616f;
            bias_out[1] = 2.497836f;
            bias_out[2] = -17.362316f;
            bias_out[3] = -15.145865f;
            bias_out[4] = -17.622143f;
            bias_out[5] = -17.146181f;
            bias_out[6] = -19.921978f;
            bias_out[7] = -17.117018f;
            bias_out[8] = -9.608946f;
            bias_out[9] = 7.162946f;
            bias_out[10] = -19.751570f;
            bias_out[11] = -18.970053f;
            bias_out[12] = -15.211739f;
            bias_out[13] = -2.018737f;
            bias_out[14] = -18.828341f;
            bias_out[15] = -17.574781f;
            bias_out[16] = -18.076864f;
            bias_out[17] = -17.510927f;
            bias_out[18] = -16.068052f;
            bias_out[19] = -16.435143f;
            break;
        case 1:
            weights_out[0] = 0.457133f;
            weights_out[1] = 0.369848f;
            weights_out[2] = -0.218368f;
            weights_out[3] = 0.439003f;
            weights_out[4] = -1.714741f;
            weights_out[5] = 0.503599f;
            weights_out[6] = -3.231074f;
            weights_out[7] = -0.616084f;
            weights_out[8] = 0.126276f;
            weights_out[9] = -0.619492f;
            weights_out[10] = -0.480364f;
            weights_out[11] = 1.302878f;
            weights_out[12] = 0.456912f;
            weights_out[13] = 0.378610f;
            weights_out[14] = -0.142403f;
            weights_out[15] = 0.451320f;
            weights_out[16] = -0.983752f;
            weights_out[17] = -0.735745f;
            weights_out[18] = 0.278474f;
            weights_out[19] = 1.930366f;
            bias_out[0] = 0.789627f;
            break;
        default:
            fprintf(stderr, "Unhandled ID init value: %d", id);
    }
}


void actionSampler(int size,
                   IN float *sigma_in, IN const float *action_in,
                   OUT float *action_out) {
    // Pre-compute constant value
    float sigma = sigma_in[0];
    for (int i = 0; i < size; ++i) {
        float mu = action_in[i];
        action_out[i] = normalSampler(mu, sigma);
    }
//    fprintf(stderr, "mu: %f sigma: %f action: %f \n", action_in[0], sigma, action_out[0]);
}

void validActor(IN float *sigma,
                OUT int *valid) {
    valid[0] = 0;
    if (sigma[0] > 0.f) {
        valid[0] = 1;
    }
}

void sigmaGen(OUT float *sigma) {
    static float sigma_static = SIGMA_GAUSSIAN;
    static int timestep = 0;
    sigma[0] = sigma_static;
    // Sigma decay

    sigma_static = (float)(timestep) * (-SIGMA_GAUSSIAN / 20000.f) + SIGMA_GAUSSIAN;
    timestep = (timestep + 1) % 20000;
    if (timestep == 0) {
        sigma_static = SIGMA_GAUSSIAN;
    }
}

void actorLearningRateGen(OUT float *learning_rate) {
    learning_rate[0] = ACTOR_LEARNING_RATE;
}


void saveNetWork(int n_layer,
                 IN int *size_layer_weights, IN int * size_layer_bias, IN float **weights, IN float **bias) {
    FILE *file = NULL;
    file = fopen("./network.txt", "wr+");
    if (file) {
        char integer[3] = "%d";
        fprintf(file, "void actorWeightGenInit(int id, OUT float *weights_out, OUT float *bias_out) {\n");
        fprintf(file, "\tswitch(id) {\n");
        int offset_weights = 0;
        int offset_bias = 0;
        for (int i = 0; i < n_layer; ++i) {
            fprintf(file, "\t\tcase %d:\n", i);
            int w_size = size_layer_weights[i];
            int b_size = size_layer_bias[i];
            for (int w = 0; w < w_size; ++w) {
                fprintf(file, "\t\t\tweights_out[%d] = %ff;\n", w, weights[i][w]);
            }
            for (int b = 0; b < b_size; ++b) {
                fprintf(file, "\t\t\tbias_out[%d] = %ff;\n", b, bias[i][b]);
            }
            fprintf(file, "\t\t\tbreak;\n");
            offset_bias += b_size;
            offset_weights += w_size;
        }
        fprintf(file, "\t\tdefault:\n");
        fprintf(file, "\t\t\tfprintf(stderr, \"Unhandled ID init value: %s\", id);\n", integer);
        fprintf(file, "\t}\n");
        fprintf(file, "}\n");
        fclose(file);
    }
}