//
// Created by farresti on 03/11/17.
//

#ifndef MLP_H
#define MLP_H

#include "preesm.h"

/**
 * @brief Generic layer actor for a MLP.
 *        This generates the raw output (i.e before activation) of each neuron.
 *
 * @param input_size   Number of inputs to the layer
 * @param layer_size   Number of neurons composing current layer
 * @param input        Input value vector of size "input_size"
 * @param weights      Weights matrix of the layer (size = input_size * layer_size)
 * @param bias_values  Bias vector values of the layer (size=layer_size)
 * @param output  Output value vector of size "output_size"
 */
void layer(int input_size, int layer_size,
           IN int *valid,
           IN float *input, IN float *weights, IN float *bias_values,
           OUT float *output);


/**
 * @brief Generic neuron actor for MLP
 *
 * This compute the output of neuron[i] of a hidden layer of a MLP neural network
 *
 * With n input values, weights array is constructed this way:
 *      weight[0] -> weight from input[0] to neuron[i]
 *      weight[1] -> weight from input[1] to neuron[i]
 *      ...
 *      weight[n] -> weight from input[n] to neuron[i]
 *
 * @param input_size  Size of the previous layer
 * @param input       Input values (output of the previous layer)
 * @param weights     Weights associated to the inputs
 * @param bias_values Bias values associated to the neuron
 * @param output      Response of the neuron to the inputs
 */
void neuron(int input_size,
            IN float *input, IN float *weights, IN float *bias_values, IN int *valid,
            OUT float *output);

/**
 * @brief Hyperbolic tangent activation function
 *
 *                            2
 *    f(x) = tanh(x) = (--------------) - 1
 *                       1 + exp(-2x)
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void activateTanHyperbolic(IN float *input, IN int *valid,
                           OUT float *output);

/**
 * @brief Hyperbolic tangent derivative function
 *
 *                           2
 *    f(x) = tanh(x) = (--------------) - 1
 *                       1 + exp(-2x)
 *    f'(x) = 1 - (f(x))^2
 *
 * @param input  Input value
 * @param output Activated output of a neuron
 */
void derivativeTanHyperbolic(IN float *input, IN int *valid,
                             OUT float *output);

/**
 * @brief Rectified Linear Unit activation function
 *
 *            | 0, x < 0
 *     f(x) = |
 *            | x, else
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void activateReLU(IN float *input,
                  OUT float *output);

/**
 * @brief Rectified Linear Unit derivative function
 *
 *            | 0, x < 0
 *     f(x) = |
 *            | 1, else
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void derivativeReLU(IN float *input,
                    OUT float *output);

/**
 * @brief Soft sign activation function
 *
 *                 x
 *     f(x) = ------------
 *             1 + abs(x)
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void activateSoftSign(IN float *input,
                      OUT float *output);

/**
 * @brief Soft sign derivative function
 *
 *                    1
 *     f'(x) = ----------------
 *              (1 + abs(x))^2
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void derivativeSoftSign(IN float *input,
                        OUT float *output);

/**
 * @brief Logistic activation function
 *
 *                 1
 *     f(x) = ------------
 *             1 + exp(-x)
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void activateLogistic(IN float *input,
                      OUT float *output);


/**
 * @brief Logistic derivative function
 *
 *                 1
 *     f(x) = ------------
 *             1 + exp(-x)
 *
 *     f'(x) = f(x) * (1 - f(x))
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void derivativeLogistic(IN float *input,
                        OUT float *output);


/**
 * @brief Linear activation function
 *
 *     f(x) = x
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void activateLinear(IN float *input,
                    OUT float *output);


/**
 * @brief Linear derivative function
 *
 *     f(x) = x
 *
 *     f'(x) = 1
 *
 * @param input  Raw output of a neuron
 * @param output Activated output of a neuron
 */
void derivativeLinear(IN float *input,
                      OUT float *output);


