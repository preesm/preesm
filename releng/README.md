Preesm Build Process
====================

This document explains the build process of Preesm and its components (Graphiti, DFTools and their dependencies), and all the configuration of the release and deploy procedures.

Old documentation is available in the [HowToRelease.md](HowToRelease.md) file.

TODO: setup sonar, configure jenkins

- [Introduction](#introduction)
	- [Documentation](#documentation)
	- [Git](#git)
	- [Maven](#maven)
	- [Eclipse IDE](#eclipse-ide)
	- [Coding Style](#coding-style)
	- [Dependency Management](#dependency-management)
- [Project structure](#project-structure)
	- [Preesm website](#preesm-website)
		- [Documentation](#documentation)
		- [Generated content](#generated-content)
	- [Github](#github)
- [Build Process in Maven](#build-process-in-maven)
- [Eclipse setup](#eclipse-setup)
- [Releasing (deploy)](#releasing-deploy)
	- [Update online update site](#update-online-update-site)
- [Continuous integration](#continuous-integration)
	- [Jenkins](#jenkins)
	- [Sonar](#sonar)
- [Howto ?](#howto-)

## Introduction

Graphiti, DFTools and Preesm are sets of Eclipse plugins. Their source code is hosted on GitHub (see the [Preesm team](https://github.com/preesm) page). These projects are built using [Maven](https://maven.apache.org/) and the [Tycho](https://eclipse.org/tycho/) plugins, and mostly developed using the [Eclipse IDE](https://eclipse.org/). Facilities are provided to ease the interactions between these tools. The [Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) server [Jenkins](https://jenkins.io/) is in charge of monitoring the git repositories and triggering builds upon source code modifications.

**TODO: sonar ?**

### Documentation

The documentation is available in 3 places:
* Online documentation about [how to use Preesm](http://preesm.sourceforge.net/website/index.php?id=tutorials) or [how to develop around Preesm](http://preesm.sourceforge.net/website/index.php?id=developer).
* The generated Javadoc is automatically deployed online at this address http://preesm.sourceforge.net/gensite/API/.
* Readme files at the root of the git repositories simply recall general and basic information, except for the [ExternalDeps](https://github.com/preesm/externaldeps) project. For this project, the readme file explains details of the build process. Part of it is recalled in the present document.

### Git

All the source code is hosted on GitHub repositories (see [Preesm team](https://github.com/preesm)). The documentation about how to use git is already available on the Preesm website:
* [Install and Configure git](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git) (also explains the development workflow)
* [git tips & tricks](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git)

### Maven

In order to specifically build Eclipse plugins using Maven, the build process heavily relies on the [Tycho](https://eclipse.org/tycho/) Maven plugins. The [POM](https://maven.apache.org/pom.html#What_is_the_POM) is configured to **automatically**:

* Fetch dependencies from online repositories;
* Check coding policy using [Checkstyle](http://checkstyle.sourceforge.net/);
* Generate Java from [Xtend](https://eclipse.org/xtend/documentation/) files;
* Run [tests within an OSGi runtime](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/plugin-info.html) and compute [code coverage](http://www.eclemma.org/jacoco/);

#### Release Engineering
Using the **releng** [Maven profile](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) (disabled by default), additional actions are available:

* Generate the Javadoc for all plugins and aggregate it;
* Generate [Eclipse source bundles](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.pde.doc.user%2Ftasks%2Fpde_individual_source.htm) for all the plugins;
* Generate a P2 Repository ([Eclipse update-site](https://wiki.eclipse.org/Eclipse_Project_Update_Sites));
* Generate [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F) for Win32/64, Linux 32/64, OSX 64;
* Generate an [Eclipse feature](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm) referencing all necessary development tools for a fast and easy setup of the IDE;
* Deploy the generated Javadoc, update-site and products online.


### Eclipse IDE

It is best to develop Preesm using the Eclipse IDE. To avoid switching from the Maven build process and the Eclipse IDE, it is possible to load Maven projects in the Eclipse IDE using the [M2Eclipse](http://www.eclipse.org/m2e/) Eclipse plugins. This set of plugins allows to effortlessly imports the Maven projects in the IDE, and to run Maven commands from the IDE.

Of course, it is not needed to run Maven all the time since Eclipse has everything required to build Eclipse plugins.

### Coding Style

Having coding standards is a must in a project developed by several people from many places around the world. Many discussions promote the use of such conventions (see links right below). It ranges from simple rules, as the charset to use, to very restrictive rules, as whether or not putting the semicolon at the end of a JavaScript line. For the Preesm project, we decided to use a quite loose one.

We arbitrarily decided to fork from the [Google Java Style](https://google.github.io/styleguide/javaguide.html), and focus on the code shape, that is using spaces for indentation, where to put the bracket, what to do with empty lines, etc.; and not on naming or best practices. This allows to keep a consistent version control history, while minimizing the restriction over developer choice of implementation.

The coding style is automatically checked during the build process using the [Maven Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and within the Eclipse IDE using the [Eclipse Checkstyle plugin](http://eclipse-cs.sourceforge.net/). On top of that, we provide an [Eclipse Preferences File](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that configures the [Eclipse Java Code Style](https://help.eclipse.org/neon/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-code-style.htm) for that coding style. Using such automatic formatting tool (triggered on save), developers can focus on the implementation, not the style.

Reading:
* [Why I Have Given Up on Coding Standards](http://www.richardrodger.com/2012/11/03/why-i-have-given-up-on-coding-standards/) @www.richardrodger.com
* [Why Coding Conventions?](https://msdn.microsoft.com/en-us/library/aa733744.aspx) @msdn.microsoft.com
* [Why Coding Style Matters](https://www.smashingmagazine.com/2012/10/why-coding-style-matters/) @www.smashingmagazine.com
* [Why You Need Coding Standards](https://www.sitepoint.com/coding-standards/) @www.sitepoint.com
* [Why Coding Standards Matter](http://paul-m-jones.com/archives/3) @paul-m-jones.com

### Dependency Management

Graphiti, DFTools and Preesm projects depend on other third party projects and libraries. Since they are sets of Eclipse plugins, their dependencies should be Elcipse plugins. Although it is possible to include plain Java archives within an Eclipse plugin source tree (see [this thread](http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin) for instance), we decided to avoid doing that and choosed to take advantage of the [plugin discovery mechanism](https://wiki.eclipse.org/Equinox/p2/Discovery) integrated within Eclipse and the Maven Tycho plugin.

This dependency mechanism is very similar to the [Maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html). The main difference is the bundles and repositories type: Maven resolves plain Java jars from Maven repositories (i.e. [Nexus](http://www.sonatype.org/nexus/) or [Artifactory](https://www.jfrog.com/artifactory/)),  whereas Tycho and Eclipse resolve Eclipse plugin jars (OSGi bundles) from P2 repositories (Eclipse update-sites).

In order to use a plain Java jar using this mechanism, it first needs to be converted into an Eclipse plugin. As explained in this [article](www.vogella.com/tutorials/EclipseJarToPlugin/article.html), it can be done manually within the Eclipse IDE or automatically using the [p2-maven-plugin](https://github.com/reficio/p2-maven-plugin) or [Gradle](https://gradle.org/).

For Graphiti, DFTools and Preesm, all the plain Java jars have been externalized in the [ExternalDeps](https://github.com/preesm/externaldeps) project. This project consists of a Maven POM file that is configured to fetch all the required jars from online Maven repositories and convert them into Eclipse plugin, before deploying them on the Preesm update site.

## Project structure

### Preesm website
 (sf, gensite, products, update sites, github readme, ...)

#### Documentation

#### Generated content
update site, javadoc API, products
* Per Project
* latest link
* complete site

### Github
* external dependencies
* Graphiti
* DFTools
* Preesm

## Build Process in Maven

Defines how the project is built
* use pom.xml files
* profiles
* repositories


* Maven build big picture
* Checkstyle
* tests (structure, how to run, maven + eclipse)
* configuration details

## Eclipse setup
* Link between maven and Eclipse: load as maven project (m2e)
* Ignore some maven plugins in Eclipse
* Eclipse setup + preferences
* Checkstyle config (link to preesm website)
* Graphiti & DFTools dev (link to preesm website)
* warning source features

## Releasing (deploy)
* versionning? (how/when update version)
* deploy process
* How to deploy (windows, mac, linux)
* Maven plugin repo (sftp plugin + genfeature plugin)
* products (preesm + CDT + equinox + required)
* releng scripts: auto fill header, convert charset and line endings, git hooks
* doc
* javadoc
* feature
* dev features (import vs include)

### Update online update site

To update the online update site, the `tycho-p2-extras-plugin` is used, with the
goal `mirror`


* fucking skip the fucking cache during the fucking download of existing fucking content.xml :@@@@



## Continuous integration

Continuous integrations

### Jenkins



### Sonar

- code coverage

## Howto ?

* add a new third party library dependency
* add a dependency to another update site
* change checkstyle policy
* change target update site location
* update to a new Eclipse version (change repos, ...)
