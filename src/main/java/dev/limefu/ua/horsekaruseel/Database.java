package dev.limefu.ua.horsekaruseel;



        import com.mongodb.MongoClientSettings;
        import com.mongodb.MongoCredential;
        import com.mongodb.ServerAddress;
        import com.mongodb.reactivestreams.client.MongoClient;
        import com.mongodb.reactivestreams.client.MongoClients;
        import com.mongodb.reactivestreams.client.MongoCollection;
        import com.mongodb.reactivestreams.client.MongoDatabase;

        import org.bson.Document;
        import java.util.Collections;

public class Database {
    private final MongoClient mongoClient;
    private final MongoDatabase database;


    public Database(String host, int port, String databaseName, String username, String password) {
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential credential = MongoCredential.createCredential(username, databaseName, password.toCharArray());
        MongoClientSettings settings = MongoClientSettings.builder()
                .credential(credential)
                .applyToClusterSettings(builder -> builder.hosts(Collections.singletonList(serverAddress)))
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase(databaseName);
    }

    public void insertPlayer(String playerName, String horseName, String startTime, String endTime) {
        MongoCollection<Document> collection = database.getCollection("players");
        Document document = new Document()
                .append("playerName", playerName)
                .append("horseName", horseName)
                .append("startTime", startTime)
                .append("endTime", endTime);
        collection.insertOne(document);
    }

    public void close() {
        mongoClient.close();
    }
}

