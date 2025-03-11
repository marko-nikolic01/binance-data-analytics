package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Date;
import java.util.Properties;

public class CalculateMovingAverage {

    public static void main(String[] args) {
        // Kafka Streams Configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "calculate-moving-averages");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Read binance data
        KStream<String, String> inputStream = builder.stream("transformed_data");

        // Define a 10-second sliding window
        final Duration WINDOW_SIZE = Duration.ofSeconds(10);

        // Calculate average prices over 10-second widnows grouped by symbol
        KTable<Windowed<String>, SumAndCount> movingAverageTable = inputStream
            .map((key, value) -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(value);
                    String symbol = jsonNode.has("symbol") ? jsonNode.get("symbol").asText() : null;
                    String price = jsonNode.has("close_price") ? jsonNode.get("close_price").asText() : null;

                    if (symbol == null || price == null) return null;

                    return new KeyValue<>(symbol, price);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter((key, value) -> key != null && value != null)
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE).advanceBy(WINDOW_SIZE))
            .aggregate(
                SumAndCount::new,
                (key, value, aggregate) -> {
                    aggregate.add(Double.parseDouble(value));
                    return aggregate;
                },
                Materialized.with(Serdes.String(), new SumAndCountSerde())
            );

        // Write to Mongo DB
        MongoDBWriter mongoDBWriter = new MongoDBWriter(); 
        movingAverageTable.toStream().foreach((windowedKey, value) -> {
            String symbol = windowedKey.key();
            Date windowStartTime = new Date(windowedKey.window().start());
            double movingAverage = value.getAverage();

            mongoDBWriter.writeToMongo(symbol, windowStartTime, movingAverage);
        });


        // Start Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
