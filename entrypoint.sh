#!/bin/bash

git clone https://github.com/FenixEdu/fenixedu-cms.git

cd fenixedu-cms/
mvn clean install

curl 'https://start.fenixedu.org/webapp.zip' -H 'Content-Type: application/x-www-form-urlencoded' -H 'Connection: keep-alive' --data 'groupId=org.fenixedu&artifactId=fenixedu-cms&name=FenixEdu+CMS&mavenVersion=2.3.0&bennuVersion=4.0.0.RC2&dbHost=localhost&dbPort=3306&dbName=fenixedu-cms&dbUser=root&dbPassword=&generate=' --compressed -O webapp.zip

unzip webapp.zip
VERSION=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version|grep -Ev '(^\[|Download\w+:)')

cd fenixedu-cms/

echo "dbAlias = //$DB_HOST:$DB_PORT/$DB_DATABASE
dbUsername = $DB_USER
dbPassword = $DB_PASS
updateRepositoryStructureIfNeeded = true" > src/main/resources/fenix-framework.properties

echo "application.url = $APP_URL
cas.enabled = false
cas.serverUrl =
cas.serviceUrl =
# Default email for support. This is intended to be the fall-back for when no application specific email is configured.
default.support.email.address =
# Whether development mode is on. Throughout the application the behaviour can change according to this setting.
development.mode = true
# Default System Locale. If empty falls back to java system default. Must be included in locales.supported
locale.default =  pt-PT
# Locales that should be supported in ResourceBundles and other I18N mechanisms. If not specified falls back to a list with only the java system default.
locales.supported = pt-PT,en-GB
# the size threshold after which files will be written to disk
multipart.fileSizeThreshold = 67108864
# maximum size allowed for uploaded files
multipart.maxFileSize = 2147483648
# maximum size allowed for multipart/form-data requests" > src/main/resources/configuration.properties

sed "s/<dependencies>/<dependencies><dependency><groupId>org.fenixedu<\/groupId><artifactId>fenixedu-cms<\/artifactId><version>$(echo $VERSION)<\/version><\/dependency>/g" -i pom.xml
mvn clean tomcat7:run