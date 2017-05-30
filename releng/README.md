Preesm Maintainer Documentation
===============================

This document explains the build process of Preesm and its components (Graphiti, DFTools and their dependencies), and all the configuration of the release and deploy procedures.

Old documentation is available in the [HowToRelease.md](HowToRelease.md) file.

TODO:

*   Sonar+Jenkins
*   Howtos
*   Check dead links

Table of Content
----------------
<!-- Generated with Atom markdown-toc plugin -->
<!-- TOC depthFrom:2 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Table of Content](#table-of-content)
- [Introduction](#introduction)
	- [Documentation](#documentation)
	- [Git](#git)
	- [Maven](#maven)
	- [Eclipse IDE](#eclipse-ide)
	- [Coding Style](#coding-style)
	- [Dependency Management](#dependency-management)
- [Project Structure](#project-structure)
	- [Git Repositories (GitHub)](#git-repositories-github)
	- [Preesm Website (SourceForge)](#preesm-website-sourceforge)
	- [Generated Content](#generated-content)
	- [Releng Files](#releng-files)
- [Build Process in Maven](#build-process-in-maven)
	- [Overview](#overview)
	- [Dependencies](#dependencies)
	- [Profiles](#profiles)
	- [Phase Binding and Configuration Details](#phase-binding-and-configuration-details)
	- [The tycho.mode setting](#the-tychomode-setting)
- [Eclipse Setup](#eclipse-setup)
	- [M2Eclipse](#m2eclipse)
	- [Installing Dependencies](#installing-dependencies)
	- [Eclipse Preferences](#eclipse-preferences)
	- [Running Maven from Eclipse](#running-maven-from-eclipse)
	- [Missing Source Features](#missing-source-features)
- [Release Engineering](#release-engineering)
	- [Overview](#overview)
	- [Versioning](#versioning)
	- [Javadoc](#javadoc)
	- [Feature](#feature)
	- [Dev Meta Feature](#dev-meta-feature)
	- [Complete Site](#complete-site)
	- [Update Online Update Site](#update-online-update-site)
	- [Product](#product)
	- [Deploy Phase](#deploy-phase)
- [Continuous integration](#continuous-integration)
	- [SonarQube](#sonarqube)
	- [Jenkins](#jenkins)
- [Howto](#howto)
	- [Check Coding Policy](#check-coding-policy1)
	- [Update Project Version](#update-project-version)
	- [Deploy](#deploy)
	- [Add New Dependency](#add-new-dependency)
	- [Add New Repository](#add-new-repository)
	- [Change Checkstyle Coding Style](#change-checkstyle-coding-style)
	- [Move Online Resources](#move-online-resources)
	- [Update to a New Eclipse Version](#update-to-a-new-eclipse-version)

<!-- /TOC -->

Introduction
------------

Graphiti, DFTools and Preesm are sets of Eclipse plugins. Their source code is hosted on GitHub (see the [Preesm team](https://github.com/preesm) page). These projects are built using [Maven](https://maven.apache.org/) and the [Tycho](https://eclipse.org/tycho/) plugins, and mostly developed using the [Eclipse IDE](https://eclipse.org/). Facilities are provided to ease the interactions between these tools. The [Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) server [Jenkins](https://jenkins.io/) is in charge of monitoring the git repositories and triggering builds periodically and upon source code modifications.

### Documentation

The documentation is available in 3 places:

*   Online documentation about [how to use Preesm](http://preesm.sourceforge.net/website/index.php?id=tutorials) or [how to develop around Preesm](http://preesm.sourceforge.net/website/index.php?id=developer).
*   The generated Javadoc is automatically deployed [online](http://preesm.sourceforge.net/gensite/API/).
*   Readme files at the root of the git repositories simply recall general and basic information, except for the [ExternalDeps](https://github.com/preesm/externaldeps) project. For this last project, the readme file explains details of the build process. Part of it is recalled in the present document.

### Git

All the source code is hosted on GitHub repositories (see [Preesm team](https://github.com/preesm)). The documentation about how to use git is already available on the Preesm website:

*   [Install and Configure git](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git) (also explains the development workflow)
*   [git tips & tricks](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git)

### Maven

The Preesm project builds, tests, releases and distributions are managed by [Maven](https://maven.apache.org/what-is-maven.html).

*"Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information."* (see [Maven home page](https://maven.apache.org/)).

*   [Philosophy of Maven](https://maven.apache.org/background/philosophy-of-maven.html)
*   [What is Maven ?](https://maven.apache.org/what-is-maven.html)
*   [Maven in 5 minutes](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

Please make sure to read about the [Maven Plugin Configuration](https://maven.apache.org/guides/mini/guide-configuring-plugins.html) before reading further.

In order to specifically build Eclipse plugins using Maven, the build process heavily relies on the [Tycho](https://eclipse.org/tycho/) Maven plugins. The [POM](https://maven.apache.org/pom.html#What_is_the_POM) is configured to **automatically**:

*   Fetch dependencies from online repositories;
*   Check coding policy using [Checkstyle](http://checkstyle.sourceforge.net/);
*   Generate Java from [Xtend](https://eclipse.org/xtend/documentation/) files;
*   Run [tests within an OSGi runtime](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/plugin-info.html) and compute [code coverage](http://www.eclemma.org/jacoco/);

A full build (including product and update site) is triggered with the following command:

-   `mvn -P releng clean verify`

#### Release Engineering
Using the **releng** [Maven profile](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) (disabled by default), additional actions are available:

*   Generate the Javadoc for all plugins and aggregate it;
*   Generate [Eclipse source bundles](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fpde_individual_source.htm) for all the plugins;
*   Generate a P2 Repository ([Eclipse update-site](https://wiki.eclipse.org/Eclipse_Project_Update_Sites));
*   Generate [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F) for Win32/64, Linux 32/64, OSX 64;
*   Generate an [Eclipse feature](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm) referencing all necessary development tools for a fast and easy setup of the IDE;
*   Deploy the generated Javadoc, update-site and products online.

### Eclipse IDE

It is best to develop Preesm using the Eclipse IDE. To avoid switching from the Maven build process and the Eclipse IDE, it is possible to load Maven projects in the Eclipse IDE using the [M2Eclipse](http://www.eclipse.org/m2e/) Eclipse plugins. This set of plugins allows to effortlessly imports the Maven projects in the IDE, and to run Maven commands from the IDE.

Of course, it is not needed to run Maven all the time since Eclipse has everything required to build Eclipse plugins (see [PDE tools](http://www.eclipse.org/pde/)). However the final bundles should be built with Maven (see [Running Maven from Eclipse](#running-maven-from-eclipse)).

### Coding Style

Having coding standards is a must in a project developed by several people from many places around the world. Many discussions promote the use of such conventions (see links below). It ranges from simple rules, as the charset to use, to very restrictive rules, as whether or not putting the semicolon at the end of a JavaScript line. For the Preesm project, we decided to use a quite loose one.

We arbitrarily decided to fork from the [Google Java Style](https://google.github.io/styleguide/javaguide.html), and focus on the code shape, that is using spaces for indentation, where to put the bracket, what to do with empty lines, etc.; and not on naming or best practices. This allows to keep a consistent version control history, while minimizing the restriction over developer choice of implementation.

The coding style is automatically checked during the build process using the [Maven Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and within the Eclipse IDE using the [Eclipse Checkstyle plugin](http://eclipse-cs.sourceforge.net/). On top of that, we provide an [Eclipse Preferences File](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that configures the [Eclipse Java Code Style](https://help.eclipse.org/neon/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-code-style.htm) for that coding style. Using such automatic formatting tool (triggered on save), developers can focus on the implementation, not the style (see [developers doc](http://preesm.sourceforge.net/website/index.php?id=developer)).

Reading:

*   [Why I Have Given Up on Coding Standards](http://www.richardrodger.com/2012/11/03/why-i-have-given-up-on-coding-standards/) @www.richardrodger.com
*   [Why Coding Conventions?](https://msdn.microsoft.com/en-us/library/aa733744.aspx) @msdn.microsoft.com
*   [Why Coding Style Matters](https://www.smashingmagazine.com/2012/10/why-coding-style-matters/) @www.smashingmagazine.com
*   [Why You Need Coding Standards](https://www.sitepoint.com/coding-standards/) @www.sitepoint.com
*   [Why Coding Standards Matter](http://paul-m-jones.com/archives/3) @paul-m-jones.com

### Dependency Management

Graphiti, DFTools and Preesm projects depend on other third party projects and libraries. Since they are sets of Eclipse plugins, their dependencies should be Eclipse plugins. Although it is possible to include plain Java archives within an Eclipse plugin source tree (see [this thread](http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin) for instance), we decided to avoid doing that and chose to take advantage of the [plugin discovery mechanism](https://wiki.eclipse.org/Equinox/p2/Discovery) integrated within Eclipse and the Maven Tycho plugin.

This dependency mechanism is very similar to the [Maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html). The main difference is the bundles and repositories type: Maven resolves plain Java jars from Maven repositories (i.e. [Nexus](http://www.sonatype.org/nexus/) or [Artifactory](https://www.jfrog.com/artifactory/)),  whereas Tycho and Eclipse resolve Eclipse plugin jars (OSGi bundles) from P2 repositories (Eclipse update-sites).

In order to use a plain Java jar using this mechanism, it first needs to be converted into an Eclipse plugin. As explained in this [article](www.vogella.com/tutorials/EclipseJarToPlugin/article.html), it can be done manually within the Eclipse IDE or automatically using the [p2-maven-plugin](https://github.com/reficio/p2-maven-plugin) or [Gradle](https://gradle.org/).

For Graphiti, DFTools and Preesm, all the plain Java jars have been externalized in the [ExternalDeps](https://github.com/preesm/externaldeps) project. This project consists of a Maven POM file that is configured to fetch all the required jars from online Maven repositories and convert them into Eclipse plugin, before deploying them on the Preesm update site.

Project Structure
-----------------

ExternalDeps, Graphiti, DFTools and Preesm resources are located in 2 places:

*   GitHub repositories
*   Preesm website on SourceForge

### Git Repositories (GitHub)

All the source code for the projects is hosted on GitHub (under [Preesm team](https://github.com/preesm)):

*   ExternalDeps: [https://github.com/preesm/externaldeps](https://github.com/preesm/externaldeps)
*   Graphiti: [https://github.com/preesm/graphiti](https://github.com/preesm/graphiti)
*   DFTools: [https://github.com/preesm/dftools](https://github.com/preesm/dftools)
*   Preesm: [https://github.com/preesm/preesm](https://github.com/preesm/preesm)
*   Preesm-apps (Preesm small use cases and tutorials): [https://github.com/preesm/preesm-apps](https://github.com/preesm/preesm-apps)

Documentation about the release process (this file) is in the Preesm git repository, under [**releng/README.md**](https://github.com/preesm/preesm/blob/develop/releng/README.md)

Dedicated Maven plugins used during project builds are also hosted there:

*   [Sftp Maven plugin](https://github.com/preesm/sftp-maven-plugin)
*   [GenFeature Maven plugin](https://github.com/preesm/genfeature-maven-plugin)

Their use is detailed later in this document and in the [ExternalDeps Readme](https://github.com/preesm/externaldeps).

#### Source Code Structure

The Git repositories are organized as follows:

*   **/plugins**: the source code of the projects (Graphiti, DFTools, Preesm);
*   **/releng**: the release engineering files (see below);
*   **/test-fragments**: the [test plug-in fragments](http://www.modumind.com/2007/06/20/unit-testing-plug-ins-with-fragments/) for functional and unit testing;
*   **/tests**: the integration and end-to-end tests

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


### Preesm Website (SourceForge)

Preesm main documentation is found on the Preesm website:

*   [Tutorials](http://preesm.sourceforge.net/website/index.php?id=tutorials);
*   [Developers doc](http://preesm.sourceforge.net/website/index.php?id=developer).

SourceForge also hosts the generated Javadoc API, the product releases and the update site (see links below). For uploads, see SourceForge Documentation:

*   [Release Files for Download](https://sourceforge.net/p/forge/documentation/Release%20Files%20for%20Download/): how to release files on SourceForge File Release Service;
*   [Shell Service](https://sourceforge.net/p/forge/documentation/Shell%20Service/): how to instantiate interactive shell on SourceForge servers;

### Generated Content

During the Maven deploy phase, the content is automatically uploaded to those locations:

*   [Javadoc API](http://preesm.sourceforge.net/gensite/API/)
    *   accessed on SourceForge servers under `/home/project-web/preesm/htdocs/gensite/API`
*   [Update-site](http://preesm.sourceforge.net/eclipse/update-site/)
    *   accessed on SourceForge servers under `/home/project-web/preesm/htdocs/gensite/update-site/complete` (`eclipse/update-site` is a link to `gensite/update-site/complete`)
*   [Product releases](https://sourceforge.net/projects/preesm/files/Releases/)
    *   accessed on SourceForge servers using the projects web interface or under `/home/frs/project/preesm/Releases`

### Releng Files

| File under /releng/ | Description |
|-----------------------------|--------------------------------------------|
| hooks/ | Git hooks and necessary dependencies |
| org.ietr.preesm.complete.site/ | The Maven module responsible for generating the update site and aggregating Javadoc and products |
| org.ietr.preesm.dev.feature/ | The feature referencing all Eclipse plugin required to setup a develop environment for Preesm |
| org.ietr.preesm.feature/ | The Preesm feature for end users |
| org.ietr.preesm.product/ | Maven module for generating the end user products |
| org.ietr.preesm.rcp.utils/ | Small Eclipse plugin for configuring the products |
| auto_convert_encoding_and_lineendings.sh | Bash script for converting all file line endings to Linux and charset to UTF-8 |
| run_checkstyle.sh | Small Bash script that calls Maven with proper arguments to check the coding policy |
| copyright_template.txt | Copyright template to include in file headers |
| fix_header_copyright_and_authors.sh | Bash script that replaces copyright template tokens (i.e. %%DATE%%) with data fetched from the git log |
| HowToRelease.md | Old release procedure |
| pom.xml | The main releng POM. Adds two P2 repositories for product and dev feature build. |
| README.md | This file |
| update-version.sh | Small Bash script that calls Maven with proper arguments to set a new version for all submodules. |
| VAADER_checkstyle.xml | Preesm Checkstyle configuration file (developed by VAADER team) |
| VAADER_eclipse_preferences.epf | Preesm Eclipse preferences file (developed by VAADER team) |

Build Process in Maven
----------------------

This section details how the Preesm project is built using Maven. Graphiti and DFTools are built using a similar process to the Preesm one. For the site and products generation and deploy phases, please read the [Release Engineering](#release-engineering) section.

### Overview

The Maven build process is defined in [POM files](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html). The POM files can define build properties, reference external repositories, declare sub modules, call and configure Maven plugins, define profiles, etc.

Each Eclipse plugin in the Preesm project has its own POM file. On top of that, there is a [parent POM](http://www.javavillage.in/maven-parent-pom.php), that defines project wide properties, Maven plugin configuration, [Maven profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html), and the list of [submodules](https://maven.apache.org/guides/mini/guide-multiple-modules.html). The projects also have few intermediate POM files (`releng/pom.xml`, `test-fragments/pom.xml`, ...). They add some configuration for dedicated parts of the test or release process.

The build of the project is triggered using the following command: `mvn clean verify`. This calls two Maven goals from two different [Maven Build Lifecycles](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Build_Lifecycle_Basics):

*   The **clean** goal from the *clean* lifecycle: this goal cleans the current project and all its enabled submodules. This ensures no generated file is kept between builds.
*   The **verify** goals from the *default* lifecycle: this goal validates project configuration, generates sources, checks their compliance with regard to the coding policy, compiles them, packages them and finally fires the tests.

The dependencies are all defined in MANIFEST.MF files within the Eclipse plugins and test fragments. The Tycho Maven plugin is responsible for resolving these Eclipse dependencies and fetch them from online remote P2 repositories (see below). All the Maven plugins used during the build process are available on the Maven Central repository (enabled by default, see [Super POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Super_POM)). There should be no plain Java jar dependencies (see [Dependency Management](#dependency-management)).

### Dependencies

Preesm is a set a Eclipse plugins, thus its dependencies are OSGi dependencies. They are defined in the MANIFEST.MF file of every Eclipse plugin. The Tycho Maven plug-in is then responsible for resolving such dependencies during the Maven build process. There should be no `<dependencies>` section in the POM files.

Third party plugins dependencies are resolved using external P2 repositories, such as the [Eclipse project update site](http://ftp.fau.de/eclipse/releases/) for the core components of Eclipse, [TMF releases update site](http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/) for the Xtend runtime libraries, or the [Preesm update site](http://preesm.sourceforge.net/eclipse/update-site/) for specific third party (see [ExternalDeps](https://github.com/preesm/externaldeps) project), Graphiti and DFTools dependencies. These repositories are defined in the parent POM file (at the root of the git repository):

```xml
<properties>
  <!-- ... -->
  <complete.p2.repo>http://preesm.sourceforge.net/gensite/update-site/complete/</complete.p2.repo>
  <eclipse.mirror>http://mirror.ibcp.fr/pub/eclipse/</eclipse.mirror>
</properties>
<!-- ... -->
<repositories>
  <!-- add Neon repository to resolve dependencies -->
  <repository>
    <id>Neon</id>
    <layout>p2</layout>
    <url>${eclipse.mirror}/releases/neon/</url>
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

**Note:** In order to make sure anyone can build your version of the project, it is better to clean the local repository before calling the build command. This will ensure you did not install temporary versions in your local repository that will not be available to other developers.

**Note:** If Maven is called with a goal in the default lifecycle that is before the **package** goal (for instance **compile**), the dependencies will not be resolved since modules will not be packaged. Thus the Tycho plugin will try to resolve dependencies from the [local repository](https://www.mkyong.com/maven/where-is-maven-local-repository/) and then remote ones. This will cause failure is the required version is not available or if there has been changes in the API and the version did not increase. The same failure can occur if one tries to build one of the submodules independently from the others. To circumvent this, one could first **install** (see [goal doc.](http://maven.apache.org/plugins/maven-install-plugin/)) the dependencies to the local repository, then compile only or package a submodule only.

### Profiles

Two [Maven build profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) are defined in the parent POM:

#### releng
A profile for enabling release engineering modules and plugins. See [Release Engineering](#release-engineering).

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

*   [directory-maven-plugin](https://github.com/jdcasey/directory-maven-plugin): initialize the property **main.basedir** with the path to the parent project directory. This property is used in the Checkstyle configuration for having a consistent path to its configuration file from any submodule.
*   [jacoco-maven-plugin](http://www.eclemma.org/jacoco/trunk/doc/prepare-agent-mojo.html): Used for [code coverage](https://en.wikipedia.org/wiki/Code_coverage) computation. See plugin documentation. The configuration outputs the report in the target folder of the parent project. All the submodules append their report in that file. It is later used by [Sonar](#sonarqube). Test and releng modules are ignored during when computing code coverage.
```XML
<destFile>${main.basedir}/target/jacoco.exec</destFile>
<append>true</append>
<excludes>
    <exclude>**/tests/**</exclude>
    <exclude>**/test-fragments/**</exclude>
    <exclude>**/releng/**</exclude>
</excludes>
```

*   [org.eclipse.m2e:lifecycle-mapping](http://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html): Tell the Eclipse Maven Plugin to ignore some Maven plugins. More details in the [Eclipse setup](#eclipse-setup) section. This goal is only active in Eclipse (see [only-eclipse](#only-eclipse) profile).

#### generate-sources

*   [xtend-maven-plugin](https://eclipse.org/Xtext/documentation/350_continuous_integration.html): Compiles Xtend source files from **/xtend-src** to Java files in **/xtend-gen**.

#### process-sources

*   [maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/): Checkstyle Maven plugin for checking code style compliance. Explicitly bound to **process-sources** phase. Force Checkstyle version (default version does not support Java 8 syntax). Add configuration file path.

#### compile

*   [tycho-compiler-plugin](https://eclipse.org/tycho/sitedocs/tycho-compiler-plugin/compile-mojo.html): Compiles the Java sources using Eclipse dependencies.

#### package

*   [tycho-packaging-plugin](https://eclipse.org/tycho/sitedocs/tycho-packaging-plugin/package-plugin-mojo.html): Bundle modules as Eclipse plugins.

#### integration-test

*   [tycho-surefire-plugin](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/test-mojo.html): Fire tests within an Eclipse runtime environment. Defined in the test fragments POM.

#### deploy

*   [maven-deploy-plugin](http://maven.apache.org/plugins/maven-deploy-plugin/): disable the default deploy plugin. This is due to issues when deploying P2 repositories on SourceForge. The actual deploy procedure is detailed in the [Release Engineering](#release-engineering) section.

### The tycho.mode setting

The Tycho Maven plugins active them self by default when calling Maven. More specifically, the P2 dependency resolver activates itself when the [reactor](https://maven.apache.org/guides/mini/guide-multiple-modules.html) computes a build order. This resolver "converts" dependencies between Eclipse plugins into implicit Maven dependencies, and fetch them from P2 repositories (Eclipse update sites). The main issue is the time it takes.

Since some Maven goals do not need dependency resolution (for instance clean, or set-version), it is advised to disable it by setting the property **tycho.mode** to **maven**.


*   [Eclipse Tycho](http://www.vogella.com/tutorials/EclipseTycho/article.html#setting-version-numbers)

Eclipse Setup
-------------

Eclipse is the preferred IDE for developing Preesm. The developer setup is detailed on the [SourceForge website](http://preesm.sourceforge.net/website/index.php?id=building-preesm). This section details the links between the Maven configuration and the Eclipse setup.

**Note:** Eclipse should not be used to package or release.

### M2Eclipse

Most of the job is done by the [M2Eclipse Eclipse plugin](http://www.eclipse.org/m2e/). This plugin allows to import Maven projects in the Eclipse workspace. It also reads the POM files and configure Eclipse projects accordingly.

Some Maven plugins are however not handled by the M2E plugin. This is the case for the **directory-maven-plugin**. This plugin should not affect the Eclipse build process, and is therefore add to the ignore list in M2E (see [initialize phase](#initialize)). Indeed the Checkstyle configuration for Eclipse is detailed [here](http://preesm.sourceforge.net/website/index.php?id=building-preesm) and does not need the **main.basedir** property.

Some other Maven plugins need to be supported by Eclipse, as the Tycho plugin that sets some Eclipse plugin configuration up. Therefore we need to install some M2E extensions (called connector) to support them, using the [M2E Tycho connector](https://github.com/tesla/m2eclipse-tycho) (installed from this [update site](http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/)).

### Installing Dependencies

The third-party dependencies must be installed through update sites. All of them are bundled with the Dev Meta Feature generated during the release process (see below). It is also possible to setup an Eclipse for developing Preesm along with Graphiti and/or DFTools sources (see this [documentation](http://preesm.sourceforge.net/website/index.php?id=working-with-dftoolsgraphiti-source)).

### Eclipse Preferences

Eclipse comes with many development facilities. Among them is the code formatter. We provide an [Eclipse Preference File](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that comes with a formatter configuration that respects the Checkstyle coding policy and that is called upon save.

Various small configurations are also included in this preference file (see [source](VAADER_eclipse_preferences.epf)).

### Running Maven from Eclipse

As aforementioned, the Eclipse IDE is best to develop with Preesm, but should not be used for packaging and releasing. Indeed the [Plugin Development Environment build tools](https://projects.eclipse.org/projects/eclipse.pde) provide all necessary components for the plugin development life. However the configuration differs from the Maven plugins (for instance the Eclipse update site builder uses a file named **site.xml** whereas the [tycho-p2-repository-plugin](http://www.eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-repository-plugin/assemble-repository-mojo.html) reads a file named **category.xml**). The release process is tuned for Maven build so that it can be called from a continuous integration platform.

Thankfully, the M2E Eclipse plugins come with facilities to run Maven goals from the Eclipse IDE, without having to install a local Maven distribution. This is done by running any of the imported Maven project as "Maven build" (see example [here](https://books.sonatype.com/m2eclipse-book/reference/running-sect-running-maven-builds.html)). Goals, profiles and parameters can be set in the Eclipse interface. This should be used if one wants to [update project version](#update-project-version) or [release/deploy](#deploy) Preesm from Eclipse.

### Missing Source Features

As mentioned on the [Preesm website](http://preesm.sourceforge.net/website/index.php?id=working-with-dftoolsgraphiti-source), some warnings can appear when working with Graphiti and DFTools source code. This is nothing to be wary of as these "missing" features are actually automatically generated during the **package** phase by the [tycho-source-feature-plugin](https://eclipse.org/tycho/sitedocs-extras/tycho-source-feature-plugin/source-feature-mojo.html).

Release Engineering
-------------------

This section explains what are the sources and targets of the deploy phase.

### Overview

After the Preesm Eclipse plugins are built, they have to be bundled and deployed in order to be distributed to the end users. The common way to distribute Eclipse plugins is to use [update sites](http://agile.csc.ncsu.edu/SEMaterials/tutorials/install_plugin/index_v35.html). The Tycho Maven plugins provide such functionality and the releng (short term for release engineering) POM file produces one update site. The [tycho-p2-director-plugin](https://eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-director-plugin/plugin-info.html) also enable the construction of [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F). Finally, for development purposes, the Javadoc API is also generated and integrated to the deployed content.

### releng Profile

The **releng** Maven profile can be enabled to activate all release engineering steps:
```bash
mvn -P releng clean deploy
```
This profile adds:

*   Javadoc generation: bound to the **process-sources** phase, it generates the Javadoc site using the [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/).
*   Source plugin generation: During **prepare-packaging** phase, the [tycho-source-plugin](https://eclipse.org/tycho/sitedocs/tycho-source-plugin/plugin-source-mojo.html) generates, along with the binary package, an Eclipse bundle that contains the source code of every plugin. This is used for publishing SDKs.
*
This profile also activates the generation of the Source Feature that include all generated source plugins using the [tycho-source-feature-plugin](https://eclipse.org/tycho/sitedocs-extras/tycho-source-feature-plugin/source-feature-mojo.html).
*   **/releng/** intermediate POM: this submodule contains the plugins for the generation of the features, the site, and the product. This POM also enable the [Preesm Maven repository](http://preesm.sourceforge.net/maven/) for accessing the [sftp-maven-plugin](https://github.com/preesm/sftp-maven-plugin).

### Versioning

**Note:** at the time of writing, there is no [snapshot repository](https://www.tutorialspoint.com/maven/maven_snapshots.htm) for Preesm.

*   How/when update version: [Semantic Versioning](http://semver.org/);
    *   Can also add 4th level of version for hotfixes (example: 2.2.28.hotfix1);
    *   Can also append **qualifier**, to be replaced with current timestamp (example: 2.2.8.qualifier). This is usually used for snaphot releases.
*   See [Update Project Version](#update-project-version). This procedure updates version in all the POM files, in the MANIFEST.MF file of all included submodules, and in the features. Generated site and product names are also impacted.

### Javadoc

The Javadoc is generated only when the releng profile is enabled. It is generated during the phase **process-sources** using the [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/). In order to facilitate browsing the Javadoc of several plugins, the submodule **complete.site** also enables the aggregation of the Javadoc of the plugins during the **package** phase.

### Feature

The elements found in an update site are [Installable Units (IU)](https://wiki.eclipse.org/Installable_Units). Eclipse plugins are IUs. However in order to install all plugins at once, an [Eclipse feature](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm) can be used.

An Eclipse feature is declared as an Eclipse project, with a [feature.xml](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fmisc%2Ffeature_manifest.html) file defining various information, as the list of plugins it contains, the license, contacts, referenced update sites, etc. It can be built using the Tycho Maven plugins by specifying [eclipse-feature](https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-feature) as packaging type. The Preesm update site and product both use this feature during their build process.

The configuration of a feature is done within the **feature.xml** file. This file can be edited using the [Feature Editor](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fpde_feature_build.htm) from Eclipse PDE tools. If you know what you are doing, you can edit it as an XML file.

### Dev Meta Feature

To ease the developer  job, we provide another feature designed for setting up the development environment for Preesm. This feature actually imports other features. This can be seen as adding dependencies in the Eclipse IDE toward other features. This "meta" feature imports the following features:

*   ExternalDeps (third party dependencies);
*   Graphiti and DFTools features and source features;
*   Xtend, GEF, EMF and Graphiti (from eclipse.org) SDKs;
*   Git and Checkstyle integration;
*   M2Eclipse and its tycho connector.

Note that these features have to be **imported** and not **included** in order to avoid install errors with conflicting versions that could be already installed.
The feature requires new repositories to make sure latest releases are installed (even though the Preesm update site is self-contained, it does not contain updates since last release):
```XML
<!-- The following section tells where to lookup missing imported features -->
<url>
  <!-- Eclipse main repos -->
  <discovery label="Neon" url="http://mirror.ibcp.fr/pub/eclipse/releases/neon/"/>
  <discovery label="Neon Updates" url="http://mirror.ibcp.fr/pub/eclipse/eclipse/updates/4.6"/>

  <!-- TMF Repo for latest xtend -->
  <discovery label="TMF Releases" url="http://mirror.ibcp.fr/pub/eclipse/modeling/tmf/xtext/updates/composite/releases/"/>

  <!-- M2E extension -->
  <discovery label="M2E Tycho" url="http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/"/>
  <!-- Eclipse main repos -->
  <discovery label="Checkstyle" url="http://eclipse-cs.sf.net/update/"/>

  <!-- Preesm Repo -->
  <discovery label="Preesm" url="http://preesm.sourceforge.net/gensite/update-site/complete/"/>
</url>
```

These references are used when installing the reference from an Eclipse installation. During the Maven build, these repositories should be added. The Preesm, TMF and Neons repositories are already included in the parent POM (see [Dependencies](#dependencies)). The extra development plugins, however, need to be found during the build process, for bundling the dev feature and generating the complete site (sadly, Tycho does not use this `<discovery>` tags during the build). Therefore, intermediate releng POM declares new P2 repositories (in order to share this configuration with the site project):
```XML
  <!-- Extra repositories for building the all-in-one dev feature -->
  <repositories>
    <repository>
      <id>Tycho M2E extension</id>
      <layout>p2</layout>
      <url>http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/</url>
    </repository>
    <repository>
      <id>Checkstyle</id>
      <layout>p2</layout>
      <url>http://eclipse-cs.sf.net/update</url>
    </repository>
  </repositories>
```

### Complete Site

The complete site project is responsible for:

*   The aggregation of the Javadoc of all the plugins thanks to the `<includeDependencySources>` configuration of the [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/).
*   The generation of the Update site for all Preesm features (Preesm, Source, Dev) with the default Tycho plugin with the type [eclipse-repository](https://wiki.eclipse.org/Tycho/eclipse-repository).
*   Preparing and uploading the generated content using
    *   [Maven Ant targets](http://maven.apache.org/plugins/maven-antrun-plugin/): copy generated sites (P2 repo & Javadoc API) and create symlink;
    *   [download-maven-plugin](https://github.com/maven-download-plugin/maven-download-plugin): fetch online P2 repository metadata
    *   [tycho-p2-extras-plugin:mirror](https://eclipse.org/tycho/sitedocs-extras/tycho-p2-extras-plugin/mirror-mojo.html): merge online P2 metadata with generated P2 repo
    *   [sftp-maven-plugin](https://github.com/preesm/sftp-maven-plugin): upload content

The configuration of the content of the update site is done with the **category.xml** file. It can be edited with the [Site Editor](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fpde_feature_build.htm) from PDE tools. If you know what you are doing, you can edit it as an XML file.

Since this site includes all the necessary dependencies, it needs to be able to lookup the development plugins required by the Dev feature (see `<repositories>` tags in previous section).

### Update Online Update Site

To update the online update site, the `tycho-p2-extras-plugin` is used with the
goal `mirror`  (see [tycho-p2-extras-plugin:mirror](https://eclipse.org/tycho/sitedocs-extras/tycho-p2-extras-plugin/mirror-mojo.html)). This goal allows to merge several P2 repositories into one, and if one P2 repository exists at the target location, to append the content.

To avoid downloading the whole online update site, only the metadata are fetched using simple wget (see [download-maven-plugin](https://github.com/maven-download-plugin/maven-download-plugin)). Then the generated update site is appended to the copy of the online one, before being uploaded using the [sftp-maven-plugin](https://github.com/preesm/sftp-maven-plugin).

The [download-maven-plugin](https://github.com/maven-download-plugin/maven-download-plugin) uses an internal cache for the downloaded files. When releasing several projects successively, this cache should be disabled or the online metadata will not reflect the actual content of the P2 repository. This cached is disabled for all Preesm projects.

### Product

The product project generates [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F) that include the Preesm feature along with the default Eclipse environment and the CDT plugin for C/C++ editor. They are generated using the [tycho-p2-director-plugin](https://eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-director-plugin/plugin-info.html) that can materialize and archive Eclipse products. The
[Maven Ant targets](http://maven.apache.org/plugins/maven-antrun-plugin/) and
[sftp-maven-plugin](https://github.com/preesm/sftp-maven-plugin) are responsible for preparing and uploading the generated content online.

The product is configured with the **org.ietr.preesm.product** file. It can be edited using the [Product Editor](http://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fpde_feature_build.htm) from PDE tools, or as an XML file.

### Deploy Phase

The deploy phase is fully automated within the Maven POM files using the following command (see [Deploy](#deploy) for running this command from Eclipse):

*   `mvn -P releng clean deploy`

The only data that should be given to Maven are the SourceForge credentials for authenticating on the sftp server. These credentials are provided by one of the [Maven settings file](https://maven.apache.org/settings.html#Servers), usually the one located in the user home: `${user.home}/.m2/settings.xml`:
```xml
<!-- ... -->
<servers>
<!-- ... -->
  <server>
    <id>sf-preesm-update-site</id>
    <username>my_sourceforge_login</username>
    <password>my_password</password>
  </server>
  <!-- ... -->
</servers>
<!-- ... -->
```

This will be used during the upload of the product and the complete site.

The site deploy phase is configured in the POM file of the complete site module. It uploads the aggregated Javadoc API, the update site for the current release, and the appended metadata for the full P2 repository.

The product deploy phase is configured in the POM file of the product module. The content is uploaded to the [SourceForge File Release System](https://sourceforge.net/p/forge/documentation/Release%20Files%20for%20Download/) to avoid the 20MB file size limitation. Note that the update site cannot be accessed by Eclipse P2 director from there.

Continuous Integration
----------------------

[Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) is a practice that consists in integrating everyhing that is pushed to the source code repository. This include compiling, testing, coding policy checking, etc. This helps uncovering integration issues as soon as possible. For the Preesm project, we rely on the [Jenkins](https://jenkins.io/) program to continously integrate changes.

Continuous integration can check for the [quality of the code](https://en.wikipedia.org/wiki/Software_quality) and report failure upon low code quality (see [Quality Gates](https://docs.sonarqube.org/display/SONAR/Quality+Gates)). The current build process does not fail upon such criteria. However, since having reports about quality can give hints and help solving bugs, it is designed to run along with SonarQube.

### SonarQube

[SonarQube](https://www.sonarqube.org/) is a client/server tool that analyses source code and reports bugs, code smells, code coverage, and various other metrics about its quality.

The client scans the code and send relevant data to the server. The server then analyses the code and reports (for instance JaCoCo reports) and produces an online centralized reporting website. The scanner can be called from Maven with goal **sonar:sonar** (used by Jenkins), using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner), or the [Jenkins Sonar plugin](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins). 

In order to run, a server needs to be running and the scanner needs to know the URL of that server. When the server and the scanner both run on the same host, [no specific configuration is required](https://docs.sonarqube.org/display/SONAR/Analysis+Parameters#AnalysisParameters-Server). When the scanner runs on a different host, the **sonar.host.url** property needs to be set. When scanning from Maven, it can be set using several ways:

*   in the command line arguments using `-Dsonar.host.url=http://my.url.or.ip:9000/`;
*   in the settings.xml file (see [SonarQube documentation](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Maven#AnalyzingwithSonarQubeScannerforMaven-GlobalSettings));
*   directly in the pom.xml file (not recommended).

SonarQube uses many analysers of its own and can reuse reports generated by other tools. This is the case with [JaCoCo](#initialize) reports for [code coverage](https://en.wikipedia.org/wiki/Code_coverage). Some properties must be set in the parent POM.xml file to tell Sonar where to look for the JaCoCo report:

```XML
<sonar.core.codeCoveragePlugin>jacoco</sonar.core.codeCoveragePlugin>
<sonar.dynamicAnalysis>reuseReports</sonar.dynamicAnalysis>
<sonar.jacoco.reportPaths>../../target/jacoco.exec</sonar.jacoco.reportPaths>
<sonar.exclusions>**/tests/**, **/test-fragments/**, **/releng/**</sonar.exclusions>
```

**Note:** The path to the JaCoCo report is hardcoded. It works at the time of writing since all plugins are at a depth of 2 in the repository. The **${main.basedir}** can not be used here since the properties are assigned before the execution of **directory-maven-plugin**. 

**TODO**: find another way to set this property dynamically.

### Jenkins

TODO



Jenkinsfile

Blue Ocean + required plugins

Notification mail

Howto
-----

### Check Coding Policy

In the root folder of the project, run

`mvn -P releng -Dtycho.mode=maven -Dmain.basedir=. checkstyle:check`

Alternatively, from a shell, the script `/releng/run_checkstyle.sh` wraps the Maven call.

To check the coding policy from Eclipse, the [developer page](http://preesm.sourceforge.net/website/index.php?id=building-preesm) explains how to set up the Checkstyle Eclipse Plugin.

### Update Project Version

In the root folder of the project, run

*   `mvn -Dtycho.mode=maven -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z
`

**Note:** The variable **tycho.mode** set to **maven** disable Tycho dependency resolver. Resolving P2 dependencies takes a long time is useless for setting the new version, thus we can skip it.

This can be done from Eclipse (see procedure to [call Maven from Eclipse](#running-maven-from-eclipse)):

![Run configuration for updating versions](doc/setNewVersionWithMavenFromEclipse.png "Run configuration for calling 'mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z' from Eclipse.")

Alternatively, from a shell, the script `/releng/update-version.sh X.Y.Z` wraps the Maven call.

### Deploy

In the root folder of the project, run

*   `mvn -P releng clean deploy`

This can be run from Eclipse (see previous Howto).

### Add a New Plugin

*   create a new eclipse plugin in the plugin folder
    *   do not add .project, .settings (everything should be configured in the Maven settings)
*   copy POM template, tune it if necessary
*   insert new module in parent pom
*   add the plugin in the feature (the normal one)
*   create test fragment
    *   add module in test-fragment intermediate pom

### Add New Dependency

TODO

### Apply a Fix in Graphiti/DFTools

TODO

### Add New Repository

TODO

### Change Checkstyle Coding Style

TODO

### Move Online Resources

TODO

### Update to a New Eclipse Version

TODO
