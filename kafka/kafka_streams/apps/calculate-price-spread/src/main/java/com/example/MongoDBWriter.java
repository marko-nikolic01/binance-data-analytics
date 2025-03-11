package com.example;

import java.util.Date;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoDBWriter {

    private static final String MONGO_URI = "mongodb://mongodb:27017";
    private static final String DATABASE_NAME = "binance";
    private static final String COLLECTION_NAME = "price_spread";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBWriter() {
        this.mongoClient = MongoClients.create(MONGO_URI);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public void writeToMongo(String symbol, double lowPrice, double highPrice, Date windowStartTime) {
        Document doc = new Document()
                .append("symbol", symbol)
                .append("low_price", lowPrice)
                .append("high_price", highPrice)
                .append("time", windowStartTime);

        collection.insertOne(doc);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
