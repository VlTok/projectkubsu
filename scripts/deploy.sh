#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/kubsu-key.pem \
    target/project.jar \
    ec2-user@ec2-54-93-53-237.eu-central-1.compute.amazonaws.com:/home/ec2-user/

echo 'Restart server...'

ssh -i ~/.ssh/kubsu-key.pem ec2-user@ec2-54-93-53-237.eu-central-1.compute.amazonaws.com << EOF
  pgrep java | xargs kill -9
  nohup java -jar project.jar > log.txt &
EOF

echo 'Bye'