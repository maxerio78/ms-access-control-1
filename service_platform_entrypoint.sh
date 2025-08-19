#!/bin/sh

APP_ENV=`cat /etc/sunseries.ini  | grep ENV_TYPE | awk -F'=' '{ print $2 }'`
java -Xmx1024m -Dspring.profiles.active=$APP_ENV -Dapp.config.file="/etc/sunseries.ini" -jar target/ms-access-control-*.jar
