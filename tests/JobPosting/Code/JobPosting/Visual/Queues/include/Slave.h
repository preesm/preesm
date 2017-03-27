#include "jobheader.h"
#include "JobQueue.h"

#ifndef SLAVE
#define SLAVE

/**
 A slave processor connects to one job queue of the master and receives jobs that it can process

 @author mpelcat
*/
class Slave {
	private :
		// Id of the slave
		int id;
		// Queue of pending jobs
		JobQueue* jobQueue;
	public :
		/**
		 Constructor

		 @param inputId: Id of the slave
		*/
		Slave(int inputId);
		
		/**
		 Destructor
		*/
		~Slave();

		/**
		 Launches the slave. Runs until ESC is pressed
		*/
		void launch(void);

};

#endif