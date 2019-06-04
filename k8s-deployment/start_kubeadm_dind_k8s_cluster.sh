#!/bin/bash -xe

if [ "`which docker`" == "" ]; then
  echo "ERROR: docker not found. Please install docker CE for your OS."
  exit 1
fi
if [ "`which kubectl`" == "" ]; then
  if [ `awk -F= '/^NAME/{print $2}' /etc/os-release` == '"Ubuntu"' ]; then
    sudo curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.13.0/bin/linux/amd64/kubectl
    sudo chmod +x ./kubectl
    sudo mv ./kubectl /usr/local/bin/kubectl
    sudo kubectl version
  else
    echo "ERROR: kubectl not found. Please install kubectl for your OS."
    exit 1
  fi
fi

wget -O ~/dind-cluster-v1.13.sh https://github.com/kubernetes-sigs/kubeadm-dind-cluster/releases/download/v0.1.0/dind-cluster-v1.13.sh
chmod +x ~/dind-cluster-v1.13.sh
~/dind-cluster-v1.13.sh up

