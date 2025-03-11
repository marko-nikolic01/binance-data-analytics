FROM openjdk:17

WORKDIR /app

COPY ./apps/clean-binance-data-app/target/clean-binance-data-app-1.0-SNAPSHOT.jar /app/
COPY ./apps/calculate-moving-average/target/calculate-moving-average-1.0-SNAPSHOT.jar /app/
COPY ./apps/calculate-trading-volume-by-hour-of-day/target/calculate-trading-volume-by-hour-of-day-1.0-SNAPSHOT.jar /app/
COPY ./apps/calculate-price-spread/target/calculate-price-spread-1.0-SNAPSHOT.jar /app/

COPY run.sh /app/
RUN chmod +x /app/run.sh

CMD ["/app/run.sh"]
