# process-warden

[![Build Status](https://travis-ci.org/johnlcox/process-warden.png)](https://travis-ci.org/johnlcox/process-warden)

process-warden provides a safer wrapper around the built in Java Process and ProcessBuilder classes for executing native subprocesses.  Here is a simple example with Java 6:

```java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
FinalizedProcess process = pb.start();
try {
  int returnVal = process.waitFor(5000);
} finally {
  process.close();
}
```

If running on Java 7, try-with-resources can be used:
```java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
try (FinalizedProcess process = pb.start()) {
  int returnVal = process.waitFor(5000);
}
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
