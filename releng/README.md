Preesm Maintainer Documentation
===============================

**NOTE: part of this documentation might be outdated.**

This document explains the build process of Preesm and its components (Graphiti and their dependencies), all the configuration of the release and deploy procedures, automatic documentation for tasks, website management, as well as the IDE automatic setup with custom Eclipse plugins.

Introduction
------------

Graphiti and Preesm are sets of Eclipse plugins. Their source code is hosted on GitHub (see the [Preesm team](https://github.com/preesm) page). These projects are built using [Maven](https://maven.apache.org/) and the [Tycho](https://eclipse.org/tycho/) plugins, and mostly developed using the [Eclipse IDE](https://eclipse.org/). Facilities are provided to ease the interactions between these tools. The [Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) server [Travis](https://travis-ci.org/) is in charge of building and running tests on evey push. This document describes complete interaction between all the repositories and services involved.

### Documentation

All documentation is written using Markdown. Some 

*   Online user and developer documentation is available on [preesm.github.io](https://preesm.github.io/).
*   This file describes the complete interaction between all the repositories of the [Preesm team](https://github.com/preesm) and their purpose.
*   Readme files at the root of the git repositories simply recall general and basic information, except for the [ExternalDeps](https://github.com/preesm/externaldeps) project. For this last project, the readme file explains details of the build process. Part of it is recalled in the present document.

### Git

All the source code is hosted on GitHub repositories (see [Preesm team](https://github.com/preesm)). The documentation about how to use git is already available on the Preesm website:

*   [Install and Configure git](https://preesm.github.io/docs/gitsetup/)
*   [git tips & tricks](https://preesm.github.io/docs/gittips/)

### Maven

The Preesm project builds, tests, releases and distributions are managed by [Maven](https://maven.apache.org/what-is-maven.html), along with Bash script for github release and documentation update.

*"Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information."* (see [Maven home page](https://maven.apache.org/)).

*   [Philosophy of Maven](https://maven.apache.org/background/philosophy-of-maven.html)
*   [What is Maven ?](https://maven.apache.org/what-is-maven.html)
*   [Maven in 5 minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

Please make sure to read about the [Maven Plugin Configuration](https://maven.apache.org/guides/mini/guide-configuring-plugins.html) before reading further.

In order to build Eclipse plugins using Maven, the build process heavily relies on the [Tycho](https://eclipse.org/tycho/) Maven plugins.

Please refer to [user documentation](https://preesm.github.io/docs/buildpreesm/) and [developer documentation](https://preesm.github.io/docs/devdoc/) for usage.

### Eclipse IDE

It is best to develop Preesm using the Eclipse IDE. To avoid switching from the Maven build process and the Eclipse IDE, it is possible to load Maven projects in the Eclipse IDE using the [M2Eclipse](http://www.eclipse.org/m2e/) Eclipse plugins. This set of plugins allows to effortlessly import and configure the Maven projects in the IDE, and to run Maven commands from the IDE.

Of course, it is not needed to run Maven all the time since Eclipse has everything required to build Eclipse plugins (see [PDE tools](http://www.eclipse.org/pde/)). However the final bundles should be built and deployed with Maven.

### Coding Style

Having coding standards is a must in a project developed by several people from many places around the world. Many discussions promote the use of such conventions (see links below). It ranges from simple rules, as the charset to use, to very restrictive rules, as whether or not putting the semicolon at the end of a JavaScript line. For the Preesm project, we decided to use a quite loose one.

We arbitrarily decided to fork from the [Google Java Style](https://google.github.io/styleguide/javaguide.html), and focus on the code shape, that is using spaces for indentation, where to put the bracket, what to do with empty lines, etc.; and not on naming or best practices. This allows to keep a consistent version control history, while minimizing the restriction over developer choice of implementation.

The coding style is automatically checked during the build process using the [Maven Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and within the Eclipse IDE using the [Eclipse Checkstyle plugin](http://eclipse-cs.sourceforge.net/). On top of that, we provide preference files (stored in the [coding policy project](https://github.com/preesm/preesm-maven)) that configures the [Eclipse Java Code Style](https://help.eclipse.org/oxygen/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-code-style.htm) for that coding style. Using such automatic formatting tool (triggered on save), developers can focus on the implementation, not the style.

Reading:

*   [Why I Have Given Up on Coding Standards](http://www.richardrodger.com/2012/11/03/why-i-have-given-up-on-coding-standards/) @www.richardrodger.com
*   [Why Coding Conventions?](https://msdn.microsoft.com/en-us/library/aa733744.aspx) @msdn.microsoft.com
*   [Why Coding Style Matters](https://www.smashingmagazine.com/2012/10/why-coding-style-matters/) @www.smashingmagazine.com
*   [Why You Need Coding Standards](https://www.sitepoint.com/coding-standards/) @www.sitepoint.com
*   [Why Coding Standards Matter](http://paul-m-jones.com/archives/3) @paul-m-jones.com

### Dependency Management

Graphiti and Preesm projects depend on other third party projects and libraries. Since they are sets of Eclipse plugins, their dependencies should be Eclipse plugins. Although it is possible to include plain Java archives within an Eclipse plugin source tree (see [this thread](http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin) for instance), we decided to avoid doing that and chose to take advantage of the [plugin discovery mechanism](https://wiki.eclipse.org/Equinox/p2/Discovery) integrated within Eclipse and the Maven Tycho plugin.

This dependency mechanism is very similar to the [Maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html). The main difference is the bundles and repositories type: Maven resolves plain Java jars from Maven repositories (i.e. [Nexus](http://www.sonatype.org/nexus/) or [Artifactory](https://www.jfrog.com/artifactory/)),  whereas Tycho and Eclipse resolve Eclipse plugin jars (OSGi bundles) from P2 repositories (Eclipse update-sites).

In order to use a plain Java jar using this mechanism, it first needs to be converted into an Eclipse plugin. As explained in this [article](https://www.vogella.com/tutorials/EclipseJarToPlugin/article.html), it can be done manually within the Eclipse IDE or automatically using the [p2-maven-plugin](https://github.com/reficio/p2-maven-plugin) or [Gradle](https://gradle.org/).

For Graphiti and Preesm, all the plain Java jars have been externalized in the [ExternalDeps](https://github.com/preesm/externaldeps) project. This project consists of a Maven POM file that is configured to fetch all the required jars from online Maven repositories and convert them into Eclipse plugin, before deploying them on an Eclipse update site.

Project Structure
-----------------

All the resources are located on Github.

### Git Repositories (GitHub)

All the source code for the projects is hosted on GitHub (under [Preesm team](https://github.com/preesm), omitting archived repos). Documentation is usually available in the README.md of each repository.

*   [preesm.github.io](https://github.com/preesm/preesm.github.io): Preesm website (using [Github Pages](https://pages.github.com/))
*   [preesm-maven](https://github.com/preesm/preesm-maven/): Dedicated plugins for helping release engineering, coding policy, and setting IDE config
*   [externaldeps](https://github.com/preesm/externaldeps/): Responsible for listing all Java dependencies and converting them to OSGi bundles (Eclipse plugins)
*   [dev-features](https://github.com/preesm/dev-features/): Responsible for listing all required Eclipse features required to develop Preesm/Graphiti (includes Xcore, Xtend, Eclipse Checkstyle, etc.)
*   [m2e-settings](https://github.com/preesm/m2e-settings/): Eclipse plugin to automatically load configuration stored in the coding policy/settings modules of [preesm-maven]. Documentation is outdated and does not reflect changes in the plugin since the fork.
*   [preesm](https://github.com/preesm/preesm/): source code of Preesm;
*   [graphiti](https://github.com/preesm/graphiti/): Eclipse plugin for the Workflow and Slam graphical editors;
*   [spider](https://github.com/preesm/spider/): C++ runtime for dynamic PiSDFs
*   [preesm-cli](https://github.com/preesm/preesm-cli/): Bash wrappers for calling Preesm from the command line (includes Windows batch wrapper that uses Cygwin)
*   [preesm-apps](https://github.com/preesm/preesm-apps/): Preesm use cases and tutorials
*   [preesm-snapshot-site](https://github.com/preesm/preesm-snapshot-site/): Snapshot repository for Preesm develop branch builds

#### The .gitignore Files

During the build process, many files are automatically generated. For instance, all the files under **/target** are generated with Maven and the files under **/xtend-gen** are Java files generated from Xtend source. Those files should not be added to the Git repository.

The purpose of the [.gitignore file](https://git-scm.com/docs/gitignore) is to declare a list of path patterns that will automatically be ignored by Git. If one wants to force the addition of some ignored path, the **-f** option should be appended to the git add command.

#### The .mailmap File

At the root of the Git repositories lies a file named `.mailmap`.  This files is used to associate different author names and mail addresses to one unique identity.

For instance, let say Developer John Smith commits using **jsmith** name and **jsmith@company.com**  mail at work, and using **"John Smith"** name and **john.smith@public.net** mail from home. The git log would differentiate both users whereas they refer to the same identity. To have both of them show the proper entry (let say **"John Smith"** and **john.smith@company.com**), one would have the following `.mailmap` file at the root of the git repository:
```bash
#### Format is :
####   "Proper Name" "<proper mail>" "logged Name" "<logged mail>"
John Smith john.smith@company.com jsmith jsmith@company.com
John Smith john.smith@company.com John Smith john.smith@public.net
```

Reading:

*   [git shortlog](https://git-scm.com/docs/git-shortlog#_mapping_authors)
*   [using-mailmap-to-fix-authors-list-in-git](https://stacktoheap.com/blog/2013/01/06/using-mailmap-to-fix-authors-list-in-git/)
*   [mailmap](https://github.com/git/git/blob/master/Documentation/mailmap.txt)

#### Github Policies

On the main projects, the master and develop branches of the repositories are [protected](https://help.github.com/en/articles/about-protected-branches).

### Releng Files

| File under /releng/ | Description |
|-----------------------------|--------------------------------------------|
| org.preesm.feature/ | The [Eclipse feature](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm) for end users; includes Preesm plugins |
| org.preesm.product/ | Maven module for generating the end user [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F), including Preesm feature and useful dependencies |
| org.preesm.site/ | The module responsible for generating the update site, including Preesm feature |
|||
| **build_and_test.sh** | Script for building and testing Preesm. Automatically starts X virtual frame buffer (Xvfb) for running graphic tests if binary is present. This script is called by Travis CI. | 
| copyright_template.txt | Copyright template to include in file headers. Used by the **Maven License Plugin** during **fix_header_copyright_and_authors.sh** script. |
| deploy.sh | Wraps clean build test deploy within a scripts that runs Xvfb if present. | 
| fetch-rcptt-runner.sh | RCPTT runner is bundled as a zip file of few hundreds of MB. The default Maven mirror hosting this file is slow, and causes the Travis CI build to timeout. This script fetches the runner from the configured mirror (see `<properties>` section of the parent POM file) and install it in the local Maven repository. | 
| fix_header_copyright_and_authors.sh | Bash script that replaces all source file header comment with the copyright_template.txt file content using the **Maven License Plugin**, then use the **git log** commands for replacing copyright template tokens (i.e. %%DATE%%) with proper data. |
| pom.xml | The main releng POM. |
| README.md | This file |
| release.sh | Script for release Preesm. Check authentication for uploading artifacts; call fix header script; update version and release notes; build, test, deploy; create release on Github | 
| run_checkstyle.sh | Small Bash script that calls Maven with proper arguments to check the coding policy. Calls the **Maven Checkstyle Plugin** with configuration from the [coding policy project](https://github.com/preesm/preesm-maven). |
| snapshot_predeploy.sh | Called by Travis CI when building develop branch. This script fetches the [snapshot update site of Preesm](https://github.com/preesm/preesm-snapshot-site), prepare it to integrate current built version, then push the changes. | 
| update-version.sh | Small Bash script that calls Maven with proper arguments to set a new version for all submodules. Calls the **Maven Tycho Version Plugin**. |

The **.travis.yml** file at the root of the repositories defines what needs to be done by the Travis CI service. See https://docs.travis-ci.com/

Build Process in Maven
----------------------

This section details how the Preesm project is built using Maven. Graphiti is built using a similar process to the Preesm one. For the site and products generation and deploy phases, please read the [Release Engineering](#release-engineering) section.

### Overview

The Maven build process is defined in [POM files](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html). The POM files can define build properties, reference external repositories, declare sub modules, call and configure Maven plugins, define profiles, etc.

Each Eclipse plugin in the Preesm project has its own POM file. On top of that, there are [parent POM](http://www.javavillage.in/maven-parent-pom.php) files, that define properties, Maven plugin configuration, [Maven profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html), for their [submodules](https://maven.apache.org/guides/mini/guide-multiple-modules.html).

The build and test of the project is triggered using the following command: `mvn clean verify`. This calls two Maven goals from two different [Maven Build Lifecycles](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Build_Lifecycle_Basics):

*   The **clean** goal from the *clean* lifecycle: this goal cleans the current project and all its enabled submodules. This ensures no generated file is kept between builds.
*   The **verify** goals from the *default* lifecycle: this goal validates project configuration, generates sources, checks their compliance with regard to the coding policy, compiles them, packages them and finally fires the tests.

The dependencies are all defined in MANIFEST.MF files within the Eclipse plugins and test fragments. The Tycho Maven plugin is responsible for resolving these Eclipse dependencies and fetch them from online remote P2 repositories (see below). All the Maven plugins used during the build process are available on the Maven Central repository (enabled by default, see [Super POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Super_POM)). There should be no plain Java jar dependencies (see [Dependency Management](#dependency-management)), except for forcing plugin versions.

### Dependencies

Preesm is a set a Eclipse plugins, thus its dependencies are OSGi dependencies. They are defined in the MANIFEST.MF file of every Eclipse plugin. The Tycho Maven plug-in is then responsible for resolving such dependencies during the Maven build process. There should be no `<dependencies>` section in the POM files.

Third party plugins dependencies are resolved using external P2 repositories, such as the [Eclipse project update site](http://ftp.fau.de/eclipse/releases/) for the core components of Eclipse, [TMF releases update site](http://download.eclipse.org/modeling/tmf/xtext/updates/releases/) for the Xtend runtime libraries, or the [Preesm update site](http://preesm.insa-rennes.fr/repo/) for specific third party (see [ExternalDeps](https://github.com/preesm/externaldeps) project) and Graphiti dependencies. These repositories are defined in the parent POM file (at the root of the git repository). Following is an example of such declaration.

```xml
<properties>
  <!-- ... -->
  <complete.p2.repo>http://preesm.sourceforge.net/gensite/update-site/complete/</complete.p2.repo>
  <eclipse.mirror>http://mirror.ibcp.fr/pub/eclipse/</eclipse.mirror>
</properties>
<!-- ... -->
<repositories>
  <!-- add Eclipse repository to resolve dependencies -->
  <repository>
    <id>Oxygen</id>
    <layout>p2</layout>
    <url>${eclipse.mirror}/releases/oxygen/</url>
  </repository>
  <!-- ... -->
  <!-- add Preesm repository to resolve dependencies -->
  <repository>
    <id>Complete Repo</id>
    <layout>p2</layout>
    <url>${complete.p2.repo}</url>
  </repository>
</repositories>
<!-- ... -->
```

Dependencies between submodules are resolved on the fly during the build process. Once a module has been [packaged](https://eclipse.org/tycho/sitedocs/tycho-packaging-plugin/package-plugin-mojo.html) (triggered automatically when calling the **verify** goal), it becomes available for other plugins. The Tycho plugins add implicit dependencies between submodules in order for the [Maven Reactor](https://maven.apache.org/guides/mini/guide-multiple-modules.html) to compute a valid build order for all submodules.

**Note:** If Maven is called with a goal in the default lifecycle that is before the **package** goal (for instance **compile**), the dependencies will not be resolved since modules will not be packaged. Thus the Tycho plugin will try to resolve dependencies from the [local repository](https://www.mkyong.com/maven/where-is-maven-local-repository/) and then remote ones. This will cause failure is the required version is not available or if there has been changes in the API and the version did not increase. The same failure can occur if one tries to build one of the submodules independently from the others. To circumvent this, one could first **install** (see [goal doc.](http://maven.apache.org/plugins/maven-install-plugin/)) the dependencies to the local repository, then compile or package only one submodule.

### Profiles

Two [Maven build profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) are defined in the parent POM:
.org/bugs/show_bug.cgi?id=427693)).

#### java8-doclint-disabled

Add JDK command line option to prevent Javadoc linter to throw errors when parsing the Javadoc. See https://stackoverflow.com/questions/15886209/maven-is-not-working-in-java-8-when-javadoc-tags-are-incomplete

#### os-macosx

The main purpose of this profile is to add some arguments to the JVM when running Eclipse plugin tests. The profile is automatically activated when running on Mac OSX family:
```XML
<activation>
  <os>
    <family>mac</family>
  </os>
</activation>
```
The specific argument to add is defined as follows:
`<tycho.surefire.extra.vmargs>-XstartOnFirstThread</tycho.surefire.extra.vmargs>` (see [this bug report](https://bugs.eclipse.org/bugs/show_bug.cgi?id=427693)).

#### only-eclipse

This profile is activated when a property named `m2e.version` is given to Maven. This is the case when Maven is called by the M2Eclipse Eclipse plugins (see below). The profile enables the configuration of the M2Eclipse plugin for Eclipse. It is disabled outside Eclipse because it can cause some warnings/errors during a normal Maven build.

### Phase Binding and Configuration Details

This section details what plugins are bound to which phases (including clean lifecycle and tests, but not releng profile) and their configuration and role in the build process. Take a look at their use in the POM files for more details.

#### clean

*   [maven-clean-plugin](https://maven.apache.org/plugins/maven-clean-plugin/): Add a pattern to clean the Xtend generated Java files under */xtend-gen/*.

#### initialize

*   [directory-maven-plugin](https://github.com/jdcasey/directory-maven-plugin): initialize the property **main.basedir** with the path to the parent project directory. This property is used in the JaCoCo configuration for having a consistent path to its report file from any submodule.
*   [jacoco-maven-plugin](http://www.eclemma.org/jacoco/trunk/doc/prepare-agent-mojo.html): Used for [code coverage](https://en.wikipedia.org/wiki/Code_coverage) computation. See plugin documentation. The configuration outputs the report in the target folder of the parent project. All the submodules append their report in that file. It is later used by [Sonar](#sonarqube).
*   [org.eclipse.m2e:lifecycle-mapping](http://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html): Tell the Eclipse Maven Plugin to ignore some Maven plugins. More details in the [Eclipse setup](#eclipse-setup) section. This goal is only active in Eclipse (see [only-eclipse](#only-eclipse) profile).

#### generate-sources

*   [xtend-maven-plugin](https://eclipse.org/Xtext/documentation/350_continuous_integration.html): Compiles Xtend and Xcore source files from **/xtend-src** and **/model** to Java files in **/xtend-gen** and **/ecore-gen**.

#### process-sources

*   [maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/): Checkstyle Maven plugin for checking code style compliance. Explicitly bound to **process-sources** phase. Force Checkstyle version (default version does not support Java 8 syntax). Add configuration file path from the [preesm-maven/preesm-coding-policy](https://github.com/preesm/preesm-maven/tree/master/preesm-coding-policy) module.

#### compile

*   [tycho-compiler-plugin](https://eclipse.org/tycho/sitedocs/tycho-compiler-plugin/compile-mojo.html): Compiles the Java sources using Eclipse dependencies.

#### package

*   [tycho-packaging-plugin](https://eclipse.org/tycho/sitedocs/tycho-packaging-plugin/package-plugin-mojo.html): Bundle modules as Eclipse plugins.

#### integration-test

*   [tycho-surefire-plugin](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/test-mojo.html): Fire tests within an Eclipse runtime environment. Defined in the test fragments POM. Also calls the RCPTT runner plugin.

#### deploy

*   [maven-deploy-plugin](http://maven.apache.org/plugins/maven-deploy-plugin/): deploy artifacts to the SFTP server.

Eclipse Setup
-------------

Eclipse is the preferred IDE for developing Preesm. The developer setup is detailed on the [website](https://preesm.github.io/docs/buildpreesm/). This section details the links between the Maven configuration and the Eclipse setup.

**Note:** Eclipse should not be used to package or release, except for [exporting a temporary product](https://preesm.github.io/docs/buildpreesm/#from-eclipse).

### M2Eclipse

Most of the job is done by the [M2Eclipse Eclipse plugin](http://www.eclipse.org/m2e/). This plugin allows to import Maven projects in the Eclipse workspace. It also reads the POM files and configure Eclipse projects accordingly.

Some Maven plugins are however not handled by the M2E plugin. This is the case for the **directory-maven-plugin**. This plugin should not affect the Eclipse build process, and is therefore add to the ignore list in M2E (see [initialize phase](#initialize)).

Some other Maven plugins need to be supported by Eclipse, as the Tycho plugin that sets some Eclipse plugin configuration up. Therefore we need to install some M2E extensions (called connector) to support them:

*   [M2E Tycho connector](https://github.com/tesla/m2eclipse-tycho) (installed from this [update site](http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/)): Create **.project** files with Eclipse plugin nature from the POM description;
*   [M2E Checkstyle Connector](https://github.com/m2e-code-quality/m2e-code-quality) (installed from [this update site](https://m2e-code-quality.github.io/m2e-code-quality-p2-site/)): Apply the checkstyle configuration to the Eclipse Checkstyle Plugin from the Maven Checkstyle Plugin configuration;
*   [M2E Settings Connector](https://preesm.github.io/m2e-settings/) (installed from [this update site](https://preesm.github.io/m2e-settings/site/)): Automatically sets project specific settings (formatter on save action, cleanup, encoding, ...) from the POM file.

### Installing Dependencies

The third-party dependencies must be installed through update sites. All of them are bundled with the "development features" in the **dev-features** repository.

### Eclipse Preferences

Eclipse comes with many development facilities. Among them is the code formatter. We provide an [Eclipse Preference File](https://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that comes with a formatter configuration that respects the Checkstyle coding policy and that is called upon save.

This configuration is automatically loaded using the M2E Settings Connector and settigns from the [Coding Policy plugin](https://github.com/preesm/preesm-maven).

### Running Maven from Eclipse

As aforementioned, the Eclipse IDE is best to develop with Preesm, but should not be used for packaging and releasing. Indeed the [Plugin Development Environment build tools](https://projects.eclipse.org/projects/eclipse.pde) provide all necessary components for the plugin development life. However the configuration differs from the Maven plugins (for instance the Eclipse update site builder uses a file named **site.xml** whereas the [tycho-p2-repository-plugin](http://www.eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-repository-plugin/assemble-repository-mojo.html) reads a file named **category.xml**). The release process is tuned for Maven build so that it can be called from a continuous integration platform.

The M2E Eclipse plugins come with facilities to run Maven goals from the Eclipse IDE, without having to install a local Maven distribution. This is done by running any of the imported Maven project as "Maven build" (see example [here](https://books.sonatype.com/m2eclipse-book/reference/running-sect-running-maven-builds.html)). Goals, profiles and parameters can be set in the Eclipse interface.

### Missing Source Features

As mentioned on the [Preesm website](http://preesm.sourceforge.net/website/index.php?id=working-with-dftoolsgraphiti-source), some warnings can appear when working with Graphiti and DFTools source code. This is nothing to be wary of as these "missing" features are actually automatically generated during the **package** phase by the [tycho-source-feature-plugin](https://eclipse.org/tycho/sitedocs-extras/tycho-source-feature-plugin/source-feature-mojo.html).

Releasing
---------

Produced artifacts are uploaded on 3 services:
*    **preesm-insa-rennes**: This is a server hosted at INSA Rennes. URL is configured in the project POM files in the [sftp-maven-pluging](https://github.com/preesm/preesm-maven/tree/master/sftp-maven-plugin) configuration. Authentication to this server is configured in the [Maven User Settings](https://maven.apache.org/settings.html) file:
```XML
  <servers>
    <server>
      <id>preesm-insa-rennes</id>
      <username>preesm</username>
      <password>XXX</password>
    </server>
    <!-- ... -->
  </servers>
```
*    **Github Pages** service: This service is used to deploy snapshot releases of Preesm, some update sites for dedicated plugins, and for the website. For the local scripts to work, the developer needs to have the SSH keys properly setup (this is the case for the release of the m2e-settings or the [Preesm workflow task reference](https://preesm.github.io/docs/workflowtasksref/)). When the service is accessed during a Travis CI build, a Github token has to be created and set in the Travis settings (see [here](https://docs.travis-ci.com/user/deployment/pages/)), as it is the case for the Preesm develop snapshot releases.
*    **Github Release** service: This service is used to upload Preesm and Spider products. This service can be accessed [manually](https://help.github.com/en/articles/creating-releases). However some release scripts automatically call the [Github Release API](https://developer.github.com/v3/repos/releases/) to create release and upload assets. In order to work, a Github Token has to be created and given to the release scripts. It is usually done via the file **~/.ghtoken** in the Preesm related projects (see [here](https://github.com/settings/tokens/new) to create a new token).

### Preesm, Graphiti, Spider

The release script **releng/release.sh** is in charge of releasing the artifact generated. In these 3 projects, the script will upload artifacts to both the **preesm-insa-rennes** server (product + update site) and the **Github Release** service (product only). In the case of Preesm, the documentation is updated via the **Github Pages** service.

### dev-feature

The release script **releng/release.sh** is in charge of releasing the artifact generated. Will fail if the server **preesm-insa-rennes** is not configured.

### externaldeps

Run **mvn clean deploy**. Will fail if the server **preesm-insa-rennes** is not configured.

### preesm-maven

The projects in this repository are uploaded to the [Maven Central](https://search.maven.org/) repository using the **mvn clean deploy** command. Deploying to Maven Central requires to have an account on the Sonatype Nexus, and that the jar files have to be signed. Several tutorials explain the steps to achieve this.
*    https://maven.apache.org/repository/guide-central-repository-upload.html
*    https://dzone.com/articles/publish-your-artifacts-to-maven-central
*    https://medium.com/@nmauti/publishing-a-project-on-maven-central-8106393db2c3

### m2e-settings

The **release.sh** script at the root of the repository is reponsible for creating new release and uploading the artifacts to the update site.

### preesm.github.io

Every commit to the master branch is automatically reflected on the website (after 2-3 minutes). This is handled by the Github Pages service.

### preesm-apps, preesm-snapshotn, preesm-cli

Those repositories do not have a release mecanism, nor require one.

Continuous Integration
----------------------

[Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) is a practice that consists in integrating everyhing that is pushed to the source code repository. This include compiling, testing, coding policy checking, etc. This helps uncovering integration issues as soon as possible. For the Preesm project, we rely on the [Travis Ci](https://travis-ci.org/) service to continously integrate changes.

Continuous integration can check for the [quality of the code](https://en.wikipedia.org/wiki/Software_quality) and report failure upon low code quality (see [Quality Gates](https://docs.sonarqube.org/display/SONAR/Quality+Gates)). The current build process does not fail upon such criteria. However, since having reports about quality can give hints and help solving bugs, it is designed to run along with SonarQube.

### Travis CI

_"Travis CI is a hosted continuous integration service used to build and test software projects hosted at GitHub."_ ([Wikipedia](https://en.wikipedia.org/wiki/Travis_CI))

This service is used to trigger automatic build and test on every git push received by Github. The configuration is stored in the **.travis.yml** file at the root of the repository. 

The service is also responsible for notifying developers via email or the Slack chat. In the Preesm project, it also deploys the snapshot artifacts to a dedicated repository.

### SonarQube

[SonarQube](https://www.sonarqube.org/) is a client/server tool that analyses source code and reports bugs, code smells, code coverage, and various other metrics about its quality.

The client scans the code and send relevant data to the server. The server then analyses the code and reports (for instance JaCoCo reports) and produces an online centralized reporting website. The scanner can be called from Maven with goal **sonar:sonar** or the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner).

In order to run, a server needs to be running and the scanner needs to know the URL of that server. When the server and the scanner both run on the same host, [no specific configuration is required](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters#AnalysisParameters-Server). When the scanner runs on a different host, the **sonar.host.url** property needs to be set. When scanning from Maven, it can be set using several ways:

*   in the command line arguments using `-Dsonar.host.url=http://my.url.or.ip:9000/`;
*   in the settings.xml file (see [SonarQube documentation](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Maven#AnalyzingwithSonarQubeScannerforMaven-GlobalSettings));
*   directly in the pom.xml file (not recommended).

SonarQube uses many analysers of its own and can reuse reports generated by other tools. This is the case with [JaCoCo](#initialize) reports for [code coverage](https://en.wikipedia.org/wiki/Code_coverage).

The Travis CI builds use the [SonarCloud](https://sonarcloud.io/organizations/preesm-sonarcloud-org/) service.


Howto
-----

### Check Coding Policy

In the root folder of the project, run

`mvn -Dtycho.mode=maven checkstyle:check`

Alternatively, from a shell, the script `/releng/run_checkstyle.sh` wraps the Maven call.

Within Eclipse, the Dev Meta Feature automatically installs the M2E Checkstyle Plugin, which in turn automatically loads the Checkstyle coding policy from the parent POM file.

### Update Project Version

In the root folder of the project, run

*   `mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z`

**Note:** The variable **tycho.mode** set to **maven** disable Tycho dependency resolver. Resolving P2 dependencies takes a long time is useless for setting the new version, thus we can skip it.

This can be done from Eclipse (see procedure to [call Maven from Eclipse](#running-maven-from-eclipse)):

![Run configuration for updating versions](doc/setNewVersionWithMavenFromEclipse.png "Run configuration for calling 'mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z' from Eclipse.")

Alternatively, from a shell, the script `/releng/update-version.sh X.Y.Z` wraps the Maven call.

### Release

Use the `release.sh` script. See [above](#releasing).

### Add a New Plugin

*   create a new eclipse plugin in the **/plugins** folder;
    *   do not add .project, .settings, .classpath (everything should be configured in the Maven settings)
*   copy POM file from another existing plugin, and update project name;
*   make sure the version in the MANIFEST.MF matches the version in the parent pom;
    *   **-SNAPSHOT** in the POM file translates to **.qualifier** in the MANIFEST
*   insert new module in parent pom `<modules>` section with the name of the folder under **/plugins**;
*   in the **releng/org.ietr.preesm.feature/feature.xml**, add the new plugin as 'included plugin';
*   tell people working with source code to import the new plugin in their Elcipse workspace.

### Add New Dependency

*["One should not reinvent the wheel."](https://en.wikipedia.org/wiki/Reinventing_the_wheel)*

Reusing existing implementations is common and advisable in many situations. Doing it properly requires a little rigor in order to avoid it leading to maintainabilty mess or even legal complication.

#### Licensing Issues

Almost all countries follow some rules about intellectual property. Source code and derived binaries (libraries/executables) fall under these rules. This has led to several legal developments in the past (here is [top 10 for 2015](https://opensource.com/law/16/1/top-10-open-source-legal-developments-2015)). Such development must be avoided:

* In the case of plain source code, do not copy/paste without, at least,  citing the source in a nearby comment;
* Always check the license title (at least) of the third party tool/library you are using, and make sure it is compatible with the Preesm license. The current Preesm license is the [CeCILL-C](http://www.cecill.info/licences/Licence_CeCILL-C_V1-en.html), that is GPL compatible. This [Wikipedia page](https://en.wikipedia.org/wiki/Comparison_of_free_and_open-source_software_licenses) provides a list of licenses and their compatibility with GPL, FSF, Debian, ...
* When the license is not compatible with CeCILL-C:
   * Find a compatible alternative;
   * Create a new, independent, plug-in: As stated in the [GPL license](https://www.gnu.org/licenses/old-licenses/gpl-2.0-faq.en.html#TOCGPLAndPlugins), *"A main program that is separate from its plug-ins makes no requirements for the plug-ins."* Since Preesm can run without a new independent plugin, it makes the plugin free of the CeCILL-C contamination. Thus a license that respects the third party library can be used;

Note: Pieces of software can be multi-licensed, the running license dependending on some arbitrary criteria (research/education, country, price, name, ...). Especially, some tools are provided under academic/research/science/education/... license. It usually means that you can **produce scientific results** using that license, but you can **not publish any tools** directly linked to it. Naturally, the terms of a third party license are unique and one should adapt to it.


#### Packaging

Idealy, the third party dependencies should be stored independently from the source code. The Preesm tool uses an [independent P2 repository](https://github.com/preesm/externaldeps) to store its dependencies. New dependencies should be integrated to this repository (follow [this doc](https://github.com/preesm/externaldeps#details)).

##### Prototyping Way

Since using the P2 repository is a little too time consuming and complex, prototypes should use an other, faster and simpler, way to add third party dependencies:

* Copy the Jar in the plug-in that needs it, under `/lib/`;
* Open the `META-INF/MANIFEST.MF` file, under "Runtime" tab, in the Classpath section (bottom right), click on "Add ..." and select the Jar file;
* Open the `build.properties` file under the same plugin, and in the "Binary Build" column, make sure the Jar file is selected;

This should make the classes under the Jar available in your plug-in.

Please also note that binaries should not be part of the source repository, and some mesures are taken to enforce so. For instance, the git configuration [ignores all Jar files](https://github.com/preesm/preesm/blob/master/.gitignore#L18). Adding a Jar in the Git repository requires extra effort ([link1](https://stackoverflow.com/questions/8006393/force-add-despite-the-gitignore-file) [link2](https://stackoverflow.com/questions/32401387/git-add-adding-ignored-files)).

##### Things to avoid

* Do not use global/system path variables for dependencies: there is no garanty other people use the same configuration.

### Update to a New Eclipse Version

Approximately every 3 months, the Eclipse foundation releases a new Eclipse version. This implies new P2 repositories (update sites) for this new version. To follow the new Eclipse release, Preesm projects have to be updated accordingly:

*   Update the `<repositories>` section in the parent POM file (the two first P2 repositories reference latest Eclipse and Eclipse updates repositories);
*   Update versions in the feature, site, product;
*   Update the feature and dev feature discovery sites with new repository URLs and names;
*   Build locally and tryout the product, the update site using the Java package of the latest Eclipse version, the dev meta feature + source code;
*   Fix errors, if any;
*   Keep consistent Eclipse versions with Graphiti, Preesm and dev features.
