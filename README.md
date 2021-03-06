# dev-setup-wizard
A JavaFX based wizard for setting up a development environment on a Linux virtual machine (VM) - *Sorry, no Windows...*.

[![Build Status](https://jenkins.fuin.org/job/dev-setup-wizard/badge/icon)](https://jenkins.fuin.org/job/dev-setup-wizard/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.fuin.devsupwiz/devsupwiz-base/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.fuin.devsupwiz/devsupwiz-base/)
[![LGPLv3 License](http://img.shields.io/badge/license-LGPLv3-blue.svg)](https://www.gnu.org/licenses/lgpl.html)
[![Java Development Kit 1.8](https://img.shields.io/badge/JDK-1.8-green.svg)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

> :warning: Be aware that this is work in progress - The project has a very low test coverage at the moment :warning:

## Overview
The application is basically a replacement for the well known "how to" documents for setting up a project's development environment.
The wizard should make it easier for a new developer to setup everything that is necessary to start coding.
You could actually do the same thing with a bunch shell scripts, but isn't a nice JavaFX UI much more user-friendly? 
At the end there are also some tasks that will simply execute linux commands in the background to achieve their goal. 

<a href="https://github.com/fuinorg/dev-setup-wizard/raw/master/base/doc/welcome-screen.png" target="_blank"><img src="https://github.com/fuinorg/dev-setup-wizard/raw/master/base/doc/welcome-screen.png" width="320" height="335" alt="Welcome Screen"></a> 

## Standard tasks
There are already some predefined tasks available in the [devsupwiz-tasks](https://github.com/fuinorg/devsupwiz-tasks) repository.

* **set-personal-data**: Sets the developer's personal data like name and email address.
* **set-hostname**: Sets a new hostname for the virtual machine (VM).
* **create-git-config**: Creates and populates the "~/.gitconfig" file.
* **generate-ssh-key**: Generates a new key pair and adds it to the "~/.ssh/config" file. 
* **display-ssh-key**: Displays a newly generated SSH public key. 
* **git-clone**: Clones one or more git repositories. Requires that a valid SSH key is created and added to your git repository (See "generate-ssh-key" task).
* **create-maven-settings**: Creates a ".m2/settings.xml" file with credentials for private repositories. 

More tasks are [planned](https://github.com/fuinorg/devsupwiz-tasks/issues)...

## Custom tasks
It's easily possible to create your own custom tasks for the setup.

Steps:
1. Create a new Java project that will contain your custom task.
2. Develop the non-visual and [JAX-B](https://github.com/javaee/jaxb-v2) enabled task that does the work (Implement the [SetupTask](https://github.com/fuinorg/dev-setup-wizard/blob/master/common/src/main/java/org/fuin/devsupwiz/common/SetupTask.java) or [MultipleInstancesSetupTask](https://github.com/fuinorg/dev-setup-wizard/blob/master/common/src/main/java/org/fuin/devsupwiz/common/MultipleInstancesSetupTask.java) interface).
3. Design the FXML to allow the user to enter some information that will be stored in the task before it executes.
4. Implement the JavaFX controller for the FXML file. The controller validates the user's data and finally updates the task (Implement the [SetupController](https://github.com/fuinorg/dev-setup-wizard/blob/master/common/src/main/java/org/fuin/devsupwiz/common/SetupController.java) interface).
5. Provide the code in a Maven repository. This can be Maven Central for public artifacts or just a simple private webserver (See [this article](https://malalanayake.wordpress.com/2014/03/10/create-simple-maven-repository-on-github/) for explanation). 

Take a look at the [devsupwiz-tasks](https://github.com/fuinorg/devsupwiz-tasks) repository for a full example.

## Limitations
Currently only Linux is supported. This is not really a limitation as the application is meant for customizing a virtual machine (VM).
So you can just download a [pre-build developer VM](https://github.com/fuinorg/lubuntu-developer-vm/) for your Windows PC and run it with [VMware Workstation Player](https://my.vmware.com/de/web/vmware/free#desktop_end_user_computing/vmware_workstation_player/14_0) or [VirtualBox](https://www.virtualbox.org/).   

## Getting started with your project setup

A project setup consists of the following three steps:

1. [Create Maven POM](#1-create-maven-pom)
2. [Create task configuration](#2-create-task-configuration)
3. [Upload Maven POM and task config](#3-upload-maven-pom-and-task-config)

The developer's part:

4. [Prerequisites](#4-prerequisites-developer-pc)
5. [Run the installation](#5-run-the-installation-developer-pc) 


### 1. Create Maven POM
Create a Maven POM for your developer setup. 

It will be used to start the JavaFX wizard application and adds all the necessary JARs for your tasks. 
This allows you to create your own custom tasks and include them just by adding a new dependency to the 'pom.xml'. 


Example:
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <groupId>org.fuin.devsupwiz</groupId>
  <artifactId>devsupwiz-example</artifactId>
  <version>0.1.1</version>

  <dependencies>

    <!-- Base application -->
    <dependency>
      <groupId>org.fuin.devsupwiz</groupId>
      <artifactId>devsupwiz-base</artifactId>
      <version>0.1.1</version>
    </dependency>

    <!-- Base tasks -->
    <dependency>
      <groupId>org.fuin.devsupwiz</groupId>
      <artifactId>devsupwiz-tasks</artifactId>
      <version>0.2.0</version>
    </dependency>

    <!-- Additional tasks -->    
    <!-- ... Add your task libraries here ... -->

  </dependencies>

  <build>
  
    <plugins>
    
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>1.6.0</version>
        <configuration>
          <mainClass>org.fuin.devsupwiz.base.DevSetupWizard</mainClass>
          <!-- Config name defaults to 'project-setup.xml' if the following tag is missing -->
          <arguments>
            <argument>my-project-setup.xml</argument>
          </arguments>
        </configuration>
      </plugin>
      
    </plugins>
    
  </build>

</project>
```

### 2. Create task configuration 
Create a configuration for your setup tasks.

It defines the tasks to execute and also the order in which they are executed.

Example (my-project-setup.xml):
```xml
<dev-setup-wizard name="my-project">
  <tasks>
    <set-personal-data />
    <set-hostname />
    <create-git-config />
    <generate-ssh-key id="1" host="bitbucket.org" />
    <display-ssh-key id="1" ref="generate-ssh-key[1]" />
    <git-clone id="1" target-dir="~/git">
      <repository>git@bitbucket.org:my_account/my-project.git</repository>
      <repository>git@bitbucket.org:my_account/another-one.git</repository>
      <repository>git@bitbucket.org:my_account/whatever.git</repository>
    </git-clone>
    <create-maven-settings id="1" template="~/git/my-project/config/settings.xml" />
  </tasks>
</dev-setup-wizard>
```

The "name" attribute is a unique identifier for the configuration. The project's name is a good choice.

A task consists of 
* **type**: Describes the purpose of the task (e.g. tag name like "generate-ssh-key")
* **id**: Unique name for the task instance. If there is more than one task of the same type, this is used to distinguish the tasks.
* **task-class**: The full qualified name of the task's Java class. This allows dynamic loading of additional, yet unknown, tasks. 
* **attributes** or **elements** (Optional): Some tasks may offer additional attributes (e.g. "target-dir") or elements (e.g. "repository") to customize the task.

### 3. Upload Maven POM and task config
Upload the above 'pom.xml' and the 'my-project-setup.xml' to your server and provide the URL of the POM to the developer.

### 4. Prerequisites (Developer PC)
Make sure the following packages are installed on the developer's machine (VM):
* Java 1.8 Runtime
* Maven 3.3.x 

### 5. Run the installation (Developer PC)
Download the POM: 
```
curl -o pom.xml https://raw.githubusercontent.com/fuinorg/dev-setup-wizard/master/example/pom.xml
```

Execute the wizard: 
```
mvn exec:java
```


* * *


## Snapshots

Snapshots can be found on the [OSS Sonatype Snapshots Repository](http://oss.sonatype.org/content/repositories/snapshots/org/fuin "Snapshot Repository"). 

Add the following to your .m2/settings.xml to enable snapshots in your Maven build:

```xml
<repository>
    <id>sonatype.oss.snapshots</id>
    <name>Sonatype OSS Snapshot Repository</name>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
        <enabled>false</enabled>
    </releases>
    <snapshots>
        <updatePolicy>always</updatePolicy>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

 