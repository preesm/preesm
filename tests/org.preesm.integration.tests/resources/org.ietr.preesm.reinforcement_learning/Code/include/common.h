//
// Created by farresti on 30/11/17.
//

#ifndef CACLA_COMMON_H
#define CACLA_COMMON_H

#include "preesm.h"

#define N_SAMPLING_SIZE 50

#ifndef M_PI
    #define M_PI 3.1415926535897932385f
#endif

#define MODF(a,b) ((a) - (b) * (int)((a) / (b)))
#define POW2(x) ((x) * (x))
#define MIN(a, b) ((a) < (b) ? (a) : (b))
#define MAX(a, b) ((a) > (b) ? (a) : (b))
#define ABS(x) ((x) < 0 ? -(x) : (x))

/**
 * @brief Samples a random variable following a normal distribution.
 *        Each distribution is parameterized by mu and
 *        sigma = sigma
 *        The behavior is as follows, N random (normal) values are sampled
 *        and then a value is chosen uniformly among these N values and returned.
 *
 * @param sigma  Standard deviation of the distribution.
 * @param mu     Center value of the distribution.
 * @return random sample following the normal law.
 */
float normalSampler(float mu, float sigma);

/**
 * @brief Functions that returns uniformly either 1 or -1.
 *        The function samples 10 uniform values and choose
 *        one uniformly in this array.
 *
 * @return -1 or 1
 */
int randomSign(void);



/**
 * @brief Initializes a vector of float with random normal values.
 *
 * @param size    Size of the vector
 * @param mu      Center value of the distribution.
 * @param sigma   Standard deviation of the distribution.
 * @param vector  Output vector initialized.
 */
void randomVectorInitializer(int size, float mu, float sigma, float *vector);

/**
 * @brief Initializes a vector of float with a constant value.
 *
 * @param size    Size of the vector
 * @param value   Constant value to initialize the vector with.
 * @param vector  Output vector initialized.
 */
void constantVectorInitializer(int size, float value, float *vector);


/**
 * @brief Clip a vector's values in given limits.
 *        The limits array should contains only two values:
 *        limits[0] = minimum value allowed for the vector.
 *        limits[1] = maximum value allowed for the vector.
 *
 * @param size    Size of the vector
 * @param input   Input vector (non clipped).
 * @param limits  Limits values.
 * @param output  Output vector (clipped).
 */
void clipValues(int size,
                IN float *input, IN float *limits,
                OUT float *output);

/**
 * @brief Actor that produces 1 each iteration.
 *        Used to validate other actor (workaround to the lack of "if" in PREESM)
 * @param valid
 */
void valid(OUT int *valid);

#endif //CACLA_COMMON_H
