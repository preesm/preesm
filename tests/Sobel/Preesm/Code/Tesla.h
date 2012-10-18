#ifndef TESLA_H
#define TESLA_H

#include <ti/sysbios/BIOS.h>
#include <xdc/cfg/global.h>

#include <imageIO.h>
#include "src/orcc_types.h"
#include "src/actor.h"

int Semaphores_init(Semaphore_Handle *sem, int number);

#endif /* TESLA_H */
