#include "jobheader.h"
#include "JobQueue.h"

#ifndef SLAVE
#define SLAVE

class Slave {
	private :
		int id;
		JobQueue* jobQueue;
	public :
		Slave();
		void launch(void);
		void setId(int inputId);
		~Slave();

};

#endif