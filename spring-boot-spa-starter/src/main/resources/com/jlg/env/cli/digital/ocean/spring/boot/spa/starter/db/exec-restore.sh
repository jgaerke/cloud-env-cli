#!/bin/bash


# Setup working vars
SERVER_IP_ADDRESS=$1
S3_DB_BACKUP_FILE_NAME=$2

# Clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS

# Run provisioning scripts
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "bash -s" < ./restore.sh $S3_DB_BACKUP_FILE_NAME
