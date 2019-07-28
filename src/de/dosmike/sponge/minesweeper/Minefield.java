package de.dosmike.sponge.minesweeper;

import de.dosmike.sponge.megamenus.MegaMenus;
import de.dosmike.sponge.megamenus.api.IMenu;
import de.dosmike.sponge.megamenus.api.MenuRenderer;
import de.dosmike.sponge.megamenus.api.elements.MButton;
import de.dosmike.sponge.megamenus.api.elements.MIcon;
import de.dosmike.sponge.megamenus.api.listener.OnClickListener;
import de.dosmike.sponge.megamenus.api.listener.OnRenderStateListener;
import de.dosmike.sponge.megamenus.impl.BaseMenuImpl;
import de.dosmike.sponge.megamenus.impl.util.MenuUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.awt.event.MouseEvent;
import java.util.*;

import static de.dosmike.sponge.minesweeper.Icons.*;

//mine = 0x10, flag = 0x20, open = 0x40 as mask
public class Minefield {

    //random for bomb placement
    private static final Random MF_RNG = new Random();

    //map to track minesweeper games
    private static Map<UUID, Minefield> minefields = new HashMap<>();

    private int[][] values = new int[8][6];
    private int mines, closedFields, marks = 0;
    private boolean over=false;
    private int passedSec=0;

    //references to fancy/"interactive" icons on the right
    private MIcon clock = MIcon.builder()
            .setIcon(MF_CLOCK)
            .setName(Text.of(TextColors.WHITE, "Playtime: ",TextColors.GOLD,"0:0"))
            .setPosition(new SlotPos(8,1))
            .build();
    private MIcon info = MIcon.builder()
            .setIcon(MF_INFO)
            .setName(Text.of(TextColors.WHITE, "Mines cleard: ",TextColors.GOLD,"0/8"))
            .setPosition(new SlotPos(8,2))
            .build();
    private MIcon game = MIcon.builder()
            .setIcon(ItemStack.empty())
            .setPosition(new SlotPos(8,4))
            .build();

    public Minefield(int mineCount) {
        assert mineCount>3&&mineCount<11;
        mines = mineCount;
        closedFields = 48-mines;
        //generate mines
        for (int i=0;i<mines;i++) {
            int x = MF_RNG.nextInt(8);
            int y = MF_RNG.nextInt(6);
            if (values[x][y] >0) i--; //repeat
            else values[x][y] = 0x10;
        }
        //generate values
        for (int x=0;x<8;x++)
            for (int y=0;y<6;y++) {
                if (values[x][y]==0x10) continue; //is bomb
                int c = 0;
                if (x>0) {
                    if ((values[x-1][y]&0x10)>0)c++;
                    if (y>0 && (values[x-1][y-1]&0x10)>0)c++;
                    if (y<5 && (values[x-1][y+1]&0x10)>0)c++;
                }
                if (x<7) {
                    if ((values[x+1][y]&0x10)>0)c++;
                    if (y>0 && (values[x+1][y-1]&0x10)>0)c++;
                    if (y<5 && (values[x+1][y+1]&0x10)>0)c++;
                }
                if (y>0 && (values[x][y-1]&0x10)>0)c++;
                if (y<5 && (values[x][y+1]&0x10)>0)c++;
                values[x][y]=c;
            }
    }

