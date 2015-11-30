#!/bin/bash

mongo admin --eval "db.dropUser(\"sa\")"
mongo admin --eval "db.getSiblingDB(\"$DB_NAME\").dropUser(\"$DB_USER_NAME\")"
mongo admin --eval "db.createUser({ user: \"$DB_ADMIN_USER_NAME\", pwd: \"$DB_ADMIN_USER_PASSWORD\", roles: [ { role: \"root\", db: \"admin\" } ] } )"
mongo admin --eval "db.getSiblingDB(\"$DB_NAME\").createUser({ user: \"$DB_USER_NAME\", pwd: \"$DB_USER_PASSWORD\", roles: [ { role: \"readWrite\", db: \"$DB_NAME\" } ] })"
