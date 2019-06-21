# Kubernetes Deployment

### Prerequisites
Requires docker to be installed. We have tested our deployment against docker version ```18.09.6```

### Steps
Setup Kubernetes on your system by running the following script: 
```
bash k8s-deployment/dind_k8s_cluster.sh up
```  
1. The script downloads kubectl and sets it in ```/usr/local/bin``` PATH environment variable.  
**NOTE:**  
i. The script downloads kubectl version ```v1.13.0```, if you already have some other version of kubectl downloaded keep a backup of it.  
ii. Ensure ```/usr/local/bin``` is set in your PATH environment variable as this is where the script places your kubectl.  
2. The script sets up Docker in Docker(dind) version ```v1.13``` on your system. If you want to be running dind using some alternate version, follow steps given in this [link](https://github.com/kubernetes-sigs/kubeadm-dind-cluster).   
3. Upon successful execution of the script a unique link to access the kubernetes-dashboard is generated.  
![kubernetes-dashboard](images/dashboard.png)

## OMS and KernelServer deployment in Kubernetess
### Prerequisites
1. Set credentials ```repo.username``` and ```repo.password``` in ```Amino.Run-Demos/gradle.properties``` file for access to the [dockerhub](https://hub.docker.com) repo.
2. In the same file, change the port in the ```baseURL``` and replace it with the proxy port of kubernetes (Proxy port varies as per local kubernetes deployment).  
![kubernetes-proxy-port](images/dashboard.png)
3. Ensure generation of fatJar for your application.  
    ```
    bash gradlew build
    ```

### Steps
Deploy OMS and kernelServer by executing the following command:
```
bash gradlew <appName>:subprojects:deployKernelServer
```
1. Upon execution of the above task a Dockerfile gets generated in respective ```subproject``` folder of application.
2. The same Dockerfile is further used to build an image. This image contains the fatJar file and is used to run the OMS, kernelServer and the application.
3. The image is then pushed to the [dockerhub](https://hub.docker.com) using the credentials set in the gradle.properties file: 
    ```
    repo.username=xxxx
    repo.password=xxxx
    ```
4. Further deployment yaml files for OMS and kernelServer is generated using ```oms.template.yml``` and ```kernelserver.template.yml``` in respective applications.  
    These yaml files also contain kubernetes service configurations. **Currently we make use of NodePort to expose OMS and kernelServer**.
5. Finally the OMS and kernelServer is deployed respectively.

## Running the Application
### Steps

1. Update the OMS ip and port in the configuration file of the applications.  
i. Replace OMS ip with node ip.  
ii. Replace OMS port with OMS NodePort. Details for the same can be found by viewing the oms logs.  
    ![oms-logs](images/nodePort.png)  
2. For chess application we also need to change the kernelServers ip and port in the configuration file. Details for kernelServer ip and port can be found by viewing the oms logs as highlighted in the image above.  
3. Finally run the app by clicking on the run app icon from within the IDE.

 
       

 
   
    
