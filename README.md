# Config Manager
[![Latest version](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/config-manager/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/config-manager/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aconfig-manager&metric=alert_status)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aconfig-manager)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aconfig-manager&metric=coverage)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aconfig-manager)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Configuration manager to load and populate configurations from distinct formats and locations as system properties to config applications 

## Motivation
Loading conifurations is something booring that needs to be done in each application, in the past all java web applications should use JNDI or application server resources, but more and more applications are managed as standalone docker images running as a single application and JVM instance. 
The scenery has changed and we can go back to use system properties as reliable shared configuration between all application layers. But as been said times has changed and now properties files became too simple to manage current configurations, and for this reason is necessary some tool to load from distinct formats and overloading distinc configs.

## Requirements
Java version: JDK8+

### Currently supported formats
* Java Properties
* A simplified yaml without profiles nor inline arrays

### Currently supported schemes
* Classpath
* File


## Restrict

## Quick start
(Please read our [Javadoc](https://config-manager.bytemechanics.org/javadoc/index.html) for further information)

1. First of all include the Jar file in your compile and execution classpath.

**Maven**
```Maven
	<dependency>
		<groupId>org.bytemechanics</groupId>
		<artifactId>config-manager</artifactId>
		<version>X.X.X</version>
	</dependency>
```
**Graddle**
```Gradle
dependencies {
    compile 'org.bytemechanics:config-manager:X.X.X'
}
```

1. Create the ConfigManager instance (keep in account that first configurations will be overrided if exist in the following files)
```Java
   ConfigManagerImpl instance = new ConfigManagerImpl("file://src/test/resources/integral-test-1.yml","classpath://integral-test.yml","classpath://org/bytemechanics/config/manager/internal/integral-test-2.yaml","classpath://integral-test-3.properties");    
```

1. Load configuration, either
   * Directly to System.properties (classic behaviour) 
     ```Java
        instance.load();
     ```
   * Manually (custom behaviour) 
     ```Java
        instance.stream() //Stream<Config>
                .map(toMyObject::convert)
                (...)
     ```


