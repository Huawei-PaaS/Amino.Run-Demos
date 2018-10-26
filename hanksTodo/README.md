# Hankstodo demo application

Follow the following steps to build and run hanksTodo demo app.

### Add Android Properties
```shell
$ cd DCAP-Sapphire-Examples/hanksTodo/
> cat >> local.properties  << EOF
ndk.dir=<your ndk dir>
sdk.dir=<your sdk dir>
EOF
```

### Build App
```
$ cd DCAP-Sapphire-Examples/hanksTodo/
$ ./gradlew build
```

### Run OMS
```
$ cd DCAP-Sapphire-Examples/hanksTodo/
$ ./gradlew runoms
```

### Run KernelServer
```
$ cd DCAP-Sapphire-Examples/hanksTodo/
$ ./gradlew runks
```
