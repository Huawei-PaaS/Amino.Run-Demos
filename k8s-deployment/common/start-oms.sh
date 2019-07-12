#!/bin/sh -xe

source /root/amino/common.inc

# TODO: getopt / usage
RMI_PORT=$1
SVC_PORT=$2
get_node_ip
OMS_IP=$node_ip


if [ -z $OMS_IP ]; then
  echo "ERROR: Failed to get container IP address"
  exit 1
fi

echo "Starting OMS for app $OMS_APP_MAIN_CLASS, listening on $OMS_IP:$RMI_PORT .."
java -cp "/root/amino/jars/*" amino.run.oms.OMSServerImpl --oms-ip $OMS_IP --oms-port $RMI_PORT \
        --service-port $SVC_PORT
