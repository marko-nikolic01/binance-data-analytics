package com.example;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class TradingVolumeSerde extends Serdes.WrapperSerde<TradingVolume> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TradingVolumeSerde() {
        super(new TradingVolumeSerializer(), new TradingVolumeDeserializer());
    }

    public static class TradingVolumeSerializer implements Serializer<TradingVolume> {
        @Override
        public byte[] serialize(String topic, TradingVolume data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (IOException e) {
                throw new RuntimeException("Error serializing TradingVolume", e);
            }
        }
    }

    public static class TradingVolumeDeserializer implements Deserializer<TradingVolume> {
        @Override
        public TradingVolume deserialize(String topic, byte[] data) {
            try {
                return objectMapper.readValue(data, TradingVolume.class);
            } catch (IOException e) {
                throw new RuntimeException("Error deserializing TradingVolume", e);
            }
        }
    }
}
