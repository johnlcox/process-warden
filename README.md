# process-warden

[![Build Status](https://travis-ci.org/johnlcox/process-warden.png)](https://travis-ci.org/johnlcox/process-warden)

process-warden provides a safer wrapper around the built in Java Process and ProcessBuilder classes for executing native subprocesses.  

## Usage

With Java 6:
```java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
FinalizedProcess process = pb.start();
try {
  int returnVal = process.waitFor(5000);
} finally {
  process.close();
}
```

With Java 7 and try-with-resources:
```java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
try (FinalizedProcess process = pb.start()) {
  int returnVal = process.waitFor(5000);
}
```

Now With Stream Gobbling.  The StreamGobbler will gobble up the input stream, error stream, or both in a separate thread to prevent from blocking your thread.
```java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
pb.gobbleStreams(true);
try (FinalizedProcess process = pb.start()) {
  int returnVal = process.waitFor(5000);
}
```

## Installation

Add it as a maven dependency:
```xml
<dependency>
  <groupId>com.leacox.process</groupId>
  <artifactId>process-warden</artifactId>
  <version>1.1.0</version>
</dependency>
```

## License

Copyright 2013 John Leacox

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
