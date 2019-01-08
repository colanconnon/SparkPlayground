FROM continuumio/miniconda3


ENV APACHE_SPARK_VERSION 2.3.1
ENV HADOOP_VERSION 2.7

RUN apt-get -y update && \
    apt-get install --no-install-recommends -y openjdk-8-jre-headless ca-certificates-java && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN cd /tmp && \
        wget -q http://mirrors.ukfast.co.uk/sites/ftp.apache.org/spark/spark-${APACHE_SPARK_VERSION}/spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz && \
        echo "DC3A97F3D99791D363E4F70A622B84D6E313BD852F6FDBC777D31EAB44CBC112CEEAA20F7BF835492FB654F48AE57E9969F93D3B0E6EC92076D1C5E1B40B4696 *spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz" | sha512sum -c - && \
        tar xzf spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz -C /usr/local --owner root --group root --no-same-owner && \
        rm spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

RUN cd /usr/local && ln -s spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} spark

ENV SPARK_HOME /usr/local/spark
ENV PYTHONPATH $SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.7-src.zip
ENV SPARK_OPTS --driver-java-options=-Xms1024M --driver-java-options=-Xmx4096M --driver-java-options=-Dlog4j.logLevel=info


RUN conda install --quiet -y 'pyarrow' && \
    conda install --quiet -y 'pyspark'

RUN mkdir -p /src/livy
WORKDIR /src/livy
RUN wget https://www-us.apache.org/dist/incubator/livy/0.5.0-incubating/livy-0.5.0-incubating-bin.zip
RUN apt-get update && apt-get install unzip
RUN unzip livy-0.5.0-incubating-bin.zip
RUN apt-get update && apt-get install -y procps
ENV HADOOP_CONF_DIR /etc/hadoop/conf

COPY ./livy.conf /src/livy/livy-0.5.0-incubating-bin/conf/


SHELL ["/bin/bash", "-c"]
RUN mkdir -p /src/livy/livy-0.5.0-incubating-bin/logs
CMD ["/src/livy/livy-0.5.0-incubating-bin/bin/livy-server"]
