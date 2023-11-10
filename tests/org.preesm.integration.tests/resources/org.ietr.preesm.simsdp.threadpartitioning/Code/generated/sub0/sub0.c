/**
 * @file sub0.c
 * @generated by CPrinter
 * @date Thu Nov 02 17:45:09 CET 2023
 *
 */
// no monitoring by default
#define _PREESM_NBTHREADS_ 3
#define _PREESM_MAIN_THREAD_ 0

// application dependent includes
#include "preesm_gen.h"

// Declare computation thread functions
void* computationThread_Core0(void *arg);
void* computationThread_Core1(void *arg);
void* computationThread_Core2(void *arg);

#ifndef _WIN32
#include <execinfo.h>
#endif
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>

#ifndef _WIN32
void handler(int sig) {
  void *array[30];
  size_t size;
  size = backtrace(array, 30);
  fprintf(stderr, "Error: signal %d:\n", sig);
  backtrace_symbols_fd(array, size, STDERR_FILENO);
  exit(1);
}
#endif

pthread_barrier_t iter_barrier;
int preesmStopThreads;

#ifdef PREESM_MD5_UPDATE
		struct rk_sema preesmPrintSema;
#endif

unsigned int launch(unsigned int core_id, pthread_t *thread, void* (*start_routine)(void*)) {

  // init pthread attributes
  pthread_attr_t attr;
  pthread_attr_init(&attr);

#ifndef PREESM_NO_AFFINITY
#ifdef _WIN32
			SYSTEM_INFO sysinfo;
			GetSystemInfo(&sysinfo);
			unsigned int numCPU = sysinfo.dwNumberOfProcessors;
		#else
  unsigned int numCPU = sysconf(_SC_NPROCESSORS_ONLN);
#endif

  // check CPU id is valid
  if (core_id >= numCPU) {
    // leave attribute uninitialized
    printf(
        "** Warning: thread %d will not be set with specific core affinity \n   due to the lack of available dedicated cores.\n",
        core_id);
  } else {
#if defined __APPLE__ || defined _WIN32
				// NOT SUPPORTED
		#else
    // init cpuset struct
    cpu_set_t cpuset;
    CPU_ZERO(&cpuset);
    CPU_SET(core_id, &cpuset);

    // set pthread affinity
    pthread_attr_setaffinity_np(&attr, sizeof(cpuset), &cpuset);
#endif
  }
#endif

  // create thread
  pthread_create(thread, &attr, start_routine, NULL);
  return 0;
}

void sub0(double out_0__in__0double out_1_1__in__0,double out_3_3__in__1,double out_4_4__in__1,double out_2_2__in__0,double out_3_3__in__0,double out_4_4__in__0,double out_1_1_3__in__1,double out_1_1_3__in__0,double out_1__in__0) {
#ifndef _WIN32
  signal(SIGSEGV, handler);
  signal(SIGPIPE, handler);
#endif
  // Set affinity of main thread to proper core ID
#ifndef PREESM_NO_AFFINITY
#if defined __APPLE__ || defined _WIN32
			// NOT SUPPORTED
		#else
  cpu_set_t cpuset;
  CPU_ZERO(&cpuset);
  CPU_SET(_PREESM_MAIN_THREAD_, &cpuset);
  sched_setaffinity(getpid(), sizeof(cpuset), &cpuset);

#endif
#endif

  // Declaring thread pointers
  pthread_t coreThreads[_PREESM_NBTHREADS_];
  void *(*coreThreadComputations[_PREESM_NBTHREADS_])(void *) = {
    &computationThread_Core0, &computationThread_Core1, &computationThread_Core2
  };

#ifdef PREESM_VERBOSE
			printf("Launched main\n");
		#endif
  // Creating a synchronization barrier
  preesmStopThreads = 1;
  pthread_barrier_init(&iter_barrier, NULL, _PREESM_NBTHREADS_);
#ifdef PREESM_MD5_UPDATE
			rk_sema_init(&preesmPrintSema, 1);
#endif
  communicationInit();

  // Creating threads
  for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
    if (i != _PREESM_MAIN_THREAD_) {
      if(launch(i,&coreThreads[i],coreThreadComputations[i])) {
        printf("Error: could not launch thread %d\n",i);
        return 1;
      }
    }
  }

  // run main operator code in this thread
  coreThreadComputations[_PREESM_MAIN_THREAD_](NULL);

  // Waiting for thread terminations
  for (int i = 0; i < _PREESM_NBTHREADS_; i++) {
    if (i != _PREESM_MAIN_THREAD_) {
      pthread_join(coreThreads[i], NULL);
    }
  }

  return 0;
}
