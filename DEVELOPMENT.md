Gluon Wave Application
====

This repository contains the Gluon Wave Application, which is using the 
Signal protocol with Java and JavaFX.

About Signal
------------

[Signal](https:/signal.org) is an encryption tool enabling end-to-end
encryption. Messages sent via the Signal Protocol are encrypted by the
sender, and decrypted by the recipient, using a combination of clever
techniques including [Extended Triple Diffie-Hellman](https://signal.org/docs/specifications/x3dh/)
and [Double Ratchet](https://signal.org/docs/specifications/doubleratchet/).

The Signal protocol and its implementations are open-source and free to
use (as long as the GPL license is respected). Signal respects users privacy
and is not showing ads. Signal is a non-profit organisation, and accepts
[donations](https://signal.org/donate).

Signal on mobile and desktop
----------------------------

In order to use Signal, you need to install it on your phone first.
Go to https://signal.org/download/ and follow the links for Android or
iOS. Once you are using Signal on your phone, you can _pair_ other
devices, e.g. your desktop or laptop, and use Signal on those devices as
well -- using the same account as the one you use on your phone.
Currently, there is an electron-based desktop application that you can
use on your system. The Wave Application in the repository you're 
currently looking at is a Java and JavaFX based alternative for this electron
based application.

Signal and Java
---------------

We didn't have to start from scratch when writing this application.
There is a Java implementation of the Signal protocol that is (or was) used by
the Android client. That implementation served as the basis for the Java
API's we needed for the Wave Application. We made a number of changes though,
since we don't have to worry about Android restrictions. We rather use
the latest Java, as developed in the [OpenJDK](https://openjdk.java.net).
We forked the Signal repositories and updated them to Java 17.

Wave App
--------

The application in this repository uses the libraries described in the
previous section, and creates a JavaFX user interface around them.

Running the Wave App
--------------------

You can run Gluon Wave in 3 ways:

* Download and run the native executables for Windows, Mac or Linux
* Download and run the jpackaged installers for Windows, Mac or Linux
* Build the code from this repository and run it.

Instructions for these options are in the [README](https://github.com/gluonhq/wave-app/README.md).
They don't require a JVM at runtime. The first option leverages
[GraalVM native-image](https://graalvm.org), the second option leverages
the Java packager that is part of the JDK distributions.

Issues
------

There are a bunch of known and unknown issues, which you can report in
the issue tracker of this repository.

Missing functionality
---------------------
* No support for groups
* No support for attachments
* No support for stickers

... but that is just a matter of time.
