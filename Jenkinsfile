
def javaVersion = "1.8"
def javaToolID = "JDK-${javaVersion}"

def mavenVersion = "3.5.0"
def mavenToolID = "Maven-${mavenVersion}"
def mavenOpts = "-e -Dmaven.repo.local=m2-repository"
def mavenEnvOpt = "MAVEN_OPT=-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

def sourceAndRepoStash = 'stashone'
def sourcePackageAndRepoStash = 'stashtwo'

// tell Jenkins to remove 7 days old artifacts/builds and keep only 7 last ones
properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '7', artifactNumToKeepStr: '7', daysToKeepStr: '7', numToKeepStr: '7']]]);

node {
	def javaTool = tool javaToolID
	def mavenTool = tool mavenToolID

	withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
		stage ('Fetch Source Code') {
			cleanWs()
			// Checkout code from repository
			checkout scm
		}
		stage ('Checkstyle') {
			sh "java -jar releng/hooks/checkstyle-7.6.1-all.jar -c releng/VAADER_checkstyle.xml plugins/"
		}
		stage ('Resolve Maven Dependencies') {
			// resolve Maven dependencies (jars, plugins) for all modules
			sh "mvn ${mavenOpts} -T 2C -P releng dependency:go-offline -Dtycho.mode=maven"
		}
		stage ('Resolve P2 Dependencies') {
			// Resolve P2 dependencies
			// note: help:help with arg -q makes a "nop" goal for maven
			// see https://stackoverflow.com/a/27020792/3876938
			// We have to call maven with a nop goal to simply load the
			// tycho P2 resolver that will load all required dependencies
			// This will allow to run next stages in offline mode
			sh "mvn ${mavenOpts} -T 8C -P releng help:help"
			stash excludes: '**/.git/**', name: sourceAndRepoStash
		}
		cleanWs()
	}
}

node {
	cleanWs()
	def javaTool = tool javaToolID
	def mavenTool = tool mavenToolID

	withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
		stage ('Build and Package') {
			unstash sourceAndRepoStash
			sh "mvn --offline ${mavenOpts} package"
			stash excludes: '**/.git/**', name: sourcePackageAndRepoStash
		}
	}
	cleanWs()
}

parallel 'Test and Code Quality':{
	node {
		cleanWs()
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
			stage ('Test') {
				unstash sourcePackageAndRepoStash
				// run tests and findbugs
				// note: findbugs need everything packaged
				sh "mvn --offline -fae ${mavenOpts} verify findbugs:findbugs"
				
				// publish test results, code coverage and finbugs results
				junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
				step([$class: 'JacocoPublisher'])
				findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
			}
			
			stage ('Sonar') {
				// run sonar
				// note: run on same node to get test results and findbugs reports
				sh "mvn --offline ${mavenOpts} sonar:sonar"
			}
		}
		cleanWs()
	}
}, 'Check Packaging':{
	node {
		cleanWs()
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
			stage ('Check Packaging') {
				unstash sourcePackageAndRepoStash
				
				// final stage to check that the products and site can be packaged
				// noneed to redo all tests there
				sh "mvn --offline ${mavenOpts} -Dmaven.test.skip=true -P releng package"
			}
		}
		cleanWs()
	}
}

