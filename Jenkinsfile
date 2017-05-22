/*

			
			echo "Looking for Java: ${javaToolID}"
			env.JAVA_HOME = tool "${javaToolID}"
			env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
			
			
			
			*/

ansiColor('xterm') {
	node {

		cleanWs()
		
		def javaVersion = "1.8"
		def javaToolID = "JDK-${javaVersion}"
		def javaTool = tool javaToolID

		def mavenVersion = "3.5.0"
		def mavenToolID = "Maven-${mavenVersion}"
		def mavenTool = tool mavenToolID

		withEnv(["JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
			echo "${env.PATH}"
			stage ('Initialize and Check code style') {
				// Checkout code from repository
				checkout scm
				
				// run initialize to make sure ${main.basedir} is set
				// initialize will call validate (see maven lifecycle reference)
				sh "mvn clean initialize checkstyle:check"
			}
			
			stage ('Build and Package') {
				sh "mvn package"
			}
			
			stage ('Test and Code Quality') {
				// actually wants verify, but use install so that code quality analyses do not need to rebuild everything
				sh "mvn verify findbugs:findbugs sonar:sonar"
				
				junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
				step([$class: 'JacocoPublisher'])
				findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
			}
			
			stage ('Releng Package') {
				// final stage to check that the products and site can be packaged
				// noneed to redo all tests there
				sh "mvn -Dmaven.test.skip=true -P releng package"
			}
		
		}
		
		cleanWs()
		
	}
}


