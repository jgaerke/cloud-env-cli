#!/bin/bash

# Setup working vars
SERVER_IP_ADDRESS=$1
CONF_FILE="mongod.conf"

# Clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS

scp -oStrictHostKeyChecking=no backup.sh root@$SERVER_IP_ADDRESS:/root

# Run provisioning scripts
cat aws.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
cat mongo.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "service mongod stop"
scp -oStrictHostKeyChecking=no $CONF_FILE root@$SERVER_IP_ADDRESS:/etc/mongod.conf
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "service mongod restart"
cat cron.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
