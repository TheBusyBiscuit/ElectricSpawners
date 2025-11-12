package io.github.thebusybiscuit.electricspawners;

import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerHead;
import io.github.thebusybiscuit.slimefun4.libraries.dough.skins.PlayerSkin;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.GitHubBuildsUpdater;

public class ElectricSpawners extends JavaPlugin implements Listener, SlimefunAddon {

    @Override
    public void onEnable() {
        Config cfg = new Config(this);

        // Setting up bStats
        new Metrics(this, 6163);

        if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
            new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ElectricSpawners/master").start();
        }

        boolean forceDisableAI = false;
        boolean defaultDisabledAI = false;

        if (cfg.contains("mob-ai.force-disable")) {
            forceDisableAI = cfg.getBoolean("mob-ai.force-disable");
        }
        if (cfg.contains("mob-ai.default-disabled")) {
            defaultDisabledAI = cfg.getBoolean("mob-ai.default-disabled");
        }

        ItemGroup itemGroup = new ItemGroup(new NamespacedKey(this, "electric_spawners"),
                new CustomItemStack(PlayerHead.getItemStack(PlayerSkin.fromHashCode("db6bd9727abb55d5415265789d4f2984781a343c68dcaf57f554a5e9aa1cd")),
                        "&9Electric Spawners"));
        Research research = new Research(new NamespacedKey(this, "electric_spawners"), 4820, "Powered Spawners", 30);

        for (String mob : cfg.getStringList("mobs")) {
            try {
                EntityType type = EntityType.valueOf(mob);
                new ElectricSpawner(itemGroup, mob, type, research, forceDisableAI, defaultDisabledAI).register(this);
            } catch (IllegalArgumentException x) {
                getLogger().log(Level.WARNING, "An Error has occurred while adding an Electric Spawner for the (possibly outdated or invalid) EntityType \"{0}\"", mob);
            } catch (Exception x) {
                getLogger().log(Level.SEVERE, x, () -> "An Error has occurred while adding an Electric Spawner for the EntityType \"" + mob + "\"");
            }
        }

        research.register();

        saveDefaultConfig();
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/TheBusyBiscuit/ElectricSpawners/issues";
    }
}