#!/bin/bash


# Setup working vars
SERVER_IP_ADDRESS=$1

# Clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS

# Run provisioning scripts
cat restore-latest.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash