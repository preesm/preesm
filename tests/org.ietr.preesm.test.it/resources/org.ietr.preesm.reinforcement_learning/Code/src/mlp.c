//
// Created by farresti on 03/11/17.
//

// math lib
#include <math.h>
// std libs
#include <stdio.h>
#include <string.h>
// file header
#include "../include/mlp.h"
#include "../include/common.h"

void layer(int input_size, int layer_size,
           IN int *valid,
           IN float *weights, IN float *bias_values, IN float *input,
           OUT float *output) {
    if (*valid == 0) {
        for (int i = 0; i < layer_size; ++i) {
            output[i] = 0;
        }
        return;
    }
    /*
     * Structure of layer in a MLP:
     *
     * input:       -> N tokens with N the number of input to the layer
     * output:      -> O tokens with O the number of neurons of the layer
     *
     * weights:     -> Matrix of dimension N*O tokens
     * bias_values: -> Vector of O tokens
     *
     */

    // Performs the matrix product between the input vector and the weight matrix
    int offset_neuron = 0;
    for (int n = 0; n < layer_size; ++n) {
        float neuron = bias_values[n];
        for (int i = 0; i < input_size; ++i) {
            neuron += input[i] * weights[offset_neuron + i];
        }
        output[n] = neuron;
        offset_neuron += input_size;
    }
}

void neuron(int input_size,
            IN float *input, IN float *weights, IN float *bias_values, IN int *valid,
            OUT float *output) {
    if (*valid == 0) {
        (*output) = 0;
    } else {
        float result = bias_values[0];
        /*
         * This compute the output of neuron[i] of a layer of a MLP neural network
         *
         * With n input values, weights array is constructed this way:
         *      weight[0] -> weight from input[0] to neuron[i]
         *      weight[1] -> weight from input[1] to neuron[i]
         *      ...
         *      weight[n] -> weight from input[n] to neuron[i]
         *
         */
        for (int wi = 0; wi < input_size; ++wi) {
            result += input[wi] * weights[wi];
        }
        (*output) = result;
    }
}


/************************************/
/********  ACTIVATION FUNC **********/
/************************************/

void activateTanHyperbolic(IN float *input, IN int *valid,
                           OUT float *output) {
    if (*valid == 0) {
        output[0] = 0;
    } else {
        output[0] = (float)(tanh((double)(input[0])));
    }
}

void derivativeTanHyperbolic(IN float *input, IN int *valid,
                             OUT float *output) {
    if (*valid == 0) {
        output[0] = 0;
    } else {
        float f_x = (float)(tanh((double)(input[0])));
        output[0] = 1 - (f_x * f_x);
    }
}

void activateReLU(IN float *input,
                  OUT float *output) {
    if (input[0] < 0) {
        output[0] = 0.f;
    } else {
        output[0] = input[0];
    }
}

void derivativeReLU(IN float *input,
                    OUT float *output) {
    if (input[0] < 0) {
        output[0] = 0.f;
    } else {
        output[0] = 1.f;
    }
}

void activateSoftSign(IN float *input,
                      OUT float *output) {
    output[0] = input[0] / (1.f + ABS(input[0]));
}


void derivativeSoftSign(IN float *input,
                        OUT float *output) {
    float den = (1.f + ABS(input[0]));
    output[0] = 1.f / (den * den);
}

void activateLogistic(IN float *input,
                      OUT float *output) {
    output[0] = 1 / (1.f + (float)(exp((double)(-input[0]))));
}

void derivativeLogistic(IN float *input,
                        OUT float *output) {
    float f_x = 1 / (1.f + (float)(exp((double)(-input[0]))));
    output[0] = f_x * (1  - f_x);
}


void activateLinear(IN float *input,
                    OUT float *output) {
    output[0] = input[0];
}

void derivativeLinear(IN float *input,
                      OUT float *output) {
    output[0] = 1;
}




/************************************/
/*********  TRAINING FUNC ***********/
/************************************/

void computeLayerBackPropError(int layer_size, int next_layer_size,
                               IN int *valid, IN float *derivative_values, IN float *next_layer_errors, IN float *next_layer_weights,
                               OUT float *errors) {
    if (*valid == 0) {
        return;
    } else {
        for (int j = 0; j < layer_size; ++j) {
            float weighted_backprop_error = 0.f;
            for (int k = 0; k < next_layer_size; ++k) {
                /* The weights are arranged in following order:
                 *     l = layer_size
                 *     n = l - 1
                 *     ln = next_layer_size
                 *     k = ln - 1
                 *
                 *     weight[0]          -> weight from node[0] of layer (l) to node[0] of layer (l + 1)
                 *     weight[1]          -> weight from node[1] of layer (l) to node[0] of layer (l + 1)
                 *     ...
                 *     weight[n]          -> weight from node[n] of layer (l) to node[0] of layer (l + 1)
                 *     weight[n + 1]      -> weight from node[0] of layer (l) to node[1] of layer (l + 1)
                 *     ...
                 *     weight[l * ln - 1] -> weight from node[n] of layer (l) to node[k] of layer (l + 1)
                 *
                 */
                weighted_backprop_error += next_layer_errors[k] * next_layer_weights[j + k * layer_size];
            }
            errors[j] = derivative_values[j] * weighted_backprop_error;
        }
    }
}

