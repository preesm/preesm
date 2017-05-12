Preesm Build Process
====================

This document explains the build process of Preesm and its components (Graphiti, DFTools and their dependencies), and all the configuration of the release and deploy procedures.

Old documentation is available in the [HowToRelease.md](HowToRelease.md) file.

**TODO: sonar + jenkins @ copernic ?**

- [Introduction](#introduction)
	- [Documentation](#documentation)
	- [Git](#git)
	- [Maven](#maven)
	- [Eclipse IDE](#eclipse-ide)
	- [Coding Style](#coding-style)
	- [Dependency Management](#dependency-management)
- [Project structure](#project-structure)
	- [Preesm website (Sourceforge)](#preesm-website-sourceforge)
	- [Github](#github)
	- [Generated content](#generated-content)
	- [Releng Files](#releng-files)
- [Build Process in Maven](#build-process-in-maven)
- [Eclipse setup](#eclipse-setup)
	- [Running Maven from Eclipse](#running-maven-from-eclipse)
- [Releasing (deploy)](#releasing-deploy)
	- [Update online update site](#update-online-update-site)
- [Continuous integration](#continuous-integration)
	- [Jenkins](#jenkins)
	- [Sonar](#sonar)
- [Howto ?](#howto-)
	- [Update project version](#update-project-version)

## Introduction

Graphiti, DFTools and Preesm are sets of Eclipse plugins. Their source code is hosted on GitHub (see the [Preesm team](https://github.com/preesm) page). These projects are built using [Maven](https://maven.apache.org/) and the [Tycho](https://eclipse.org/tycho/) plugins, and mostly developed using the [Eclipse IDE](https://eclipse.org/). Facilities are provided to ease the interactions between these tools. The [Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) server [Jenkins](https://jenkins.io/) is in charge of monitoring the git repositories and triggering builds upon source code modifications.

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

ExternalDeps, Graphiti, DFTools and Preesm resources are located in 2 places:
* Preesm website on Sourceforge
* Github repositories

### Preesm website (Sourceforge)

Preesm main documentation is found on the Preesm website:
* [Tutorials](http://preesm.sourceforge.net/website/index.php?id=tutorials);
* [Developers doc](http://preesm.sourceforge.net/website/index.php?id=developer).

Sourceforge also hosts the generated Javadoc API, the product releases and the update site (see links below).

### Github

All the source code for the projects is hosted on Github (under [Preesm team](https://github.com/preesm)):
 * ExternalDeps: https://github.com/preesm/externaldeps
 * Graphiti: https://github.com/preesm/graphiti
 * DFTools: https://github.com/preesm/dftools
 * Preesm: https://github.com/preesm/preesm
 * Preesm-apps (Preesm small use cases and tutorials): https://github.com/preesm/preesm-apps

Documentation about the release process (this file) is in the Preesm git repository, under **releng/README.md**: https://github.com/preesm/preesm/blob/develop/releng/README.md

Dedicated Maven plugins used during project builds are also hosted there:
* Sftp Maven plugin: https://github.com/preesm/sftp-maven-plugin
* GenFeature Maven plugin: https://github.com/preesm/genfeature-maven-plugin

Their use is detailled later in this document and in the [ExternalDeps Readme](https://github.com/preesm/externaldeps).

#### Source code structure

* src
* test plugins : http://www.modumind.com/2007/06/20/unit-testing-plug-ins-with-fragments/
* /releng/
* ignored files (.gitignore)

#### The .mailmap File

TODO

### Generated content

During the Maven deploy phase, the content is automatically uploaded to those locations:
* Javadoc API : http://preesm.sourceforge.net/gensite/API/
* Update-site : http://preesm.sourceforge.net/eclipse/update-site/
* Product releases : https://sourceforge.net/projects/preesm/files/Releases/

### Releng Files

| file under /releng/ | what it is |
|-----|----|
| hooks/ | Git hooks and necessary dependencies |
| org.ietr.preesm.complete.site/ | The Maven module responsible for generating the update site and aggregating Javadoc and products |
| org.ietr.preesm.dev.feature/ | The feature referencing all Eclipse plugin required to setup a develop environment for Preesm |
| org.ietr.preesm.feature/ | The Preesm feature for end users |
| org.ietr.preesm.product/ | Maven module for generating the end user products |
| org.ietr.preesm.rcp.utils/ | Small Eclipse plugin for configuring the products |
| auto_convert_encoding_and_lineendings.sh | Bash script for converting all file line endings to Linux and charset to UTF-8 |
| copyright_template.txt | Copyright template to include in file headers |
| fix_header_copyright_and_authors.sh | Bash script that replaces copyright template tokens (i.e. %%DATE%%) with data fetched from the git log |
| HowToRelease.md | Old release procedure |
| pom.xml | The main releng POM. Adds two P2 repositories for product and dev feature build. |
| README.md | This file |
| update-version.sh | Small Bash script that calls Maven with proper arguments to set a new version for all submodules. |
| VAADER_checkstyle.xml | Preesm Checkstyle cofniguration file |
| VAADER_eclipse_preferences.epf | Preesm Eclipse preferences file |

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

### Running Maven from Eclipse

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

### Update project version

In the root folder of the project, run `mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z
`. This can be done from Eclipse (see procedure to [call Maven from Eclipse](#running-maven-from-eclipse)):

![alt text](doc/setNewVersionWithMavenFromEclipse.png "Run configuration for calling 'mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z' from Eclipse.")

Alternatively, from a shell, the script `/releng/update-version.sh X.Y.Z` wraps the maven call.

* add a new third party library dependency
* add a dependency to another update site
* change checkstyle policy
* change target update site location
* update to a new Eclipse version (change repos, ...)
