FROM ubuntu:16.04


WORKDIR /kafka

RUN cd /kafka

RUN apt-get -y update && \
    apt-get install --no-install-recommends -y openjdk-8-jdk ca-certificates-java wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN wget http://apache.claz.org/kafka/2.1.0/kafka_2.11-2.1.0.tgz
RUN tar xzvf kafka_2.11-2.1.0.tgz

COPY zookeeper.properties ./kafka_2.11-2.1.0/config/zookeeper.properties

CMD ["/kafka/kafka_2.11-2.1.0/bin/zookeeper-server-start.sh", "/kafka/kafka_2.11-2.1.0/config/zookeeper.properties"]