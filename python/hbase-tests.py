import happybase

connection = happybase.Connection('hbase', autoconnect=False)

connection.open()

print(connection.tables())
