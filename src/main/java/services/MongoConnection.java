package services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Level;
import java.util.logging.Logger;

class MongoConnection {
    MongoCollection<Document> getCollection(String collectionName){
        return getConnection().getCollection(collectionName);
    }

    private MongoDatabase getConnection(){
        defineMongoLoggerLevel();
        MongoClient mongoClient = MongoClients.create("mongodb+srv://user:user@clustergereco-rhvgo.mongodb.net");
        return mongoClient.getDatabase("gereco");
    }

    private void defineMongoLoggerLevel(){
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
    }
}
