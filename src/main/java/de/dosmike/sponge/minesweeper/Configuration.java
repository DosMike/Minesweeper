package de.dosmike.sponge.minesweeper;

import de.dosmike.sponge.VersionChecker;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;

import java.io.IOException;

public class Configuration {

    private boolean broadcastVictory = true;
    public boolean shouldBroadcastVictory() {
        return broadcastVictory;
    }

    public Configuration(ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            CommentedConfigurationNode root = configManager.load();
            if (root.getNode("BroadcastVictory").isVirtual()) {
                HoconConfigurationLoader defaultManager = HoconConfigurationLoader.builder()
                        .setURL(Sponge.getAssetManager().getAsset(Minesweeper.getInstance(), "default.conf").get().getUrl())
                        .build();
                CommentedConfigurationNode defaults = defaultManager.load();
                root.mergeValuesFrom(defaults);
                configManager.save(root);
            }

            broadcastVictory = root.getNode("BroadcastVictory").getBoolean(true);
            VersionChecker.setVersionCheckingEnabled(
                    Sponge.getPluginManager().fromInstance(Minesweeper.getInstance()).get().getId(),
                    root.getNode("VersionChecking").getBoolean(false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}