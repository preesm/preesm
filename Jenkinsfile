#!groovy

// how many time should we retry resolving dependencies
retryResolveCount = 4

def javaVersion = "1.8"
def javaToolID = "JDK-${javaVersion}"

def mavenVersion = "3.5.0"
def mavenToolID = "Maven-${mavenVersion}"
def mavenOpts = "--errors --strict-checksums --batch-mode -Dmaven.repo.local=m2-repository"
def mavenEnvOpt = "MAVEN_OPT=-XX:+TieredCompilation -XX:TieredStopAtLevel=1"

// tell Jenkins to remove 7 days old artifacts/builds and keep only 7 last ones
properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '7', artifactNumToKeepStr: '7', daysToKeepStr: '7', numToKeepStr: '7']]]);

node {
	try {
		def javaTool = tool javaToolID
		def mavenTool = tool mavenToolID

		withEnv([mavenEnvOpt, "JAVA_HOME=${javaTool}", "PATH=${javaTool}/bin:${mavenTool}/bin:${env.PATH}"]) {
			stage ('Cleanup Workspace') {
				cleanWs()
			}

			stage ('Fetch Source Code') {
				// Checkout code from repository
				checkout scm
			}
			
			parallel (
				'Checkstyle': {
					stage ('Checkstyle') {
						sh "releng/run_checkstyle.sh"
					}
				}
			,
				'Validate POM': {
					stage ('Validate POM') {
						sh "mvn ${mavenOpts} -P doUpdateSite -Dtycho.mode=maven help:help -q"
					}
				}
			)

			parallel (
				'Maven Plugins': {
					stage ('Resolve Maven Dependencies') {
						retry(retryResolveCount) {
							// resolve Maven dependencies (jars, plugins) for all modules
							sh "mvn ${mavenOpts} -P doUpdateSite dependency:go-offline -Dtycho.mode=maven"
						}
					}
				}
			,
				'P2 Dependencies': {
					stage ('Resolve P2 Dependencies') {
						// Resolve P2 dependencies
						// note: help:help with arg -q makes a "nop" goal for maven
						// see https://stackoverflow.com/a/27020792/3876938
						// We have to call maven with a nop goal to simply load the
						// tycho P2 resolver that will load all required dependencies
						// This will allow to run next stages in offline mode
						retry(retryResolveCount) {
							sh "mvn ${mavenOpts} -P doUpdateSite help:help"
						}
					}
				}
			)

			stage ('Build, Package and run Findbugs') {
				sh "mvn --offline ${mavenOpts} package findbugs:findbugs"
				findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
			}

			stage ('Test and Compute Code Coverage') {
				// run tests and findbugs
				// note: findbugs need everything packaged
				// note: fail at end to gather as much traces as possible
				try {
					sh "mvn --offline --fail-at-end ${mavenOpts} verify "
				} finally {
					// publish test results, code coverage and finbugs results
					junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
					step([$class: 'JacocoPublisher'])
				}
			}

			try {
				parallel (
					'Sonar': {
						// run sonar
						// note: run on same node to get test results and findbugs reports
						// note: never fail on that stage (report warning only)
						// note: executing in parallel with 'package' should not interfere
						stage ('Run Sonar') {
							sh "mvn --offline ${mavenOpts} sonar:sonar"
						}

					}, 'Package': {
						stage ('Check Packaging') {
							// final stage to check that the products and site can be packaged
							// noneed to redo all tests there
							sh "mvn --offline ${mavenOpts} -Dmaven.test.skip=true -P doUpdateSite package"
						}
					}
				)
			} catch (err) {
				echo "Caught: ${err}"
				currentBuild.result = 'UNSTABLE'
			}
		}
	} finally {
		// whatever happens, cleanup
		stage ('Cleanup Workspace') {
			cleanWs()
		}
	}
}

