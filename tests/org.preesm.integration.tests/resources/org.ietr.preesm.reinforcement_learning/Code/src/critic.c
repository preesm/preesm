//
// Created by farresti on 03/11/17.
//
// std libs
#include <stdio.h>
// file header
#include "critic.h"


void td_error(IN float *gamma_in,IN float *reward, IN float *value_state, IN float *value_next_state,
              OUT float *target, OUT float *delta) {
    (*target) = reward[0] + gamma_in[0] * value_next_state[0];
    (*delta) = (*target) - value_state[0];
}

void gammaGen(OUT float *gamma) {
    gamma[0] = DISCOUNT_FACTOR;
}

void criticLearningRateGen(OUT float *learning_rate) {
    learning_rate[0] = CRITIC_LEARNING_RATE;
}