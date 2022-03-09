# Config Manager
[![Latest version](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/config-manager/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/config-manager/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aconfig-manager&metric=alert_status)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aconfig-manager)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aconfig-manager&metric=coverage)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aconfig-manager)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Configuration manager to load and populate configurations from distinct formats and locations as system properties to config applications 

## Motivation
Loading conifurations is something booring that needs to be done in each application, in the past all java web applications should use JNDI or application server resources, but more and more applications are managed as standalone docker images running as a single application and JVM instance. 
The scenery has changed and we can go back to use system properties as reliable shared configuration between all application layers. But as been said times has changed and now properties files became too simple to manage current configurations, and for this reason is necessary some tool to load from distinct formats and overloading distinc configs.

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

2. Use the library you prefer, to main reference please download and look in javadoc, we intend to keep it updated and explicative


