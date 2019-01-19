#!/bin/bash

/hbasedir/hbase-2.1.2/bin/start-hbase.sh
tail -f /hbasedir/hbase-2.1.2/logs/*
