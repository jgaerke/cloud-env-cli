#!/bin/bash

#install java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
sudo apt-get install oracle-java8-installer -y

