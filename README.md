Open Hue SDK
======================

This project started in early 2013, before Phillips was kind enough to document their API, and as such is not 100% complete.

The SDK currently allows for registering with th bridge, finding and controlling lights.
Added to the SDK is a custom "CustomAlert" object that will allow for colored light flashes.

Issues
------------
Missing from the SDK in its current state is
* Groups 
* Scenes

Building and Including
-----
This library and the included example are built using Gradle.

This library is available on Maven. To include it in your project, add the following to your root build.gradle

    repositories {
        mavenCentral()
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
    
And add the following to your app build.gradle

```compile 'com.t3hh4xx0r:open-hue-sdk:0.0.1-SNAPSHOT@aar'```


**Please check out the provided example for more details**

