package dev.limefu.ua.horsekaruseel;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HorseKaruseel extends JavaPlugin {

    @Getter
    HorseKaruseel instance;
    private HorseCreate horseCreate;
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        Bukkit.getPluginManager().registerEvents(new HorseListener(), this);

        Location center = new Location(getServer().getWorld("world"), 0, 64, 0);
        horseCreate = new HorseCreate(center, this);
        horseCreate.loadConfig(config);
        connectToDatabase();


    }
    private void connectToDatabase() {

        String host = getConfig().getString("mongodb.host");
        int port = getConfig().getInt("mongodb.port");
        String databaseName = getConfig().getString("mongodb.database");
        String collectionName = getConfig().getString("mongodb.collection");


        mongoClient = new MongoClient(host, port);


        database = mongoClient.getDatabase(databaseName);


        collection = database.getCollection(collectionName);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }
}
