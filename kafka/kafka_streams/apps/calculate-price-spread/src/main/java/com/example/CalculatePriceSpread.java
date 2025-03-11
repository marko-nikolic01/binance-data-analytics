package com.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Date;
import java.util.Properties;

public class CalculatePriceSpread {

    public static void main(String[] args) {
        // Kafka Streams Configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "calculate-price-spread");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Read binance data
        KStream<String, String> inputStream = builder.stream("transformed_data");

        // Calculate price spread grouped by symbol
        KStream<String, PriceSpread> priceRangeStream = inputStream
            .mapValues((ValueMapper<String, PriceSpread>) value -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    JsonNode jsonNode = objectMapper.readTree(value);
                    String symbol = jsonNode.has("symbol") ? jsonNode.get("symbol").asText() : null;
                    double high = jsonNode.has("high_price") ? jsonNode.get("high_price").asDouble() : 0;
                    double low = jsonNode.has("low_price") ? jsonNode.get("low_price").asDouble() : 0;
                    long startTime = jsonNode.has("start_time") ? jsonNode.get("start_time").asLong() : 0;

                    if (symbol == null || high == 0 || low == 0 || startTime == 0) return null;

                    return new PriceSpread(symbol, high, low, new Date(startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter((key, priceSpread) -> priceSpread != null);

        // Write to MongoDB
        MongoDBWriter mongoDBWriter = new MongoDBWriter();
        priceRangeStream.foreach((key, priceSpread) -> {
            mongoDBWriter.writeToMongo(priceSpread.getSymbol(), priceSpread.getLowPrice(), priceSpread.getHighPrice(), priceSpread.getTime());
        });

        // Start Kafka Streams
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
