#!/bin/bash

#setup variables
SERVER_IP_ADDRESS=$1

#clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS


#run provisioning scripts
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "mkdir app"
cat haveaged.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
cat java.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
cat git.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