void computeOutputError(int output_size,
                        IN int *valid, IN float *derivative_values, IN float *predicted, IN float *target,
                        OUT float *errors) {
    if (*valid == 0) {
        return;
    }
    for (int j = 0; j < output_size; ++j) {
        errors[j] = 2.f * (predicted[j] - target[j]) *derivative_values[j];
    }
}


void lossMSE(int size,
             IN float *targets, IN float *predictions,
             OUT double *mse_output) {
    // Compute element wise (label - prediction) * (label - prediction)
    double mse = 0;
    for (int i = 0; i < size; ++i) {
        mse += (double)(predictions[i] - targets[i]) * (double)(predictions[i] - targets[i]);
    }
    mse_output[0] = mse / 2.;
}


void computeWeightsGradients(int input_size, int layer_size,
                             IN float *errors, IN float *inputs, IN int *valid,
                             OUT float *gradients) {
    if (*valid == 0) {
        return;
    }
    /* Gradients are computed as follows:
     *
     *     G(wij) = Sj * Ai
     *
     *     where:
     *        i      -> index of input to current layer (l)
     *        j      -> index of node in current layer (l)
     *        G(wij) -> gradients of weight i, j
     *        wij    -> weight connecting input i from layer (l - 1) to node j in layer (l)
     *        Sj     -> sigmas of node j (see computeLayerBackPropError for more on that)
     *        Ai     -> input i of layer (l)
     *
     *     The gradients are stored in the same way the weights are:
     *        gradients[0]     -> gradient of weight[0] (i.e weight connecting input[0] to node[0])
     *        gradients[1]     -> gradient of weight[1] (i.e weight connecting input[1] to node[0])
     *        ...
     *        gradients[n]     -> gradient of weight[n] (i.e weight connecting input[n] to node[0])
     *        gradients[n + 1] -> gradient of weight[n + 1] (i.e weight connecting input[0] to node[1])
     *        ...
     *        gradients[m]     -> gradient of weight[m] (i.e weight connecting input[n] to node[l])
     *
     */
    for (int j = 0; j < layer_size; ++j) {
        for (int i = 0; i < input_size; ++i) {
            gradients[i + j * input_size] = errors[j] * inputs[i];
        }
    }
}

void applyAdamOptimizer(int size,
                        IN int *valid, IN float *learning_rate, IN double *betas, IN double *epsilon,
                        IN float *param_in, IN double *fo_moment_in, IN double *so_moment_in, IN float *gradients,
                        OUT float *param_out, OUT double *fo_moment_out, OUT double *so_moment_out) {
    if (*valid == 0) {
        memcpy(param_out, param_in, size * sizeof(double));
        memcpy(fo_moment_out, fo_moment_in, size * sizeof(double));
        memcpy(so_moment_out, so_moment_in, size * sizeof(double));
    } else {
        double beta1 = betas[0];
        double beta2 = betas[1];
        double beta1_pow_t = betas[2];
        double beta2_pow_t = betas[3];
        double norm_beta2 = sqrt(1. - beta2_pow_t);
        double epsilon_t = epsilon[0] * norm_beta2;
        double lr = learning_rate[0] * norm_beta2 / (1. - beta1_pow_t); // Optimization, see original papers

        for (int i = 0; i < size; ++i) {
            // Updates moments
            double g = gradients[i];
            fo_moment_out[i] = fo_moment_in[i] * beta1 + (1 - beta1) * g;     // Biased first order moment estimate
            so_moment_out[i] = so_moment_in[i] * beta2 + (1 - beta2) * g * g; // Biased second raw order moment estimate

            param_out[i] = param_in[i] - (float)(lr * fo_moment_out[i] / (epsilon_t + sqrt(so_moment_out[i])));
        }
    }
}

void adamUpdateBetas(IN double *betas_in, IN int *valid,
                     OUT double *betas_out) {
    if (*valid == 0) {
        betas_out[0] = betas_in[0];
        betas_out[1] = betas_in[1];
        betas_out[2] = betas_in[2];
        betas_out[3] = betas_in[3];
    } else {
        // Update betas powered
        betas_out[0] = betas_in[0]; // Save B1
        betas_out[1] = betas_in[1]; // Save B2
        betas_out[2] = betas_in[2] * betas_in[0]; // Update B1^t
        betas_out[3] = betas_in[3] * betas_in[1]; // Update B2^t
    }
}


void initAdam(double *betas) {
    betas[0] = 0.9;
    betas[1] = 0.999;
    betas[2] = betas[0];
    betas[3] = betas[1];
}

void adamEpsilonGen(OUT double *epsilon) {
    epsilon[0] = 1e-8;
}




