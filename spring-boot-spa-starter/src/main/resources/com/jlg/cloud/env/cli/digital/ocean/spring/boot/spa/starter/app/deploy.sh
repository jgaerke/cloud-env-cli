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

#clear keygen history
ssh-keygen -R $SERVER_IP_ADDRESS

#run deployment scripts
echo 'starting deployment'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "killall -9 java"
echo 'killed all java processes'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "rm /root/app/* -rf"
echo 'cleared app dir'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "rm /root/$GIT_REPO_NAME -rf && git clone $GIT_REPO_URL"
echo 'repo downloaded!'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "cd /root/$GIT_REPO_NAME && ./gradlew clean assemble"
echo 'assembled jar'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "mv /root/$GIT_REPO_NAME/$JAR_DIR/$JAR /root/app"
echo 'moved jar to run time directory'
ssh -oStrictHostKeyChecking=no -t root@$SERVER_IP_ADDRESS "rm /root/$GIT_REPO_NAME -rf"
echo 'removed repo'
ssh -oStrictHostKeyChecking=no root@$SERVER_IP_ADDRESS "java -jar /root/app/$JAR -Dmongo.host=$SERVER_DB_IP_ADDRESS -Dmongo.port=$DB_PORT -Dmongo.database=$DB_NAME -Dmongo.user=$DB_USER_NAME -Dmongo.password=$DB_USER_PASSWORD  > startup.log &"
echo 'started jar'
