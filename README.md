# Netdata Java Plugin Daemon

[![Build Status](https://travis-ci.org/simonnagl/netdata-plugin-java-daemon.svg?branch=master)](https://travis-ci.org/simonnagl/netdata-plugin-java-daemon)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c5196ea860ba4cb8a47f40c5264cc17f)](https://www.codacy.com/app/simonnagl/netdata-plugin-java-daemon?utm_source=github.com&utm_medium=referral&utm_content=simonnagl/netdata-plugin-java-daemon&utm_campaign=badger)

A [netdata](https://github.com/firehol/netdata) Java plugin daemon.

## Java Plugins

- JMX Collector (Work in progress)

## Installation

```
mvn package
cp src/main/sh/java.d.plugin /your/netdata/plugin/dir
cp -r config/* /your/netdata/config/dir
cp target/java-daemon-0.1.0-SNAPSHOT.jar /tmp
```

## License

netdata-plugin-java-daemon is [GPLv3+](LICENSE).

It re-distributes other open-source tools and libraries. Please check the [third party licenses](LICENSE-REDISTRIBUTED.md).