package de.dosmike.sponge.minesweeper;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

final public class CommandRegistra {

    public static void registerCommands() {
        Sponge.getCommandManager().register(Minesweeper.getInstance(), CommandSpec.builder()
                .permission("minesweeper.command")
                .description(Text.of("Start a round of minesweeper"))
                .arguments(
                        GenericArguments.optional(GenericArguments.integer(Text.of("mines")))
                ).executor((src,args)->{

                    if (!(src instanceof Player))
                        throw new CommandException(Text.of("Sorry console, minesweeper is player only"));

                    int mines = args.<Integer>getOne("mines").orElse(8);
                    if (mines < 4) {
                        src.sendMessage(Text.of("Come on, less than 3 mines is cheating"));
                    } else if (mines > 10) {
                        src.sendMessage(Text.of("Calm down, trust me more than 10 mines is just unplayable"));
                    } else {
                        new Minefield(mines).play((Player) src);
                    }

                    return CommandResult.success();
                }).build(), "minesweeper");
    }

}
