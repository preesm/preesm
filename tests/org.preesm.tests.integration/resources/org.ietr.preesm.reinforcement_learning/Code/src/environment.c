// std lib
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
// math lib
#include <math.h>
// time lib
#include <time.h>
// file headers
#include "../include/environment.h"
#include "../include/common.h"

extern int preesmStopThreads;

// Global Variable
float last_action = 0.f;

void envInit(float *state_angular, float *state_observation) {
    // Use a random value to determine the sign of initial values
    float sign = (float)(randomSign());
    state_angular[0] = sign * M_PI * (float)(rand()) / (float)(RAND_MAX);
    sign = (float)(randomSign());
    state_angular[1] = sign * (float)(rand()) / (float)(RAND_MAX);
    //state_angular[0] = 2.631469f;
  //  state_angular[1] = 0.818002f;
    fprintf(stderr, "Initial angular state:\n");
    fprintf(stderr, "     Angular Position: %f rad\n", state_angular[0]);
    fprintf(stderr, "     Angular Velocity: %f rad/s\n", state_angular[1]);
    state_observation[0] = (float)(cos((double)(state_angular[0])));
    state_observation[1] = (float)(sin((double)(state_angular[0])));
    state_observation[2] = state_angular[1];
    fprintf(stderr, "Initial state: %f - %f - %f\n", state_observation[0], state_observation[1], state_observation[2]);
}


void step(int state_space_size, int action_space_size, int state_angular_size,
          IN float *state_angular_in, OUT float *state_angular_out, IN float *input_actions, OUT float *state_observation, OUT float *reward) {
    static long long int timestep = 0;
    static int index_reward = 0;
    static float reward_array[REWARD_SIZE] = {0.f};
    static float episode_reward = 0.f;
    static int episode = 0;
    int is_over = 0;
    float mean_reward = 0.f;
    if (timestep % REWARD_SIZE == 0 && timestep > 0) {
        for (int i = 0; i < REWARD_SIZE; ++i) {
            mean_reward += reward_array[i];
        }
        mean_reward /= (float)(REWARD_SIZE);
        if (ABS(mean_reward) < 0.1f) {
            fprintf(stderr, "System converged in: %lld time steps.\n", timestep);
            is_over = 1;
        }
    }
    timestep++;
//    if (timestep % MAX_TIME_STEP == 0) {
//        is_over = 1;
//    }
    if (is_over) {
        fprintf(stderr, "Episode: %d\n", episode);
        fprintf(stderr, "Episode reward: %f\n", episode_reward);
        fprintf(stderr, "Time steps: %lld\n", timestep);
        index_reward = 0;
        timestep = 0;
        episode_reward = 0.f;
        episode++;
        envInit(state_angular_out, state_observation);
        return;
    }


    // Get current angular state
    float theta = state_angular_in[0];
    float angular_speed = state_angular_in[1];

    // Clip value of action
    float action_clip = MIN(MAX(input_actions[0], -MAX_TORQUE), MAX_TORQUE);
    last_action = action_clip;

    // Compute reward
    if (reward) {
        reward[0] = POW2(MODF((theta + M_PI), (2.f * M_PI)) - M_PI) + 0.1f * POW2(angular_speed) + 0.001f*(POW2(action_clip));
        reward[0] = -(reward[0]);
        reward_array[index_reward] = (*reward);
        index_reward = (index_reward + 1) % REWARD_SIZE;
        episode_reward += (*reward);
    }

    // Update angular state
    angular_speed = angular_speed + ((-3.f) * G_CONSTANT / (2.f * LENGTH_CONSTANT) * (float)(sin(theta + M_PI)) +
                                     (3.f / (MASS_CONSTANT * POW2(LENGTH_CONSTANT))) * action_clip) * TIME_DELTA;
    theta = theta + angular_speed * TIME_DELTA;
    angular_speed = MIN(MAX(angular_speed, -MAX_SPEED), MAX_SPEED);
    state_angular_out[0] = theta;
    state_angular_out[1] = angular_speed;

    // Output the observations
    state_observation[0] = (float)cos((double)theta);
    state_observation[1] = (float)sin((double)theta);
    state_observation[2] = angular_speed;
}

void step_noreward(int state_space_size, int action_space_size, int state_angular_size,
                   IN float *state_angular_in, OUT float *state_angular_out, IN float *input_actions, OUT float *state_observation) {
    step(state_space_size,
         action_space_size,
         state_angular_size,
         state_angular_in,
         state_angular_out,
         input_actions,
         state_observation, NULL);
}


void envActionLimits(OUT float *limits) {
    limits[0] = -MAX_TORQUE;
    limits[1] = MAX_TORQUE;
}
