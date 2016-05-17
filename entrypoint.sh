#!/bin/bash

git clone https://github.com/FenixEdu/fenixedu-cms.git

cd fenixedu-cms/
mvn clean install

curl 'https://start.fenixedu.org/webapp.zip' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Connection: keep-alive' --data 'groupId=org.fenixedu&artifactId=fenixedu-cms&name=FenixEdu+CMS&mavenVersion=2.3.0&bennuVersion=4.0.0.RC2&dbHost=localhost&dbPort=3306&dbName=fenixedu-cms&dbUser=root&dbPassword=&generate=' --compressed -O webapp.zip

unzip webapp.zip

cd fenixedu-cms/

echo "dbAlias = //$DB_HOST:$DB_PORT/$DB_DATABASE
dbUsername = $DB_USER
dbPassword = $DB_PASS
updateRepositoryStructureIfNeeded = true" > src/main/resources/fenix-framework.properties

mvn clean tomcat7:run

echo $TEST_VAR
