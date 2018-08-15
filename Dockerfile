FROM openjdk:8-jre
MAINTAINER Jeff Risberg <j.s.risberg@gmail.com>

RUN mkdir -p /opt/seq01/bin \
             /opt/seq01/lib \
             /opt/seq01/www \
             /var/log/

ENV PATH /opt/seq01/bin:$PATH

WORKDIR /opt/seq01/bin

COPY build/libs/seq01-shadow-0.1.0.jar /opt/seq01/lib/
COPY src/main/resources /opt/seq01/conf/
COPY bin/start-server /opt/seq01/bin/

RUN chmod a+x /opt/seq01/bin/start-server

EXPOSE 8080
ENTRYPOINT start-server