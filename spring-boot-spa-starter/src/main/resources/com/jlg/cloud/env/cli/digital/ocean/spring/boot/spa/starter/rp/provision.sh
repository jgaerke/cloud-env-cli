#!/bin/bash

#setup variables
SERVER_IP_ADDRESS=$1
RP_CONF='rp.conf'

ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "apt-get update"
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "apt-get install nginx -y"
scp $RP_CONF root@$SERVER_IP_ADDRESS:/etc/nginx/conf.d/$RP_CONF
cat nginx.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash


