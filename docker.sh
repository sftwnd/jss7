#!/bin/bash
mvn clean
mkdir ./target
curl -fsSL -o ./target/dpklnx.Z https://www.dialogic.com/files/DSI/developmentpackages/linux/dpklnx.Z
curl -fsSL -o ./target/jmxtools-1.2.1.jar http://www.datanucleus.org/downloads/maven2/com/sun/jdmk/jmxtools/1.2.1/jmxtools-1.2.1.jar
cd target
tar --no-same-owner -zxvf dpklnx.Z ./JAVA/gctApi.jar
mvn install:install-file -DgroupId=com.vendor.dialogic -DartifactId=gctapi -Dversion=6.7.1 -Dpackaging=jar -Dfile=./JAVA/gctApi.jar
mvn install:install-file -DgroupId=com.sun.jdmk -DartifactId=jmxtools -Dversion=1.2.1 -Dpackaging=jar -Dfile=jmxtools-1.2.1.jar
cd ..
mvn clean
docker rmi centos-sctp-mvn-libericajdk8 2>/dev/null
docker build -t centos-sctp-mvn-libericajdk8 -f Dockerfile --no-cache .
docker run -it --rm centos-sctp-mvn-libericajdk8 java -version
