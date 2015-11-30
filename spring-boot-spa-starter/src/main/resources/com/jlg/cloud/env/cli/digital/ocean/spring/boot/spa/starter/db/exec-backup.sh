#!/bin/bash

# Setup working vars
SERVER_IP_ADDRESS=$1

echo "HOST NAME: $SERVER_IP_ADDRESS"

# Clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS

# Run provisioning scripts
cat backup.sh | ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS /bin/bash
