FROM registry.cn-qingdao.aliyuncs.com/homecommunity/ubuntu-java17:1.0

MAINTAINER xxx <xxx@xx.com>

ENV MIN_MEM=-Xms256m
ENV MAX_MEM=-Xmx512m
ENV ACTIVE=dev

COPY springboot/target/iot-boot.jar /root/iot-boot.jar

RUN echo '#!/bin/bash\njava -jar -Dspring.profiles.active=$ACTIVE $MIN_MEM $MAX_MEM /root/iot-boot.jar' > /root/start_iot_jar.sh \
    && chmod u+x /root/start_iot_jar.sh

EXPOSE 9999

CMD ["/root/start_iot_jar.sh"]
