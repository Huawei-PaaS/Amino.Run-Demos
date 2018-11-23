#!/bin/sh -x

#TODO getopt

source /root/dcap/common.inc


# TODO: getopt / usage
OMS_PORT=32557
get_node_ip
OMS_IP=$node_ip
SERVICE_PORT=32555

if [ -z $OMS_IP ]; then
  echo "ERROR: Failed to get container IP address"
  exit 1
fi

echo "Starting OMS for app $OMS_APP_MAIN_CLASS, listening on $OMS_IP:$OMS_PORT .."
java -cp "/root/dcap/jars/*" sapphire.oms.OMSServerImpl $OMS_IP $OMS_PORT --servicePort=$SERVICE_PORT
