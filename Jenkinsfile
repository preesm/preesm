
def javaVersion = "1.8"
def javaToolID = "JDK-${javaVersion}"

def mavenVersion = "3.5.0"
def mavenToolID = "Maven-${mavenVersion}"
def mavenOpts = "-e"

// tell Jenkins to remove 7 days old artifacts/builds and keep only 7 last ones
properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '7', artifactNumToKeepStr: '7', daysToKeepStr: '7', numToKeepStr: '7']]]);

node {
	def javaTool = tool javaToolID
	def mavenTool = tool mavenToolID

	withEnv(["JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${env.PATH}"]) {
		stage ('Fetch Source Code') {
			cleanWs()
			// Checkout code from repository
			checkout scm
		}
		stage ('Checkstyle') {
			sh "java -jar releng/hooks/checkstyle-7.6.1-all.jar -c releng/VAADER_checkstyle.xml plugins/"
			stash excludes: '**/.git/**', name: 'sourceCode'
			cleanWs()
		}
	}
}

//parallel 'Build and Package':{
	node {
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv(["JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${env.PATH}"]) {
			stage ('Build and Package') {
				cleanWs()
				unstash 'sourceCode'
				sh "mvn --offline ${mavenOpts} package"
				cleanWs()
			}
		}
	}
//}, 'Test and Code Quality':{
	node {
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv(["JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${env.PATH}"]) {
			stage ('Test and Code Quality') {
				cleanWs()
				unstash 'sourceCode'
				
				// actually wants verify, but use install so that code quality analyses do not need to rebuild everything
				sh "mvn --offline ${mavenOpts} verify findbugs:findbugs sonar:sonar"
				
				junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
				step([$class: 'JacocoPublisher'])
				findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
				cleanWs()
			}
		}
	}
//}, 'Check Releng Package':{
	node {
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv(["JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${env.PATH}"]) {
			stage ('Check Releng Package') {
				cleanWs()
				unstash 'sourceCode'
				
				// final stage to check that the products and site can be packaged
				// noneed to redo all tests there
				sh "mvn --offline ${mavenOpts} -Dmaven.test.skip=true -P releng package"
				cleanWs()
			}
		}
	}


