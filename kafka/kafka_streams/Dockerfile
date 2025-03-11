FROM openjdk:17

WORKDIR /app

# Copy the JAR file into the /app directory
COPY ./apps/clean-binance-data-app/target/clean-binance-data-app-1.0-SNAPSHOT.jar /app/
COPY ./apps/calculate-moving-average/target/calculate-moving-average-1.0-SNAPSHOT.jar /app/

# Copy the run.sh script
COPY run.sh /app/
RUN chmod +x /app/run.sh

# Run the script
CMD ["/app/run.sh"]
