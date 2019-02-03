#!/bin/bash
logs_dir=/hbasedir/hbase-2.1.2/logs/

/hbasedir/hbase-2.1.2/bin/hbase thrift start > $logs_dir/hbase-thrift.log 2>&1 &
/hbasedir/hbase-2.1.2/bin/hbase rest start > $logs_dir/hbase-rest.log 2>&1 &


/hbasedir/hbase-2.1.2/bin/start-hbase.sh
tail -f /hbasedir/hbase-2.1.2/logs/*
