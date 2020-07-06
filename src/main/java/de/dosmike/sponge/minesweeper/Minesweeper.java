package de.dosmike.sponge.minesweeper;

import com.google.inject.Inject;
import de.dosmike.sponge.VersionChecker;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id="minesweeper", name="Minesweeper", version="0.4", authors={"DosMike"})
final public class Minesweeper {
    public static void main(String[] args) { System.err.println("This plugin can not be run as executable!"); }

    private static Minesweeper instance = null;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    static Configuration configuration = null;

    public Minesweeper() {
        instance = this;
    }

    public static Minesweeper getInstance() {
        return instance;
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    @Listener
    public void onServerStart(GameInitializationEvent event) {
        configuration = new Configuration(configManager);
        CommandRegistra.registerCommands();
        VersionChecker.checkVersion(instance);
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        configuration = new Configuration(configManager);
    }

}
