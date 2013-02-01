# process-warden

[![Build Status](https://travis-ci.org/johnlcox/process-warden.png)](https://travis-ci.org/johnlcox/process-warden)

process-warden provide a safer wrapper around the built in Java Process and ProcessBuilder classes for executing native subprocesses.  Here is a simple example with Java 6:

``` java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
FinalizedProcess process = pb.start();
try {
  int returnVal = process.waitFor(5000);
} finally {
  process.close();
}
```

If running on Java 7, try-with-resources can be used:
``` java
FinalizedProcessBuilder pb = new FinalizedProcessBuilder("myCommand", "myArg");
try (FinalizedProcess process = pb.start()) {
  int returnVal = process.waitFor(5000);
}
```
