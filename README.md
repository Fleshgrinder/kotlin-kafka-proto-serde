# Module kafka-proto-serde

The `kafka-proto-serde` module provides an [Apache Kafka][Kafka]
[Serde]/[Serializer]/[Deserializer] implementation for 
[Google’s Protocol Buffers][Proto] (aka. _Protobuf_, _Proto_) and some
convenience building blocks to ease the Kotlin experience while working with the
Java based libraries.

## Getting Started

### Prerequisites

This library is compatible with the following minimum library versions and
everything that was released at the time of writing this document:

* [`com.google.protobuf:protobuf-java:[2.5,)`][proto-releases]
* [`org.apache.kafka:kafka-clients:[0.10,)`][kafka-releases]

Please file an [issue][issues] if you discovered some incompatibility.

### Installing

#### Gradle

Add the following dependency to your `build.gradle.kts` script:

```kotlin
dependencies {
    implementation("com.fleshgrinder:kafka-proto-serde:1.0.0")
}
```

#### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.fleshgrinder</groupId>
    <artifactId>kafka-proto-serde</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Running the Tests

You can execute the usual `gradle test` command to run the tests with your
current JVM installation but to verify that everything works with all
dependencies `make matrix-test` (or `bin/matrix-test`) is available (requires
[Bash] and [Docker]). Of course, in case you create a PR, continuous
integration, via [CircleCI], executes all tests automatically.

## Project Info

* Please read [CONTRIBUTING.md](CONTRIBUTING.md) if you want to contribute, 
  which would be very welcome.
* We use [Semantic Versioning] and [Keep a Changelog], available versions and
  changes are listed on our [releases] page.
* This module is licensed under the [Unlicense], see
  [UNLICENSE.md](UNLICENSE.md) for details.

[contributors]: https://github.com/protocolbuffers/protobuf/contributors
[issues]: https://github.com/Fleshgrinder/kotlin-kafka-proto-serde/issues
[releases]: https://github.com/Fleshgrinder/kotlin-kafka-proto-serde/releases

[Kafka]: https://kafka.apache.org/
[kafka-releases]: https://github.com/apache/kafka/releases
[Serde]: https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/serialization/Serde.java
[Serializer]: https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/serialization/Serializer.java
[Deserializer]: https://github.com/apache/kafka/blob/trunk/clients/src/main/java/org/apache/kafka/common/serialization/Deserializer.java

[Proto]: https://developers.google.com/protocol-buffers/
[proto-releases]: https://github.com/protocolbuffers/protobuf/releases

[Bash]: https://www.gnu.org/software/bash/
[Docker]: https://www.docker.com/
[CircleCI]: https://circleci.com/
[Semantic Versioning]: http://semver.org/
[Keep a Changelog]: https://keepachangelog.com/
[Unlicense]: https://unlicense.org/
