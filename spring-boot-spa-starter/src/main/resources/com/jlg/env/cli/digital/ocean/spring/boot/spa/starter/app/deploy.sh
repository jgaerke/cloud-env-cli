#!/bin/bash

#setup variables
SERVER_IP_ADDRESS=$1
GIT_REPO_URL=$2
GIT_REPO_NAME=$3
JAR_DIR=$4
JAR=$5

killall -9 java
rm /root/app -rf
mkdir /root/app
rm /root/$GIT_REPO_NAME -rf
git clone $GIT_REPO_URL
cd /root/$GIT_REPO_NAME
./gradlew clean assemble
mv /root/$GIT_REPO_NAME/$JAR_DIR/$JAR /root/app
rm /root/$GIT_REPO_NAME -rf
cd /root/app
nohup java -jar $JAR --spring.profiles.active=production > startup.log &