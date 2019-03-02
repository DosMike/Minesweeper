package de.dosmike.sponge.minesweeper;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;

public abstract class MinesweeperGameEvent implements TargetPlayerEvent, Event {

    private Player player;
    private int mines, playtime;

    public MinesweeperGameEvent(Player player, int mines, int playtime) {
        this.player = player;
        this.mines = mines;
        this.playtime = playtime;
    }

    @Override
    public Player getTargetEntity() {
        return null;
    }

    /** @return the amount of mines that were placed in this game */
    public int getMines() {
        return mines;
    }

    /** @return the amount of seconds this game of minesweeper lasted */
    public int getPlaytime() {
        return playtime;
    }

    @Override
    public Cause getCause() {
        return Sponge.getCauseStackManager().getCurrentCause();
    }

    @Override
    public Object getSource() {
        return Sponge.getCauseStackManager().getCurrentCause().root();
    }

    @Override
    public EventContext getContext() {
        return Sponge.getCauseStackManager().getCurrentContext();
    }

    public static class Victory extends MinesweeperGameEvent {
        public Victory(Player player, int mines, int playtime) {
            super(player, mines, playtime);
        }
    }
    public static class Defeat extends MinesweeperGameEvent {
        public Defeat(Player player, int mines, int playtime) {
            super(player, mines, playtime);
        }
    }
}
