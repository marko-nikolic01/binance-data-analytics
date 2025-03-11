package com.example;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SumAndCountSerde extends Serdes.WrapperSerde<SumAndCount> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public SumAndCountSerde() {
        super(new SumAndCountSerializer(), new SumAndCountDeserializer());
    }

    public static class SumAndCountSerializer implements Serializer<SumAndCount> {

        @Override
        public byte[] serialize(String topic, SumAndCount data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (IOException e) {
                throw new RuntimeException("Error serializing SumAndCount object", e);
            }
        }
    }

    public static class SumAndCountDeserializer implements Deserializer<SumAndCount> {

        @Override
        public SumAndCount deserialize(String topic, byte[] data) {
            try {
                return objectMapper.readValue(data, SumAndCount.class);
            } catch (IOException e) {
                throw new RuntimeException("Error deserializing SumAndCount object", e);
            }
        }
    }
}
