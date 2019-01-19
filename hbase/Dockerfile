FROM ubuntu:16.04


WORKDIR /hbasedir

RUN cd /hbasedir

RUN apt-get -y update && \
    apt-get install --no-install-recommends -y openjdk-8-jdk ca-certificates-java wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN wget https://www.apache.org/dist/hbase/2.1.2/hbase-2.1.2-bin.tar.gz
RUN ls
RUN tar xzvf hbase-2.1.2-bin.tar.gz

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64//
COPY ./site.xml ./hbase-2.1.2/conf/site.xml
COPY start-hbase.sh ./start-hbase.sh
RUN chmod +x start-hbase.sh
CMD ["./start-hbase.sh"]
