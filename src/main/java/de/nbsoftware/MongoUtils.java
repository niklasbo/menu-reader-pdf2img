package de.nbsoftware;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

/**
 * Utils for interacting with MongoDB
 */
public class MongoUtils {

    public static boolean isWeeknumAlreadyInMongoDB(String connectionString, String databaseName, String collectionName,
            int weeknumToCheck) {
        MongoClient client = MongoClients.create(connectionString);

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("weeknum", weeknumToCheck);
        MongoCursor<Document> cursor = collection.find(searchQuery).cursor();

        while (cursor.hasNext()) {
            return true;
        }
        return false;
    }

    public static void writeToMongoDB(String connectionString, String databaseName, String collectionName, int weeknum,
            String imageAString) {
        MongoClient client = MongoClients.create(connectionString);

        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);

        Document doc = new Document("weeknum", weeknum).append("jpegImageAsBase64String", imageAString);
        collection.insertOne(doc);
    }
}
