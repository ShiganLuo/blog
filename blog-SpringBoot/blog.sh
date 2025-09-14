#!/bin/bash
### docker启动mysql
# docker run \
#     --name blog \
#     --restart=always -p 3307:3306 \
#     -v /ChIP_seq_2/Data/mnt/blog/log:/var/log/mysql \
#     -v /ChIP_seq_2/Data/mnt/blog/data:/var/lib/mysql \
#     -v /ChIP_seq_2/Data/mnt/blog/conf:/etc/mysql/conf.d \
#     -e MYSQL_ROOT_PASSWORD=123456 \
#     -d mysql:8.0.33
###