Wave App
====

The Wave App runs with Gluon Mobile, OpenJFX, OpenJDK 11 and GraalVM

Instructions
------------

* Set `JAVA_HOME` to a JDK 11+
* Install all sub-projects in local maven repository:
```
mvn clean install
```
* Execute the application:
```
cd App && mvn gluonfx:run
```

Instructions for Native Image
------------

* Download the following version of GraalVM and unpack it like you would any other JDK. (e.g. in `/opt`):

  * [GraalVM for Linux](https://download2.gluonhq.com/substrate/graalvm/graalvm-svm-linux-20.1.0-ea+25.zip)
  * [GraalVM for Mac](https://download2.gluonhq.com/substrate/graalvm/graalvm-svm-darwin-20.1.0-ea+25.zip)

* Configure the runtime environment. Set `GRAALVM_HOME` environment variable to the GraalVM installation directory:
```
export GRAALVM_HOME=path-to-graalvm-directory
```

* Native build the application:
```
mvn client:build -pl App
```
* Once the build is successful, the native image be executed by:
```
mvn client:run -pl App
```

## FAQs

Below is a list of frequently asked questions / issues one might face during running ChatApp from source:

### No Device Found

This error comes when the app is started, but we waited too long to scan the QR code

### Network Error

This error normally occurs due to "Rate Limit Exceeded", which means that scanning was tried too often.
We need to allow it to cool down and try again after 1 or 2 minutes.

### Scanning done but no contact list shown

Current workaround is to remove all your linked devices from mobile app, remove `~/.signalfx` directory and re-scan.