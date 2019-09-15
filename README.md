# Netdata Java Orchestrator

[![Build Status](https://travis-ci.org/simonnagl/netdata-java-orchestrator.svg?branch=master)](https://travis-ci.org/simonnagl/netdata-java-orchestrator.svg?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/c5196ea860ba4cb8a47f40c5264cc17f)](https://www.codacy.com/app/simonnagl/netdata-plugin-java-daemon?utm_source=github.com&utm_medium=referral&utm_content=simonnagl/netdata-plugin-java-daemon&utm_campaign=Badge_Coverage)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c5196ea860ba4cb8a47f40c5264cc17f)](https://www.codacy.com/app/simonnagl/netdata-plugin-java-daemon?utm_source=github.com&utm_medium=referral&utm_content=simonnagl/netdata-plugin-java-daemon&utm_campaign=badger)

netdata-java-orchestrator is a [netdata](https://github.com/firehol/netdata) plugin which can collect any data in java and send it to netdata.

## Java Modules

- JMX Collector

## Installation

### 1. Prepare your system

#### Required for compilation

- netdata
- JDK 8.x

#### Required to run netdata

- netdata
- JRE 8.x

### 2. Install netdata-java-orchestrator

Do this to install and run netdata-java-orchestrator:

```(sh)
# download it - the directory 'netdata-java-orchestrator' will be created
git clone https://github.com/simonnagl/netdata-java-orchestrator.git --depth=1
cd netdata-java-orchestrator

# run script with root privileges to build and install the plugin and restart netdata.
netdata-java-orchestrator-installer.sh
````

## Configuration

Configuration files contain JSON Objects.
Additional to the JSON specification Java/C++ style comments (both '/'+'*' and '//' varieties) are allowed.

Each module get's it's own configuration file. The standard configuration should have enogh examples and comments to extend or adapt it. The table below references the classes which describe the JSON schemes of the configuration files.

File                         | Schema | Purpose
---------------------------- | ------ | -------
/etc/netdata/java.d/jmx.conf | [JmxModuleConfiguration](https://github.com/simonnagl/netdata-java-orchestrator/blob/master/src/main/java/org/firehol/netdata/module/jmx/configuration/JmxModuleConfiguration.java)| JMX module configuration


## License

netdata-java-orchestrator is [GPLv3+](LICENSE).
