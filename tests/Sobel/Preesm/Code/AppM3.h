#ifndef APPM3_H
#define APPM3_H

#include <ti/sysbios/BIOS.h>
#include <ti/sysbios/knl/Task.h>

#include <xdc/cfg/global.h>

#include "src/orcc_types.h"
#include "src/actor.h"

int Semaphores_init(Semaphore_Handle *sem, int number);

#endif /* APPM3_H */
