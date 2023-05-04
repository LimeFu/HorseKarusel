package dev.limefu.ua.horsekaruseel;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;

public class HorseListener implements Listener {
    Map<Horse, HorseRider> horseRiders = new HashMap<>();

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {
        Entity entity = event.getDismounted();
        if (entity instanceof Horse) {
            Horse horse = (Horse) entity;
            Entity passenger = horse.getPassenger();
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                long endTime = System.currentTimeMillis();

                try (MongoClient mongoClient = new MongoClient(HorseKaruseel.getPlugin(HorseKaruseel.class).getConfig().getString("mongo.host"), HorseKaruseel.getPlugin(HorseKaruseel.class).getConfig().getInt("mongo.port"))) {
                    MongoDatabase database = mongoClient.getDatabase(HorseKaruseel.getPlugin(HorseKaruseel.class).getConfig().getString("mongo.database"));
                    MongoCollection<Document> collection = database.getCollection("horse_riders");


                    Document document = new Document("player", player.getName())
                            .append("start_time", horseRiders.get(horse).startTime)
                            .append("end_time", endTime);


                    collection.insertOne(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                horseRiders.remove(horse);
            }
        }
    }
}
