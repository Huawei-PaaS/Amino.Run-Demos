#!/bin/sh -xe

source /root/amino/common.inc

get_node_ip
KERNELSERVER_IP=$node_ip

OMS_SERVICE=$1
OMS_PORT=$2
RMI_PORT=$3
KERNELSERVER_PORT=$4

get_service_ip $OMS_SERVICE
OMS_IP=$service_ip


if [ -z $KERNELSERVER_IP ]; then
  echo "ERROR: Failed to get container IP address"
  exit 1
fi
if [ -z $OMS_IP ]; then
  echo "ERROR: Failed to get OMS service IP address"
  exit 1
fi

echo "Starting KernelServer on $KERNELSERVER_IP:$KERNELSERVER_PORT, connecting to $OMS_SERVICE at $OMS_IP:$OMS_PORT .."
java -cp "/root/amino/jars/*" -Djava.rmi.server.useCodebaseOnly=false \
        -Djava.security.policy=/root/amino/client.policy amino.run.kernel.server.KernelServerImpl \
        --kernel-server-ip $KERNELSERVER_IP --kernel-server-port $KERNELSERVER_PORT \
        --oms-ip $OMS_IP --oms-port $OMS_PORT --service-port $RMI_PORT
