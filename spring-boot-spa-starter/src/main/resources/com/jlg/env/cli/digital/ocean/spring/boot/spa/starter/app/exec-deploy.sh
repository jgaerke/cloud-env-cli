#!/bin/bash


#setup variables
SERVER_IP_ADDRESS=$1
GIT_REPO_URL=$2
GIT_REPO_NAME=$3
JAR_DIR=$4
JAR=$5

echo "Parameters supplied:"
echo "SERVER = $SERVER_IP_ADDRESS"
echo "GIT_REPO_URL = $GIT_REPO_URL"
echo "GIT_REPO_NAME = $GIT_REPO_NAME"
echo "JAR_DIR = $JAR_DIR"
echo "JAR = $JAR"

ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "bash -s" < ./deploy.sh $SERVER_IP_ADDRESS $GIT_REPO_URL $GIT_REPO_NAME $JAR_DIR $JAR

