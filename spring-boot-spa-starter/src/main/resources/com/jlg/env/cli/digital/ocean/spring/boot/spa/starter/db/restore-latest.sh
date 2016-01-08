#!/bin/bash

S3_DB_BACKUP_FILE_NAME=$(aws s3 ls $S3_DB_BACKUP_BUCKET_NAME | tail -1 | awk '{print $4}')

# pull from s3
aws s3 cp s3://$S3_DB_BACKUP_BUCKET_NAME/$S3_DB_BACKUP_FILE_NAME $S3_DB_BACKUP_FILE_NAME

# restore to mongo
tar -xvf $S3_DB_BACKUP_FILE_NAME
mongo -u $DB_ADMIN_USER_NAME -p $DB_ADMIN_USER_PASSWORD --authenticationDatabase admin --eval "db.getSiblingDB(\"$DB_NAME\").dropDatabase()"
mongorestore -u $DB_ADMIN_USER_NAME -p $DB_ADMIN_USER_PASSWORD --authenticationDatabase admin

# cleanup
rm -rf dump
rm -rf $S3_DB_BACKUP_FILE_NAME



