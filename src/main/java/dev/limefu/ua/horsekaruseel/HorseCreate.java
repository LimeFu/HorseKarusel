package dev.limefu.ua.horsekaruseel;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HorseCreate implements Listener {
    private final Location center;
    private final JavaPlugin plugin;
    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;

    public HorseCreate(Location center, JavaPlugin plugin) {
        this.center = center;
        this.plugin = plugin;
    }


    public void loadConfig(FileConfiguration config) {

        int horseCount = config.getInt("horse_count", 1);
        double speed = config.getDouble("speed", 0.2);
        int radius = config.getInt("radius", 10);

        for (int i = 0; i < horseCount; i++) {
            EntityType entityType = EntityType.HORSE;
            int typeChance = new Random().nextInt(3);
            if (typeChance == 1) {
                entityType = EntityType.LLAMA;
            } else if (typeChance == 2) {
                entityType = EntityType.MULE;
            }

            Horse horse = (Horse) center.getWorld().spawnEntity(getRandomLocation(radius), entityType);
            if (horse instanceof Llama) {
                ((Llama) horse).setColor(Llama.Color.BROWN);
            } else if (horse instanceof Horse) {
                ((Horse) horse).setVariant(Horse.Variant.HORSE);
                ((Horse) horse).setColor(Horse.Color.CHESTNUT);
                ((Horse) horse).setStyle(Horse.Style.BLACK_DOTS);
                ((Horse) horse).setJumpStrength(1.0);
                ((Horse) horse).setAdult();
                ((Horse) horse).setTamed(true);
            }
            Entity leash = center.getWorld().spawnEntity(horse.getLocation(), EntityType.LEASH_HITCH);
            horse.setLeashHolder(leash);
            new HorseTask(horse, speed).runTaskTimer(plugin, 0, 1);
        }
    }

    private Location getRandomLocation(int radius) {
        Random rand = new Random();
        double angle = rand.nextDouble() * 2 * Math.PI;
        double x = center.getX() + radius * Math.cos(angle);
        double z = center.getZ() + radius * Math.sin(angle);
        Location loc = new Location(center.getWorld(), x, center.getY(), z);
        loc.setY(center.getWorld().getHighestBlockYAt(loc));
        return loc;
    }

    private static class HorseTask extends BukkitRunnable {
        private final double speed;
        private final Horse horse;
        private int direction = 1;
        private int height = 0;

        public HorseTask(Horse horse, double speed) {
            this.horse = horse;
            this.speed = speed;
        }

        @Override
        public void run() {
            Location loc = horse.getLocation();
            loc.add(loc.getDirection().multiply(speed * direction));
            loc.setY(loc.getWorld().getHighestBlockYAt(loc));
            height += direction;
            if (height > 5 || height < -5) {
                direction *= -1;
            }
            loc.setY(loc.getY() + height * 0.1);

            horse.teleport(loc);
        }
    }


}
