# Chess android demo application

### About the App
The chess app is an android board game in which the user competes against the system.

### Amino MicroServices
1. **ChessManager** :
    This class implements the MicroService and makes use of the Default Deployment Manager(DM).

2. **SimpleEngine** :
    Second class in the app to implement the Microservice and makes use of the ExplicitMigration DM.

### Local Deployment

### Add Android Properties
This can be done in two ways:

1. **Through IDE**:
    Import the gradle project in the Android Studio and the IDE itself adds a local.properties file in your project structure with the path to your sdk defined.
        
Alternatively,

2. **From the command prompt**:
```shell
    $ cd DCAP-Sapphire-Examples/hanksTodo/
    > cat >> local.properties  << EOF
    ndk.dir=<your ndk dir>
    sdk.dir=<your sdk dir>
    EOF
```

### Build App
Start by building the app, this ensures the generation of the required stubs.
```

$ cd DCAP-Sapphire-Examples/
$ ./gradlew build
```
### Environment Setup
1. Replace the below ip addresses with the ip address of oms and kernelServers respectively:

    DCAP-Sapphire-Examples/**gradle.properties** 
    ```
    omsIp = 127.0.0.1
    kernelServer1Ip = 127.0.0.1
    kernelServer2Ip = 127.0.0.1
    ```
    
2.  Replace the first argument passed to the hostAddress with the ```omsIp``` address:

    DCAP-Sapphire-Examples/hanksTodo/src/main/java/amino/run/appexamples/hankstodo/glue/**Configuration.java**
    ```
    public static String[] hostAddress = { "127.0.0.1", "22346", "10.0.2.15", "22345" };
    ```
    The third argument above in the hostAddress is your device ip address.
    
    Also, replace the first and third argument passed to the kernelServerAddress with the ```kernelServer1Ip``` and ```kernelServer2Ip``` respectively:
    ```
    public static String[] kernelServerAddress = { "127.0.0.1", "22345", "127.0.0.1", "22344" };
    ```

### Run OMS
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew chess:subprojects:runoms
```

### Run KernelServer
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew chess:subprojects:runks
```

### Run a second KernelServer
```
$ cd DCAP-Sapphire-Examples/
$ ./gradlew chess:subprojects:runks2
```

### Run App
Start the app from within the IDE by clicking on the run app icon.
