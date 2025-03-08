FROM apache/nifi:1.15.3

USER root

RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    apt-get clean

RUN pip3 install websocket-client

WORKDIR /opt/nifi/nifi-current

COPY ./nifi/scripts /scripts

EXPOSE 8443

USER nifi

CMD ["/bin/sh", "-c", "python3 /scripts/connect_to_binance.py & exec ./bin/nifi.sh run"]