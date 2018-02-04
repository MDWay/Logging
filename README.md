# Logging [![Jitpack](https://jitpack.io/v/romangraef/Logging.svg)](https://jitpack.io/#romangraef/Logging)
a simple logging utility.
use like the following:

```java
Logger logger = Logger.getLogger(); // with current classname as loggername
// or
Logger logger = Logger.getLogger("logger-name");
```

and actually log with: 
```java
logger.log("Log string");
logger.logf("%s %d", "String formatting is", 1337)
```

You can even use it in other loggers if they support `PrintStream`s or set it as default `System.out`:
```java
System.setOut(logger.getAsPrintStream());
```

## Installation
Install via [![Jitpack](https://jitpack.io/v/romangraef/Logging.svg)](https://jitpack.io/#romangraef/Logging)
