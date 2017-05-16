Preesm Build Process
====================

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
	- [Git Repositories (Github)](#git-repositories-github)
	- [Preesm Website (SourceForge)](#preesm-website-sourceforge)
	- [Generated Content](#generated-content)
	- [Releng Files](#releng-files)
- [Build Process in Maven](#build-process-in-maven)
	- [Overview](#overview)
	- [Dependencies](#dependencies)
	- [Profiles](#profiles)
	- [Phase Binding and Configuration Details](#phase-binding-and-configuration-details)
- [Eclipse Setup](#eclipse-setup)
	- [M2Eclipse](#m2eclipse)
	- [Installing Dependencies](#installing-dependencies)
	- [Eclipse Preferences](#eclipse-preferences)
	- [Running Maven from Eclipse](#running-maven-from-eclipse)
	- [Missing Source Features](#missing-source-features)
- [Release Engineering](#release-engineering)
	- [Overview](#overview)
	- [Versionning](#versionning)
	- [Javadoc](#javadoc)
	- [Feature](#feature)
	- [Dev Meta Feature](#dev-meta-feature)
	- [Complete Site](#complete-site)
	- [Update Online Update Site](#update-online-update-site)
	- [Product](#product)
	- [Deploy Phase](#deploy-phase)
- [Continuous integration](#continuous-integration)
- [Howto](#howto)
	- [Update Project Version](#update-project-version)
	- [Deploy from Eclipse](#deploy-from-eclipse)
	- [Add New Dependency](#add-new-dependency)
	- [Add New Repository](#add-new-repository)
	- [Change Checkstyle Coding Style](#change-checkstyle-coding-style)
	- [Move Online Resources](#move-online-resources)
	- [Update to a New Eclipse Version](#update-to-a-new-eclipse-version)

<!-- /TOC -->

Introduction
------------

Graphiti, DFTools and Preesm are sets of Eclipse plugins. Their source code is hosted on GitHub (see the [Preesm team](https://github.com/preesm) page). These projects are built using [Maven](https://maven.apache.org/) and the [Tycho](https://eclipse.org/tycho/) plugins, and mostly developed using the [Eclipse IDE](https://eclipse.org/). Facilities are provided to ease the interactions between these tools. The [Continuous Integration](https://en.wikipedia.org/wiki/Continuous_integration) server [Jenkins](https://jenkins.io/) is in charge of monitoring the git repositories and triggering builds upon source code modifications.

### Documentation

The documentation is available in 3 places:
*   Online documentation about [how to use Preesm](http://preesm.sourceforge.net/website/index.php?id=tutorials) or [how to develop around Preesm](http://preesm.sourceforge.net/website/index.php?id=developer).
*   The generated Javadoc is automatically deployed [online](http://preesm.sourceforge.net/gensite/API/).
*   Readme files at the root of the git repositories simply recall general and basic information, except for the [ExternalDeps](https://github.com/preesm/externaldeps) project. For this project, the readme file explains details of the build process. Part of it is recalled in the present document.

### Git

All the source code is hosted on GitHub repositories (see [Preesm team](https://github.com/preesm)). The documentation about how to use git is already available on the Preesm website:
*   [Install and Configure git](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git) (also explains the development workflow)
*   [git tips & tricks](http://preesm.sourceforge.net/website/index.php?id=install-and-configure-git)

### Maven

In order to specifically build Eclipse plugins using Maven, the build process heavily relies on the [Tycho](https://eclipse.org/tycho/) Maven plugins. The [POM](https://maven.apache.org/pom.html#What_is_the_POM) is configured to **automatically**:

*   Fetch dependencies from online repositories;
*   Check coding policy using [Checkstyle](http://checkstyle.sourceforge.net/);
*   Generate Java from [Xtend](https://eclipse.org/xtend/documentation/) files;
*   Run [tests within an OSGi runtime](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/plugin-info.html) and compute [code coverage](http://www.eclemma.org/jacoco/);

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

Of course, it is not needed to run Maven all the time since Eclipse has everything required to build Eclipse plugins.

### Coding Style

Having coding standards is a must in a project developed by several people from many places around the world. Many discussions promote the use of such conventions (see links right below). It ranges from simple rules, as the charset to use, to very restrictive rules, as whether or not putting the semicolon at the end of a JavaScript line. For the Preesm project, we decided to use a quite loose one.

We arbitrarily decided to fork from the [Google Java Style](https://google.github.io/styleguide/javaguide.html), and focus on the code shape, that is using spaces for indentation, where to put the bracket, what to do with empty lines, etc.; and not on naming or best practices. This allows to keep a consistent version control history, while minimizing the restriction over developer choice of implementation.

The coding style is automatically checked during the build process using the [Maven Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/) and within the Eclipse IDE using the [Eclipse Checkstyle plugin](http://eclipse-cs.sourceforge.net/). On top of that, we provide an [Eclipse Preferences File](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that configures the [Eclipse Java Code Style](https://help.eclipse.org/neon/topic/org.eclipse.jdt.doc.user/reference/preferences/java/ref-preferences-code-style.htm) for that coding style. Using such automatic formatting tool (triggered on save), developers can focus on the implementation, not the style.

Reading:
*   [Why I Have Given Up on Coding Standards](http://www.richardrodger.com/2012/11/03/why-i-have-given-up-on-coding-standards/) @www.richardrodger.com
*   [Why Coding Conventions?](https://msdn.microsoft.com/en-us/library/aa733744.aspx) @msdn.microsoft.com
*   [Why Coding Style Matters](https://www.smashingmagazine.com/2012/10/why-coding-style-matters/) @www.smashingmagazine.com
*   [Why You Need Coding Standards](https://www.sitepoint.com/coding-standards/) @www.sitepoint.com
*   [Why Coding Standards Matter](http://paul-m-jones.com/archives/3) @paul-m-jones.com

### Dependency Management

Graphiti, DFTools and Preesm projects depend on other third party projects and libraries. Since they are sets of Eclipse plugins, their dependencies should be Elcipse plugins. Although it is possible to include plain Java archives within an Eclipse plugin source tree (see [this thread](http://stackoverflow.com/questions/5744520/adding-jars-to-a-eclipse-plugin) for instance), we decided to avoid doing that and choosed to take advantage of the [plugin discovery mechanism](https://wiki.eclipse.org/Equinox/p2/Discovery) integrated within Eclipse and the Maven Tycho plugin.

This dependency mechanism is very similar to the [Maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html). The main difference is the bundles and repositories type: Maven resolves plain Java jars from Maven repositories (i.e. [Nexus](http://www.sonatype.org/nexus/) or [Artifactory](https://www.jfrog.com/artifactory/)),  whereas Tycho and Eclipse resolve Eclipse plugin jars (OSGi bundles) from P2 repositories (Eclipse update-sites).

In order to use a plain Java jar using this mechanism, it first needs to be converted into an Eclipse plugin. As explained in this [article](www.vogella.com/tutorials/EclipseJarToPlugin/article.html), it can be done manually within the Eclipse IDE or automatically using the [p2-maven-plugin](https://github.com/reficio/p2-maven-plugin) or [Gradle](https://gradle.org/).

For Graphiti, DFTools and Preesm, all the plain Java jars have been externalized in the [ExternalDeps](https://github.com/preesm/externaldeps) project. This project consists of a Maven POM file that is configured to fetch all the required jars from online Maven repositories and convert them into Eclipse plugin, before deploying them on the Preesm update site.

Project Structure
-----------------

ExternalDeps, Graphiti, DFTools and Preesm resources are located in 2 places:
*   Github repositories
*   Preesm website on Sourceforge

### Git Repositories (Github)

All the source code for the projects is hosted on Github (under [Preesm team](https://github.com/preesm)):
*   ExternalDeps: [https://github.com/preesm/externaldeps](https://github.com/preesm/externaldeps)
*   Graphiti: [https://github.com/preesm/graphiti](https://github.com/preesm/graphiti)
*   DFTools: [https://github.com/preesm/dftools](https://github.com/preesm/dftools)
*   Preesm: [https://github.com/preesm/preesm](https://github.com/preesm/preesm)
*   Preesm-apps (Preesm small use cases and tutorials): [https://github.com/preesm/preesm-apps](https://github.com/preesm/preesm-apps)

Documentation about the release process (this file) is in the Preesm git repository, under [**releng/README.md**](https://github.com/preesm/preesm/blob/develop/releng/README.md)

Dedicated Maven plugins used during project builds are also hosted there:
*   [Sftp Maven plugin](https://github.com/preesm/sftp-maven-plugin)
*   [GenFeature Maven plugin](https://github.com/preesm/genfeature-maven-plugin)

Their use is detailled later in this document and in the [ExternalDeps Readme](https://github.com/preesm/externaldeps).

#### Source Code Structure

The Git repositories are organized as follows:
*   **/plugins**: the source code of the projects (Graphiti, DFTools, Preesm);
*   **/releng**: the release engineering files (see below);
*   **/test-fragments**: the [test plug-in fragments](http://www.modumind.com/2007/06/20/unit-testing-plug-ins-with-fragments/) for functional and unit testing;
*   **/test**: the integration and end-to-end tests

#### The .mailmap File

At the root of the Git repositories lies a file named `.mailmap`.  This files is used to associate different author names and mail addresses to one unique identity.

For instance let say Developer John Smith commits using **jsmith** name and **jsmith@company.com**  mail at work, and using **"John Smith"** name and **john.smith@public.net** mail from home. The git log would differentiate both users whereas they refer to the same identity. To have both of them show the proper entry (let say **"John Smith"** and **john.smith@company.com**), one would have the following `.mailmap` file at the root of the git repository:
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

Sourceforge also hosts the generated Javadoc API, the product releases and the update site (see links below). For uploads, see Sourceforge Documentation:
*   [Release Files for Download](https://sourceforge.net/p/forge/documentation/Release%20Files%20for%20Download/): how to release files on SourceForge File Release Service;
*   [Shell Service](https://sourceforge.net/p/forge/documentation/Shell%20Service/): how to instanciate interactive shell on SourceForge servers;

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

Build Process in Maven
----------------------

This section details how the Preesm project is built using Maven. Graphiti and DFtools are built using a similar process. The [ExternalDeps Readme](https://github.com/preesm/externaldeps/blob/master/README.md) details the specific parts of its process. For the site and products generation and deploy phases, please read the [Release Engineering in Maven](#release-engineering-in-maven) section.

### Overview

Maven build processes are definied in [POM files](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html). The POM files can define build properties, reference external repositores, declare sub modules, call and configure Maven plugins, define profiles, etc.

Each Eclipse plugin has its own POM file. On top of that, there is a [parent POM](http://www.javavillage.in/maven-parent-pom.php), that defines project wide properties, Maven plugin configuration, [Maven profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html), and the list of [submodules](https://maven.apache.org/guides/mini/guide-multiple-modules.html). The projects also have few intermediate POM files (`releng/pom.xml`, `test-fragments/pom.xml`, ...). They add some configuration for dedicated parts of the test or release process that can be omitted during the packaging.


The build of the project is triggered using the following command: `mvn clean verify`. This calls two Maven goals from two different [Maven Build Lifecycles](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html#Build_Lifecycle_Basics):
*   The **clean** goal from the *clean* lifecycle: this goal cleans the current project and all its enabled submodules. This ensures no generated file is kept between builds.
*   The **verify** goals from the *default* lifecycle: this goal validates project configuration, generates sources, checks their compliance wrt. the coding policy, compiles them, packages them and finally fires the tests.

The dependencies are all defined in MANIFEST.MF files within the Eclipse plugins and test fragments. The Tycho Maven plugin is responsible for resolving these Eclipse dependencies and fetch them from online remote P2 repositories (see below). All the Maven plugins used during the build process are available on the Maven Central repository (enabled by default, see [Super POM](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#Super_POM)). There should be no plain Java jar dependencies (see [Dependency Management](#dependency-management)).

### Dependencies

Preesm is a set a Eclipse plugins, thus its dependencies are OSGi dependencies. They are defined in the MANIFEST.MF file of every Eclipse plugin. The Tycho Maven plug-in is then reponsible for resolving such dependencies during the Maven build process. There should be no `<dependencies>` section in the POM files.

Third party plugins dependencies are resolved using external P2 repositories, such as the [Eclipse project update site](http://ftp.fau.de/eclipse/releases/) for the core components of Eclipse, [TMF releases update site](http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/) for the XTend runtime libraries, or the [Preesm update site](http://preesm.sourceforge.net/eclipse/update-site/) for specific third party (see ExternalDeps project), Graphiti and DFTools dependencies. These repositories are definied in the parent POM file (at the root of the git repository):

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

In order to make sure anyone can build your version of the project, it is better to clean the local repository before calling the build command. This will ensure you did not install temporary versions in your local repository that will not be available to other developers.

**Note:** if Maven is called with a goal in the default lifecycle that is before the **package** goal (for instance **compile**), the dependencies will not be resolved since modules will not be packaged. Thus the Tycho plugin will try to resolve dependencies from the [local repository](https://www.mkyong.com/maven/where-is-maven-local-repository/) and then remote ones. This will cause failure is the required version is not available or if there has been changes in the API and the version did not increase. The same failure can occur if one tries to build one of the submodules independently from the others. To circumvent this, one could first **install** (see [goal doc.](http://maven.apache.org/plugins/maven-install-plugin/)) the dependencies to the local repository, then compile only or package a submodule only.

### Profiles

Two [Maven build profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html) are defined in the parent POM:
#### releng
A profile for enabling release engineering modules and plugins. See [Release Engineering in Maven](#release-engineering-in-maven).

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

### Phase Binding and Configuration Details

This section details what plugins are bound to which phases (including clean lifecycle and tests, but not releng profile) and their configuration and role in the build process.

#### clean

*   [maven-clean-plugin](https://maven.apache.org/plugins/maven-clean-plugin/): Add a pattern to clean the Xtend generated Java files under */xtend-gen/*.

#### initialize

*   [directory-maven-plugin](https://github.com/jdcasey/directory-maven-plugin): initialize the property **main.basedir** with the path to the parent project directory. This property is used in the Checkstyle configuration for having a consistent path to its configuration file from any submodule.
*   [jacoco-maven-plugin](http://www.eclemma.org/jacoco/trunk/doc/prepare-agent-mojo.html): Used for test coverage compuatin. See plugin documentation. Defined in the test fragments POM.
*   [org.eclipse.m2e:lifecycle-mapping](http://www.eclipse.org/m2e/documentation/m2e-execution-not-covered.html): Tell the Eclipse Maven Plugin to ignore some Maven plugins. More details in the [Eclipse setup](#eclipse-setup) section.

#### generate-sources

*   [xtend-maven-plugin](https://eclipse.org/Xtext/documentation/350_continuous_integration.html): Compiles XTend source files from **/xtend-src** to Java files in **/xtend-gen**.

#### process-sources

*   [maven-checkstyle-plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/): Checkstyle Maven plugin for checking code style compliance. Explicitly bound to **process-sources** phase. Force Checkstyle version (default version does not support Java 8 syntax). Add configuration file path.

#### compile

*   [tycho-compiler-plugin](https://eclipse.org/tycho/sitedocs/tycho-compiler-plugin/compile-mojo.html): Compiles the Java sources using Eclipse dependencies.

#### package

*   [tycho-packaging-plugin](https://eclipse.org/tycho/sitedocs/tycho-packaging-plugin/package-plugin-mojo.html): Bundle modules as Eclipse plugins.

#### integration-test

*   [tycho-surefire-plugin](https://eclipse.org/tycho/sitedocs/tycho-surefire/tycho-surefire-plugin/test-mojo.html): Fire tests within an Eclipse runtime environment.

#### deploy

*   [maven-deploy-plugin](http://maven.apache.org/plugins/maven-deploy-plugin/): disable the default deploy plugin. This is due to issues when deploying P2 repositories on Sourceforge. The actual deploy procedure is detailled in the [Release Engineering in Maven](#release-engineering-in-maven) section.

Eclipse Setup
-------------

Eclipse is the prefered IDE for developing Preesm. The developer setup is detailed on the [Sourceforge website](http://preesm.sourceforge.net/website/index.php?id=building-preesm). This section details the links between the Maven configuration and the Eclipse setup.

**Note:** Eclipse should not be used to package or release.

### M2Eclipse

Most of the job is done by the [M2Eclipse Eclipse plugin](http://www.eclipse.org/m2e/). This plugin allows to import Maven projects in the Eclipse workspace. It also reads the POM files and configure Eclipse accordingly.

Some Maven plugins are however not handled by the M2E plugin. This is the case for the **directory-maven-plugin**. This plugin should not affect the Eclipse build process, and is therefore add to the ignore list in M2E (see [initialize phase](#initialize)). Indeed the Checkstyle configuration for Eclipse is detailed [here](http://preesm.sourceforge.net/website/index.php?id=building-preesm) and does not need the **main.basedir** property.

Some other Maven plugins need to be supported by Eclipse, as the Tycho plugin that sets some Eclipse plugin configuration up. Therefore we need to install some M2E extensions (called connector) to support them, using the [M2E Tycho connector](https://github.com/tesla/m2eclipse-tycho) (installed from this [update site](http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/)).

### Installing Dependencies

The third party dependencies must be installed through update sites. All of them are bundled with the Dev Meta Feature generated during the release process (see below). It is also possible to setup an Eclipse for developping Preesm along with Graphiti and/or DFTools sources (see this [documentation](http://preesm.sourceforge.net/website/index.php?id=working-with-dftoolsgraphiti-source)).

### Eclipse Preferences

Eclipse comes with many development facilities. Among them is the code formatter. We provide an [Eclipse Preference File](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Ftasks%2Ftimpandexp.htm) that comes with a formatter configuration that respects the Checkstyle coding policy and that is called upon save.

Various small configurations are also included in this preference file (see [source](VAADER_eclipse_preferences.epf)).

### Running Maven from Eclipse

As aforementioned, the Eclipse IDE is best to develop with Preesm, but should not be used for packaging and releasing. Indeed the [Plugin Development Environment build tools](https://projects.eclipse.org/projects/eclipse.pde) provide all necessary components for the plugin development life. However the configuration differs from the Maven plugins (for instance the Eclipse update site builder uses a file named **site.xml** whereas the [tycho-p2-repository-plugin](http://www.eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-repository-plugin/assemble-repository-mojo.html) reads a file named **category.xml**). The release process is tuned for Maven build so that it can be called from a continuous integration platform.

Thankfully, the M2E Eclipse plugins come with facilities to run Maven goals from the Eclipse IDE, without having to install a local Maven distribution. This is done by running any of the imported Maven project as "Maven build" (see example [here](https://books.sonatype.com/m2eclipse-book/reference/running-sect-running-maven-builds.html)). Goals, profiles and parameters can be set in the Eclipse interface. This should be used if one wants to [update project version](#update-project-version) or [release/deploy](#deploy-from-eclipse) Preesm from Eclipse.

### Missing Source Features

As mentioned on the [Preesm website](http://preesm.sourceforge.net/website/index.php?id=working-with-dftoolsgraphiti-source), some warnings can appear when working with Graphiti and DFTools source code. This is nothing to be wary of as these "missing" features are actually automatically generated during the **package** phase by the [tycho-source-feature-plugin](https://eclipse.org/tycho/sitedocs-extras/tycho-source-feature-plugin/source-feature-mojo.html).

Release Engineering
-------------------

This section explains what are the sources and targets of the deploy phase.

### Overview

After the Preesm Eclipse plugins are built, they have to be bundled and deployed in order to be distributed to the end users. The common way to distribute Eclipse plugins is to use [update sites](http://agile.csc.ncsu.edu/SEMaterials/tutorials/install_plugin/index_v35.html). The Tycho Maven plugins provide such functionality and the releng (short term for release engineering) POM file produces one update site. The [tycho-p2-director-plugin](https://eclipse.org/tycho/sitedocs/tycho-p2/tycho-p2-director-plugin/plugin-info.html) also enable the construction of [Eclipse products](https://wiki.eclipse.org/FAQ_What_is_an_Eclipse_product%3F). Finally, for development purposes, the Javadoc API is also generated and integrated to the deployed content.

### Versionning

*   How/when update version: [Semantic Versioning](http://semver.org/);
*   See [Update Project Version](#update-project-version). This procedure updates version in all the POM files, in the MANIFEST.MF file of all included submodules, and in the features. Generated site and product names are also impacted.

### Javadoc

The Javadoc is generated only when the releng profile is enabled. It is generated during the phase **process-sources** using the [maven-javadoc-plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/). In order to facilitate browsing the Javadoc of several plugins, the submodule **complete.site** also enables the aggregation of the Javadoc of the plugins during the **package** phase.

### Feature

The most basic elements of an update site is an [Installable Unit (IU)](https://wiki.eclipse.org/Installable_Units). Eclipse plugins are IUs. However in order to install all plugins at once, an [Eclipse feature](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm) can be used.

An Eclipse feature is declared as an Eclipse project, with a [feature.xml](https://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fmisc%2Ffeature_manifest.html) file defining various information, as the list of plugins it contains, the license, contacts, referenced update sites, etc. It can be built using the Tycho Maven plugins by specifying [eclipse-feature](https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-feature) the packaging type.

The Preesm update site and product both use this feature during their build process.

### Dev Meta Feature

To ease the devloper  job, we provide another feature designed for setting up the development environment for Preesm. This feature actually imports other features. This can be seen as adding dependencies in the Eclipse IDE toward other features. This "meta" feature imports the following features:
*   ExternalDeps (third party dependencies);
*   Graphiti And Dftools features and source features;
*   Xtend, GEF, EMF and Graphiti (from eclipse.org) SDKs;
*   Git and Checkstyle integration;
*   M2Eclipse and its tycho connector.

Note that these features have to be **imported** and not **included** in order to avoid install errors with conflicting versions.
Requires new repositories from the feature to make sure latest releases are installed (even though the Preesm update site is self contained, it does not contain updates since last release):
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

### Complete Site

*   tycho-source-feature-plugin
*   tycho-source-plugin
*   ant tasks


### Update Online Update Site

To update the online update site, the `tycho-p2-extras-plugin` is used, with the
goal `mirror`

*   Mirror meta data only using http download: speed up deploy phase a lot
*   disable cache in [download-maven-plugin](https://github.com/maven-download-plugin/maven-download-plugin) to avoid overriding meta datas.


### Product

*   products (preesm + CDT + equinox + required)
*   Possibility to define dev products in the future...

### Deploy Phase

*   How to deploy (windows, mac, linux)
*   deploy process
*   Maven plugin repo (sftp plugin + genfeature plugin)



Continuous Integration
----------------------

Howto
-----

### Update Project Version

In the root folder of the project, run `mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z
`. This can be done from Eclipse (see procedure to [call Maven from Eclipse](#running-maven-from-eclipse)):

![Run configuration for updating versions](doc/setNewVersionWithMavenFromEclipse.png "Run configuration for calling 'mvn -P releng org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=X.Y.Z' from Eclipse.")

Alternatively, from a shell, the script `/releng/update-version.sh X.Y.Z` wraps the maven call.

### Deploy from Eclipse

### Add New Dependency

### Add New Repository

### Change Checkstyle Coding Style

### Move Online Resources

### Update to a New Eclipse Version
