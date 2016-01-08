#!/bin/bash

source /etc/environment

DATE=$(date +"%Y%m%d_%H%M%S")
TAR_BALL_NAME="$DB_NAME-${DATE}.tar.gz"

# backup mongo
mongodump -u $DB_ADMIN_USER_NAME -p $DB_ADMIN_USER_PASSWORD --authenticationDatabase admin -d $DB_NAME
tar -czf $TAR_BALL_NAME dump

# push to s3
/usr/local/bin/aws s3 cp $TAR_BALL_NAME s3://$AWS_S3_DB_BACKUP_BUCKET_NAME/$TAR_BALL_NAME

# clean up
rm dump -rf
rm $TAR_BALL_NAME -rf
