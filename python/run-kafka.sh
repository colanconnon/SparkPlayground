spark-submit \
--packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0,org.apache.hbase:hbase-client:2.1.2,org.apache.hbase:hbase:2.1.2 \
--jars sqljdbc42.jar,shc-core-1.1.3-2.4-s_2.11.jar \
--repositories https://repository.apache.org/content/repositories/releases \
streaming.py