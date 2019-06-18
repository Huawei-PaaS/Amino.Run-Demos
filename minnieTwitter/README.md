# MinnieTwitter Android Demo Application

### About the App
```
The Minnie-Twitter app helps users to post and interact with messages known as "tweets".
Registered users can tweet and view their tweet history in timeline.
```
### Amino MicroServices

1. **TagManager:**
    Manages the tags for tweets which involves adding the tags for tweets,
    getting the tweets by passing the tags and makes use of the Default Deployment Manager(DM).

2. **UserManager:**
    This microservice is responsible for user addition, deletion, user lookup and makes use of the AtLeastOnceRPC
    Deployment Manager(DM).

3. **TwitterManager:**
   Manages the TagManager, UserManager and makes use of the Default Deployment Manager(DM).

4. **Timeline:**
    Helps User to tweet, view the tweets and makes use of the AtLeastOnceRPC Deployment Manager(DM).

5. **User:**
   It contains the user information like user name, password, timeline and makes use of the Default Deployment Manager(DM).

## Local deployment

### Add Android Properties
```shell
This can be done in two ways:

1. Through IDE:
    Import the gradle project in the Android Studio and the IDE itself adds a local.properties file
    in your project structure with the path to your sdk defined.

Alternatively,

2. From the command prompt:
    $ cd Amino.Run-Demos/minnieTwitter/
    > cat >> local.properties  << EOF
    ndk.dir=<your ndk dir>
    sdk.dir=<your sdk dir>
    EOF
```

### Building the App
```
$ cd Amino.Run-Demos/
$ ./gradlew build
```
### Environment Setup
1. Replace the below ip addresses and ports with that of oms and kernelServers respectively:

    File path: Amino.Run-Demos/**gradle.properties**
    ```
    omsIp = 127.0.0.1
    kernelServer1Ip = 127.0.0.1
    omsPort=22346
    kernelServer1Port=22345
    ```

2.  Replace the first and second argument passed to the **hostAddress** with the ```omsIp``` and ```omsPort``` respectively:

    File path: Amino.Run-Demos/minnieTwitter/src/main/java/amino/run/appexamples/minnietwitter/glue/**Configuration.java**
    ```
    public static String [] hostAddress = { "127.0.0.1", "22346", "10.0.2.15", "22345" };
    ```
    The third and fourth argument above in the hostAddress are your device kernelServer ip and port respectively.

### Run OMS
```
$ cd Amino.Run-Demos/
$ ./gradlew minnieTwitter:subprojects:runoms
```
### Run KernelServer
```
$ cd Amino.Run-Demos/
$ ./gradlew minnieTwitter:subprojects:runks
```

### Run App
   Start the app from within the IDE by clicking on the run app icon.