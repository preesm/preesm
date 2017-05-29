

// how many time should we retry resolving dependencies
retryResolveCount = 4

def javaVersion = "1.8"
def javaToolID = "JDK-${javaVersion}"

def mavenVersion = "3.5.0"
def mavenToolID = "Maven-${mavenVersion}"
def mavenOpts = "-e -Dmaven.repo.local=m2-repository -T 1C"
def mavenEnvOpt = "MAVEN_OPT=-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

// tell Jenkins to remove 7 days old artifacts/builds and keep only 7 last ones
properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '7', artifactNumToKeepStr: '7', daysToKeepStr: '7', numToKeepStr: '7']]]);

node {
	cleanWs()

	def javaTool = tool javaToolID
	def mavenTool = tool mavenToolID

	withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
		stage ('Fetch Source Code') {
			// Checkout code from repository
			checkout scm
		}
		stage ('Checkstyle') {
			sh "java -jar releng/hooks/checkstyle-7.6.1-all.jar -c releng/VAADER_checkstyle.xml plugins/"
		}
		stage ('Resolve Maven Dependencies') {
			retry(retryResolveCount) {
				// resolve Maven dependencies (jars, plugins) for all modules
				sh "mvn ${mavenOpts} -P releng dependency:go-offline -Dtycho.mode=maven"
			}
		}
		stage ('Resolve P2 Dependencies') {
			// Resolve P2 dependencies
			// note: help:help with arg -q makes a "nop" goal for maven
			// see https://stackoverflow.com/a/27020792/3876938
			// We have to call maven with a nop goal to simply load the
			// tycho P2 resolver that will load all required dependencies
			// This will allow to run next stages in offline mode
			retry(retryResolveCount) {
				sh "mvn ${mavenOpts} -P releng help:help"
			}
		}

		stage ('Build, Package and run Findbugs') {
			sh "mvn --offline ${mavenOpts} package findbugs:findbugs"
			findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
		}

		stage ('Test and Compute Code Coverage') {
			// run tests and findbugs
			// note: findbugs need everything packaged
			sh "mvn --offline -fae ${mavenOpts} verify "

			// publish test results, code coverage and finbugs results
			junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
			step([$class: 'JacocoPublisher'])
		}

		stage ('Run Sonar') {
			// run sonar
			// note: run on same node to get test results and findbugs reports
			sh "mvn --offline ${mavenOpts} sonar:sonar"
		}

		stage ('Check Packaging') {
			// final stage to check that the products and site can be packaged
			// noneed to redo all tests there
			sh "mvn --offline ${mavenOpts} -Dmaven.test.skip=true -P releng package"
		}
	}
	cleanWs()
}

