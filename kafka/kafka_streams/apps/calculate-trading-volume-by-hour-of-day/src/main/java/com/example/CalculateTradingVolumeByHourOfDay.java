package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Date;
import java.util.Properties;

public class CalculateTradingVolumeByHourOfDay {

    public static void main(String[] args) {
        // Kafka Streams Configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "trading-volume-minute");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Read Binance data from Kafka
        KStream<String, String> inputStream = builder.stream("transformed_data");

        // Define a 1-minute sliding window
        final Duration WINDOW_SIZE = Duration.ofMinutes(1);

        KTable<Windowed<String>, TradingVolume> tradingVolumeTable = inputStream
            .map((key, value) -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(value);
                    String symbol = jsonNode.has("symbol") ? jsonNode.get("symbol").asText() : null;
                    String volume = jsonNode.has("volume") ? jsonNode.get("volume").asText() : null;
        
                    if (symbol == null || volume == null) return null;
        
                    return new KeyValue<>(symbol, volume);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter((key, value) -> key != null && value != null)
            .groupByKey()
            .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))
            .aggregate(
                TradingVolume::new,
                (key, value, aggregate) -> {
                    aggregate.add(Double.parseDouble(value));
                    return aggregate;
                },
                Materialized.with(Serdes.String(), new TradingVolumeSerde())
            );

        // Write to MongoDB
        MongoDBWriter mongoDBWriter = new MongoDBWriter();
        tradingVolumeTable.toStream().foreach((windowedKey, totalVolume) -> {
            String symbol = windowedKey.key();
            Date windowStartTime = new Date(windowedKey.window().start());

            mongoDBWriter.writeToMongo(symbol, windowStartTime, totalVolume.getTotalVolume());
        });

        // Start Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
