# MinnieTwitter Demo Application

## About the App
```
The Minnie-Twitter app helps users to post and interact with messages known as "tweets".
Registered users can tweet and view their tweet history in timeline.
```
## Amino MicroServices
```
1. TagManager:
    Manages the tags for tweets which involves adding the tags for tweets 
    and getting the tweets by passing the tags. This is using default DM.

2. UserManager:
    This micro service is responsible for user addition, deletion and getting the user.
    It will verify the user authentication. This is using AtLeastOnceRPCPolicy DM.
     
3. TwitterManager:
   Helps to get Tagmanager and UserManager. This is using default DM.
   
4. Timeline:
    Helps user to tweet and view the tweets. This is using AtLeastOnceRPCPolicy DM.
    
5. User:
   It contains the user information like user name, password and timeline.
   This is using default DM.

```

Follow the steps to build and run minnie-twitter demo app.

## Add Android Properties
```shell
This can be done in two ways:

1. Through IDE:
    Import the gradle project in the Android Studio and the IDE itself adds a local.properties file
    in your project structure with the path to your sdk defined.
    
Alternatively,

2. From the command prompt:
    $ cd DCAP-Sapphire-Examples/minnieTwitter/
    > cat >> local.properties  << EOF
    ndk.dir=<your ndk dir>
    sdk.dir=<your sdk dir>
    EOF
```


## Signing the release-apk file and building the App
### Generate keystores for signing Android apps
```
$ cd DCAP-Sapphire-Examples/minnieTwitter/
$ keytool -genkey -v -keystore release.keystore -storepass xxxxxx -alias xxxxxx -keypass xxxxxx -keyalg RSA -keysize 2048 -validity 10000
```
### Building the App
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew build
```
## Environment Setup
1. Replace the below ip addresses with the ip address of oms and kernelServer respectively:

    DCAP-Sapphire-Examples/**gradle.properties** 
    ```
    omsIp = 127.0.0.1
    kernelServer1Ip = 127.0.0.1
    ```
    
2.  Replace the first argument passed to the hostAddress with the ```omsIp``` address:

    File path: DCAP-Sapphire-Examples/minnieTwitter/src/main/java/amino/run/appexamples/minnietwitter/glue/**Configuration.java**
    ```
    public static String [] hostAddress = { "127.0.0.1", "22346", "10.0.2.15", "22345" };
    ```
    The third argument above in the hostAddress is your device ip address.
    
## Local deployment

### Run OMS
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew minnieTwitter:subprojects:runoms
```
### Run KernelServer
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew minnieTwitter:subprojects:runks
```

### Run App
   Start the app from within the IDE by clicking on the run app icon.