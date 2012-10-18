/*
 * a9.h
 *
 *  Created on: 9 mai 2012
 *      Author: student
 */

#ifndef A9_H_
#define A9_H_

#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>
#include <stdio.h>
#include <memory.h>
#include <imageIO.h>

#include "src/orcc_types.h"
#include "src/actor.h"

int sems_init(sem_t *sem, int number);

#endif /* A9_H_ */