/**
 * @brief Compute sigma values of a layer. The sigma value is used to compute the gradient
 *        of the weights of a layer.
 *
 *        s_j = g_j'(z_j) * sum(s_k * w_jk)
 *
 *        where:
 *        - j:    index of a node in the current layer l
 *        - g_j:  activation function of node j. (thus g_j' is the derivative of this function)
 *        - z_j:  input to node j. -> z_j = b_j + sum(a_i * w_ij)
 *        - b_j:  bias value of node j in layer l
 *        - a_i:  output value of node i in layer (l - 1). -> a_i = g_i(z_i)
 *        - w_ij: weight connecting node i in layer (l - 1) to node j in layer l
 *        - w_jk: weight connecting node j in layer l to node k in layer (l + 1)
 *        - s_k:  sigma value of node k in layer (l + 1)
 *
 *
 * @param layer_size          Size of the current layer (l).
 * @param next_layer_size     Size of the next layer (l + 1).
 * @param derivative_values   Input vector of the pre-computed derivative values (g_j'(z_j)). Size of (l).
 * @param next_layer_errors   Input vector of errors values of layer (l + 1). Size of (l + 1).
 * @param next_layer_weights  Input vector of weights connecting layer (l) to layer (l + 1). Size of (l) * (l + 1).
 * @param errors              Output vector of layer (l) errors values.
 */
void computeLayerBackPropError(int layer_size, int next_layer_size,
                               IN int *valid, IN float *derivative_values, IN float *next_layer_errors, IN float *next_layer_weights,
                               OUT float *errors);

/**
 * @brief Compute sigma values of the output layer. The sigma value is used to compute the gradient
 *        of the weights of a layer.
 *
 *        s_k = g_k'(z_k) * (a_k - t_k)
 *
 *        where:
 *        - k:    index of a node in the current layer l
 *        - g_k:  activation function of node k. (thus g_k' is the derivative of this function)
 *        - z_k:  input to node k. -> z_k = b_k + sum(a_j * w_jk)
 *        - b_k:  bias value of node k in layer l
 *        - a_j:  output value of node j in layer (l - 1). -> a_j = g_j(z_j)
 *        - w_jk: weight connecting node j in layer (l - 1) to node k in layer l
 *        - t_k:  target value for node k.
 *
 *
 * @param output_size         Size of the output layer (l).
 * @param derivative_values   Input vector of the pre-computed derivative values (g_k'(z_k)). Size of (l).
 * @param predicted           Input vector of predicted values (a_k). Size of (l)
 * @param target              Input vector of target values (t_k).
 * @param errors              Output vector of layer (l) errors values.
 */
void computeOutputError(int output_size,
                        IN int *valid, IN float *derivative_values, IN float *predicted, IN float *target,
                        OUT float *errors);

/**
 * @brief Performs the Mean Square Error loss operation
 *
 * @param size         Size of the arrays
 * @param targets      Array filled with target values
 * @param predictions  Array filled with predicted values
 * @param mse_output   Scalar value of the MSE
 */
void lossMSE(int size,
             IN float *targets, IN float *predictions,
             OUT double *mse_output);

/**
 * @brief Compute weights' gradients for a given layer
 *
 * @param input_size   Size of the input vector to the layer
 * @param layer_size   Size of the layer
 * @param errors       Vector of sigmas (i.e propagated errors) of the layer
 * @param inputs       Vector of inputs to the layer
 * @param gradients    Output vector of gradients
 */
void computeWeightsGradients(int input_size, int layer_size,
                             IN float *errors, IN float *inputs, IN int *valid,
                             OUT float *gradients);



/**
 * @brief  Apply the Adam gradient optimizer
 *         source: KINGMA, Diederik et BA, Jimmy.
 *         Adam: A method for stochastic optimization. arXiv preprint arXiv:1412.6980, 2014.
 *
 * @param size           Size of the vectors
 * @param valid          Boolean, if 0 output vectors equal to input vectors
 * @param learning_rate  Hyper parameter of the method (default is 0.001)
 * @param betas          Hyper parameters beta1 and beta2, also including beta1^t, beta2^t for optimization
 *                       default values: beta1 = 0.9, beta2=0.999
 * @param epsilon        Hyper parameter epsilon (default is 1e-8)
 * @param param_in       Input param vector to update.
 * @param fo_moment_in   First order moment estimation.
 * @param so_moment_in   Second raw order moment estimation.
 * @param gradients      Gradient vector of current parameter.
 * @param param_out      Parameter vector updated.
 * @param fo_moment_out  First order moment estimation updated.
 * @param so_moment_out  Second raw order moment estimation updated.
 */
void applyAdamOptimizer(int size,
                        IN int *valid, IN float *learning_rate, IN double *betas, IN double *epsilon,
                        IN float *param_in, IN double *fo_moment_in, IN double *so_moment_in, IN float *gradients,
                        OUT float *param_out, OUT double *fo_moment_out, OUT double *so_moment_out);


void adamUpdateBetas(IN double *betas_in, IN int *valid,
                     OUT double *betas_out);

void initAdam(double *betas);

void adamEpsilonGen(OUT double *epsilon);


#endif //MLP_H
