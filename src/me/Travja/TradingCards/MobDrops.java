package me.Travja.TradingCards;

import me.Travja.TradingCards.util.CardUtils;
import me.Travja.TradingCards.util.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.bukkit.entity.EntityType.*;

public class MobDrops implements Listener {
    public Main plugin;

    public MobDrops(Main m) {
        this.plugin = m;
    }

    private static Random r = new Random();

    @EventHandler
    public void eDeath(EntityDeathEvent event) {
        EntityType et = event.getEntityType();
        String name = et.toString();
        if (event.getEntity().getKiller() != null) {

        	World w = event.getEntity().getWorld();
        	Location loc = event.getEntity().getLocation();

            if (isBoss(et)) {
                if (r.nextInt(100) <= ConfigUtils.getInt("Drops.Bosses.Chance"))
                    w.dropItemNaturally(loc, CardUtils.getRandomMobCard(name, CardUtils.MobType.BOSS));
            } else if (isHostile(et) || event.getEntity() instanceof Monster) {
                if (r.nextInt(100) <= ConfigUtils.getInt("Drops.Hostile.Chance"))
                    w.dropItemNaturally(loc, CardUtils.getRandomMobCard(name, CardUtils.MobType.HOSTILE));

            } else if (isPassive(et)) {
                if (r.nextInt(100) <= ConfigUtils.getInt("Drops.Passive.Chance"))
                    w.dropItemNaturally(loc, CardUtils.getRandomMobCard(name, CardUtils.MobType.PASSIVE));

            } else if (isNeutral(et)) {
                if (r.nextInt(100) <= ConfigUtils.getInt("Drops.Neutral.Chance"))
                    w.dropItemNaturally(loc, CardUtils.getRandomMobCard(name, CardUtils.MobType.NEUTRAL));
            }

        }
    }


    ArrayList<EntityType> hostile = new ArrayList<>(Arrays.asList(
            BLAZE,
            CAVE_SPIDER,
            ENDERMAN,
            SPIDER,
            PIG_ZOMBIE,
            CREEPER,
            DROWNED,
            ELDER_GUARDIAN,
            ENDERMITE,
            EVOKER,
            GHAST,
            GUARDIAN,
            HUSK,
            MAGMA_CUBE,
            PHANTOM,
            PILLAGER,
            RAVAGER,
            SHULKER,
            SILVERFISH,
            SKELETON,
            SLIME,
            STRAY,
            VEX,
            VINDICATOR,
            WITCH,
            WITHER_SKELETON,
            ZOMBIE,
            ZOMBIE_VILLAGER
    ));
    ArrayList<EntityType> passive = new ArrayList<>(Arrays.asList(
            BAT,
            CAT,
            CHICKEN,
            COD,
            COW,
            DONKEY,
            FOX,
            HORSE,
            MUSHROOM_COW,
            MULE,
            OCELOT,
            PARROT,
            PIG,
            RABBIT,
            SALMON,
            SHEEP,
            SKELETON_HORSE,
            SQUID,
            TROPICAL_FISH,
            TURTLE,
            VILLAGER,
            WANDERING_TRADER,
            SNOWMAN
    ));
    ArrayList<EntityType> neutral = new ArrayList<>(Arrays.asList(
            PUFFERFISH,
            BEE,
            DOLPHIN,
            LLAMA,
            PANDA,
            POLAR_BEAR,
            TRADER_LLAMA,
            WOLF,
            IRON_GOLEM
    ));

    public boolean isHostile(EntityType et) {
        return hostile.contains(et);
    }

    public boolean isPassive(EntityType et) {
        return passive.contains(et);
    }

    public boolean isNeutral(EntityType et) {
        return isNeutral(et);
    }

    public boolean isBoss(EntityType et) {
        return et == ENDER_DRAGON || et == WITHER;
    }
}
