#!/bin/bash

#setup backup cron job
echo "" > cron_job
crontab cron_job
echo "* * * * * bash -l -c '/root/backup.sh >> /var/log/db-backup-job.log 2>&1'" > cron_job
crontab cron_job
rm cron_job