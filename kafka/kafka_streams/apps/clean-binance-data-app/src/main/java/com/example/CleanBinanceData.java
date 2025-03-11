package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CleanBinanceData {
    public static void main(String[] args) {
        // Kafka Streams Configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "clean-binance-data-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();

        // Read from the raw_data topic
        KStream<String, String> rawStream = builder.stream("raw_data");

        ObjectMapper objectMapper = new ObjectMapper();

        // List of fields to remove
        List<String> fieldsToRemove = Arrays.asList("i", "x", "q", "Q");

        // Map of renamed fields
        Map<String, String> fieldRenameMap = Map.ofEntries(
            Map.entry("e", "event"),
            Map.entry("E", "event_time"),
            Map.entry("s", "symbol"),
            Map.entry("t", "start_time"),
            Map.entry("T", "end_time"),
            Map.entry("f", "first_trade_id"),
            Map.entry("L", "last_trade_id"),
            Map.entry("o", "open_price"),
            Map.entry("c", "close_price"),
            Map.entry("h", "high_price"),
            Map.entry("l", "low_price"),
            Map.entry("v", "volume"),
            Map.entry("n", "number_of_trades"),
            Map.entry("V", "buy_base_asset_volume"),
            Map.entry("B", "best_bid")
        );

        // Remove unwanted fields and rename fields
        KStream<String, String> transformedStream = rawStream.mapValues((ValueMapper<String, String>) value -> {
            try {
                JsonNode rootNode = objectMapper.readTree(value);
                JsonNode dataNode = rootNode.path("data");
                JsonNode kNode = dataNode.path("k");

                ObjectNode newKNode = objectMapper.createObjectNode();

                newKNode.set("event_time", dataNode.get("E"));

                kNode.fieldNames().forEachRemaining(field -> {
                    if (!fieldsToRemove.contains(field)) {
                        String newFieldName = fieldRenameMap.getOrDefault(field, field);
                        newKNode.set(newFieldName, kNode.get(field));
                    }
                });

                return newKNode.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return value;
            }
        });

        // Write the transformed data to the transformed_data topic
        transformedStream.to("transformed_data", Produced.with(Serdes.String(), Serdes.String()));

        // Start the Kafka Streams application
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}
