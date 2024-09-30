FROM bellsoft/liberica-openjdk-centos:8u-cds
USER root
ARG MAVEN_VERSION=3.6.3
ARG USER_HOME_DIR="/root"
ARG MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN sed -i s/mirror.centos.org/vault.centos.org/g /etc/yum.repos.d/*.repo \
 && sed -i s/^#.*baseurl=http/baseurl=https/g /etc/yum.repos.d/*.repo \
 && sed -i s/^mirrorlist=http/#mirrorlist=https/g /etc/yum.repos.d/*.repo \
 && echo "sslverify=false" >> /etc/yum.conf \
 && yum -y install lksctp-tools jdocbook asciidoc \
 && yum clean all \
 && mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_URL} \
 && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV M2_HOME ${USER_HOME_DIR}/.m2
ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
EXPOSE 80 8080 8380 5005
WORKDIR /src
