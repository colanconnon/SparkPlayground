FROM continuumio/miniconda3


ENV APACHE_SPARK_VERSION 2.4.0
ENV HADOOP_VERSION 2.7

RUN apt-get -y update && \
    apt-get install --no-install-recommends -y openjdk-8-jdk ca-certificates-java wget curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN cd /tmp && \
        wget -q http://mirrors.ukfast.co.uk/sites/ftp.apache.org/spark/spark-${APACHE_SPARK_VERSION}/spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz && \
        tar xzf spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz -C /usr/local --owner root --group root --no-same-owner && \
        rm spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

RUN cd /usr/local && ln -s spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} spark

ENV SPARK_HOME /usr/local/spark
ENV PYTHONPATH $SPARK_HOME/python:$SPARK_HOME/python/lib/py4j-0.10.7-src.zip
ENV SPARK_OPTS --driver-java-options=-Xms1024M --driver-java-options=-Xmx4096M --driver-java-options=-Dlog4j.logLevel=info

RUN conda install --quiet -y 'pyarrow' && \
    conda install --quiet -y 'pyspark'

WORKDIR /src