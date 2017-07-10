# Netdata Java Plugin Daemon

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