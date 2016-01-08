#!/bin/bash

CONF_FILE="/etc/nginx/conf.d/rp.conf"

service nginx stop
sleep 3
sed -i "s/server ONE;/server ${SERVER_DEFAULT_APP_1_IP_ADDRESS}:8080;/g" $CONF_FILE
sed -i "s/server TWO;/server ${SERVER_DEFAULT_APP_2_IP_ADDRESS}:8080;/g" $CONF_FILE
rm /etc/nginx/sites-enabled/default
service nginx start
