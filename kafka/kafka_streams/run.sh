#!/bin/bash
sleep 45
java -jar /app/clean-binance-data-app-1.0-SNAPSHOT.jar &

sleep 15
java -jar /app/calculate-moving-average-1.0-SNAPSHOT.jar &
java -jar /app/calculate-trading-volume-by-hour-of-day-1.0-SNAPSHOT.jar

wait
