package de.dosmike.sponge.minesweeper;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id="minesweeper", name="Minesweeper", version="0.2", authors={"DosMike"})
final public class Minesweeper {
    public static void main(String[] args) { System.err.println("This plugin can not be run as executable!"); }

    static Minesweeper instance = null;

    public Minesweeper() {
        instance = this;
    }

    public static Minesweeper getInstance() {
        return instance;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        CommandRegistra.registerCommands();
    }

}
