# openalpr-android
[![Release](https://jitpack.io/v/SandroMachado/openalpr-android.svg)](https://jitpack.io/#SandroMachado/openalpr-android)

OpenALPR is an open source Automatic License Plate Recognition library written in C++ with bindings in C#, Java, Node.js, and Python. This project ports this library to Android. You can find the demo application `apk` at the [releases](https://github.com/SandroMachado/openalpr-android/releases) tab.

![Screenshot](images/screenshot.png "Main Activity Sample application")

# Gradle Dependency

## Repository

First, add the following to your app's `build.gradle` file:

```Gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Them include the openalpr-android dependency:

```gradle
dependencies {

    // ... other dependencies here.    	
    compile 'com.github.SandroMachado:openalpr-android:1.1.2'
}
```

# Usage

## Code

Copy the [OpenALPR configuration file](License-Plate-Recognition/openalpr.conf) to your android project assets directory `/main/assets/runtime_data/openalpr.conf`, open it and update the `runtime_dir` to your project directory (for instance, for the sample project the directory is: `runtime_dir = /data/data/com.sandro.openalprsample/runtime_data`). After that just follow the code example bellow. To see a full example check the [sample application](./Sample/OpenALPRSample/app/src/main/java/com/sandro/openalprsample/MainActivity.java).

```Java

static final String ANDROID_DATA_DIR = "/data/data/com.sandro.openalprsample";

final String openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";

String result = OpenALPR.Factory.create(MainActivity.this, ANDROID_DATA_DIR).recognizeWithCountryRegionNConfig("us", "", image.getAbsolutePath(), openAlprConfFile, 10);
```

## Interface

```Java
/*
 Method interface.
*/

/**
 * Recognizes the licence plate.
 *
 * @param country        - Country code to identify (either us for USA or eu for Europe). Default=us.
 * @param region         -  Attempt to match the plate number against a region template (e.g., md for Maryland, ca for California).
 * @param imgFilePath    - Image containing the license plate.
 * @param configFilePath - Config file path (default /etc/openalpr/openalpr.conf)
 * @param topN           - Max number of possible plate numbers to return(default 10)
 *
 * @return - JSON string of results
 */

public String recognizeWithCountryRegionNConfig(String country, String region, String configFilePath, String imgFilePath, int topN);

```
# Sample Application

The repository also includes a [sample application](./Sample/OpenALPRSample) that can be tested with Android Studio.

![Screencast](images/screencast.gif "Main Activity Sample application screencast")

# Credits

 - [OpenALPR](https://github.com/openalpr/openalpr) Parent Project
 - [OpenAlprDroidApp](https://github.com/sujaybhowmick/OpenAlprDroidApp) for the compiled sources and sample that helped port the project to an android library


# How to run

## Compiling OpenALPR

### Clone the latest code from GitHub
```
git clone https://github.com/openalpr/openalpr.git
```

### Setup the build directory
```
cd openalpr/src
mkdir build
cd build
```

### Setup the compile environment
```
cmake -DOpenCV_DIR=/path/to/the/buildDir/of/opencv/  -DOpenCV_FOUND=TRUE -DCMAKE_INSTALL_PREFIX:PATH=/usr -DCMAKE_INSTALL_SYSCONFDIR:PATH=/etc ..
```

### Compile the library
```
make
```

### Install the binaries/libraries to your local system (prefix is /usr)
```
sudo make install
```

### Test the library
```
wget http://easy-clan.com/ski/pics/license_plate.JPG -O lp.jpg
alpr lp.jpg
```
## Running the android application

### Clone the android app code from github
```
git clone https://github.com/Huawei-PaaS/DCAP-Sapphire-Examples.git
git branch LicensePlateAndroid
```

### Add the generated .so

- Add the generated .so to `/License-Plate-Recognition/openALPR/src/main/jniLibs/armeabi-v7a/`

### Run OMS
```
cd License-Plate-Recognition/
./gradlew runoms
```

### Run Kernel Server
```
./gradlew runks
```

### Run application

- Run the application from IDE

