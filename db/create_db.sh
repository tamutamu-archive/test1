#!/bin/bash -eu

## lxc restore ubuntu16 init_db

mysql -uroot -p -h ubuntu16.lxd < ./create_db.sql
mysql -ustock_user -ppassword -h ubuntu16.lxd stock_db < ./ddl.sql
mysql -ustock_user -ppassword -h ubuntu16.lxd stock_db < ./master_data.sql