    //build menu and renderer from values
    public void play(Player player) {
        BaseMenuImpl menu = MegaMenus.createMenu(); //create menu
        menu.setTitle(Text.of("Minesweeper")); //set title
        for (int x=0;x<8;x++) //populate all fields with a builder
            for (int y=0;y<6;y++)
                menu.add(MButton.builder()
                        .setIcon(MF_COVERED)
                        .setName(Text.of("?"))
                        .setPosition(new SlotPos(x,y))
                        .setOnClickListener(MF_FIELD_LISTENER) //set same on click listener
                        .build());
        //set other menu elements
        menu.add(clock);
        menu.add(game);
        menu.add(info);
        //create renderer
        MenuRenderer render = (MenuRenderer)menu.createGuiRenderer( 6,false);
        render.setRenderListener(new OnRenderStateListener() { //add render lestener
            int passedTimeMs=0;
            int animX=0, animY=0;

            //hook tick to count playtime and play animations on gameover
            @Override
            public boolean tick(int ms, MenuRenderer render, IMenu menu) {
                passedTimeMs += ms;
                if (over) { //if game ended
                    if (passedTimeMs>=250) { //every quarter second
                        passedTimeMs = 0;
                        if (closedFields > 0 && animY < 6) { //if the game was lost (there are still non-bomb fields covered)
                            //search next covered field (value & 0x40)
                            while ((values[animX][animY] & 0x40)>0) {
                                //go to next closed slot
                                if (++animX >= 8) {
                                    animX = 0;
                                    animY++;
                                    if (animY>=6)
                                        return false;
                                }
                            }

                            //fetch builded MButton at slot position
                            MButton button = (MButton) MenuUtil.getElementAt(menu, 1, SlotPos.of(animX, animY)).get();

                            values[animX][animY] |= 0x40; //open
                            //render open
                            if ((values[animX][animY] & 0x10) == 0) { //covered safe space
                                int around = values[animX][animY]&0x0f; //amount of mines around stored in value & 0x0f
                                button.setIcon(MF_BOMBS[around]);
                                button.setName(Text.of(around == 0 ? "Clean" : around+" nearby"));
                                player.playSound(SoundTypes.BLOCK_GRAVEL_BREAK, SoundCategories.MASTER, player.getPosition(), 0.3);
                            } else { //covered tnt
                                button.setIcon(MF_MINE);
                                button.setName(Text.of(TextColors.RED, "TNT"));
                                player.playSound(SoundTypes.ENTITY_GENERIC_EXPLODE, SoundCategories.MASTER, player.getPosition(), 0.3);
                            }
                            return true; //notify render that some elements changed
                        }
                    }
                    return false; //no elements changed
                } else if (passedTimeMs>=1000) { //every second in game
                    passedTimeMs -= 1000;
                    passedSec++; //count playtime

                    //update MIcons
                    clock.setName(Text.of(TextColors.WHITE, "Playtime: ", TextColors.GOLD, passedSec / 60, ":", passedSec % 60));
                    info.setName(Text.of(TextColors.WHITE, "Cleared: ", TextColors.GOLD, 48 - mines - closedFields, "/", 48 - mines));
                    info.setLore(Collections.singletonList(Text.of(TextColors.WHITE, "Marked: ", TextColors.GOLD, marks, "/", mines)));
                    return true; //notify render that some elements changed
                } else return false; //no elements changed
            }

            @Override
            public boolean closed(MenuRenderer render, IMenu menu, Player viewer) {
                //if the player closes a menu untrack them
                MegaMenus.l("Render instance closed");
                minefields.remove(viewer.getUniqueId());
                return false;
            }
        });
        //we're about to open the menu, track player so the click listener can access the minefield again
        minefields.put(player.getUniqueId(), this);
        //show menu to player
        render.open(player);
    }

    /** on click listener arguments: IElement, Viewer, Button, Shift held */
    private static OnClickListener MF_FIELD_LISTENER = (e, v, b, s)->{
        MButton button = ((MButton)e); //we only added this listener to MButtons so cast is safe
        SlotPos pos = button.getPosition();
        Minefield field = minefields.get(v.getUniqueId()); //get the Minefield for player
        if (field == null || field.over) return; //don't react if game is over
        int value = field.values[pos.getX()][pos.getY()]; //check value for clicked index
        if (b == MouseEvent.BUTTON1) { //if left click (primary click)
            if ((value&0x10)>0) { //field is TNT
                field.over = true; //game ends here
                field.values[pos.getX()][pos.getY()]=value|0x40; //uncoverd
                //update button
                button.setIcon(MF_MINE);
                button.setName(Text.of(TextColors.RED, "TNT"));
                //show skull at right side
                field.game.setIcon(MF_SKULL);
                field.game.setName(Text.of(TextColors.RED, "Game Over"));
                field.game.setLore(Collections.singletonList(Text.of(TextColors.WHITE, "You hit TNT")));
                //play explosion sound
                v.playSound(SoundTypes.ENTITY_GENERIC_EXPLODE, SoundCategories.MASTER, v.getPosition(), 0.3);
                //notify button/menu/renderer that elements were changed and menu has to redraw
                button.invalidate(v);
                field.emitGameEnd(v, false);
            } else if ((value&0x40)==0) { //field is closed (0x40 == 0)
                field.values[pos.getX()][pos.getY()]=value|0x40; //mark as open
                int around = value&0x0f; //fetch amount of surrounding TNT
                button.setIcon(MF_BOMBS[around]); //set icon accordingly
                button.setName(Text.of(around == 0 ? "Clean" : around+" nearby"));
                //play dig sound
                v.playSound(SoundTypes.BLOCK_GRAVEL_BREAK, SoundCategories.MASTER, v.getPosition(), 0.3);
                if ((value&0x20)>0) //was marked with flag, give it back
                    field.marks--;
                field.closedFields--; //"increase" score
                if (field.closedFields == 0) { // no more closed fields, player won
                    //play victory jingle
                    v.playSound(SoundTypes.ENTITY_PLAYER_LEVELUP, SoundCategories.MASTER, v.getPosition(), 0.5);
                    field.over = true; //game ends here
                    //display chicken dinner at right side
                    field.game.setIcon(MF_CHICKENDINNER);
                    field.game.setName(Text.of(TextColors.GOLD, "Winner Winner"));
                    field.game.setLore(Collections.singletonList(Text.of(TextColors.WHITE, "Chicken Dinner")));

                    //tell the server how good player is with probably 4 mines
                    Sponge.getServer().getBroadcastChannel().send(Text.of(
                            TextColors.BLUE, v.getName(),
                            TextColors.WHITE, " finished ",
                            Text.builder("/minesweeper")
                                    .style(TextStyles.UNDERLINE)
                                    .onHover(TextActions.showText(Text.of(TextColors.GOLD,"Click to play")))
                                    .onClick(TextActions.suggestCommand("/minesweeper "+field.mines))
                                    .build(),
                            " with ",
                            TextColors.RED, field.mines,
                            TextColors.WHITE, " mines in ",
                            TextColors.GOLD, field.passedSec/60, ":", field.passedSec%60, "s",
                            TextColors.WHITE,"!"
                    ));
                    field.emitGameEnd(v, true);
                }
                //notify button/menu/renderer that elements were changed and menu has to redraw
                button.invalidate(v);
            }
        //otherwise right mouse button was used
        } else if (b == MouseEvent.BUTTON2 && (value&0x40)==0) { //check that only closed fields are right-clicked
            if ((value&0x20)==0) { //not marked
                if (field.marks==field.mines) { //all marks set
                    //can't place more marks, play "out of stock"-sound
                    v.playSound(SoundTypes.BLOCK_WOOD_BUTTON_CLICK_ON, SoundCategories.MASTER, v.getPosition(), 0.3);
                } else {
                    field.marks++; //track placed marks
                    field.values[pos.getX()][pos.getY()] = value | 0x20; //mark field
                    button.setIcon(MF_FLAG); //update Icon
                    button.setName(Text.of(TextColors.YELLOW, "Marked"));
                    //play place sound
                    v.playSound(SoundTypes.BLOCK_SNOW_PLACE, SoundCategories.MASTER, v.getPosition(), 0.3);
                }
            } else { //unmark field
                field.marks--; //give 1 back
                field.values[pos.getX()][pos.getY()]=value&~0x20; //unset mark value
                //update Icon back to default
                button.setIcon(MF_COVERED);
                button.setName(Text.of("?"));
                //play unmark sound
                v.playSound(SoundTypes.ENTITY_ITEMFRAME_REMOVE_ITEM, SoundCategories.MASTER, v.getPosition(), 0.3);
            }
        }
    };

    private void emitGameEnd(Player player, boolean victory) {
        try (CauseStackManager.StackFrame farme = Sponge.getCauseStackManager().pushCauseFrame()) {
            Event e = victory
                    ? new MinesweeperGameEvent.Victory(player, mines, passedSec)
                    : new MinesweeperGameEvent.Defeat(player, mines, passedSec);
            Sponge.getEventManager().post(e);
        }
    }

}
