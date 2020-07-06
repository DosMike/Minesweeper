package de.dosmike.sponge.minesweeper;

import de.dosmike.sponge.megamenus.api.elements.IIcon;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

/** collection of Icon to render in Minesweeper menu */
final public class Icons {

    static IIcon MF_COVERED = IIcon.of(ItemStack.builder().itemType(ItemTypes.STAINED_GLASS_PANE).add(Keys.DYE_COLOR, DyeColors.GRAY).build());
    static IIcon MF_MINE = IIcon.of(ItemStack.builder().itemType(ItemTypes.TNT).build());
    static IIcon MF_FLAG = IIcon.of(ItemStack.builder().itemType(ItemTypes.BANNER).add(Keys.DYE_COLOR, DyeColors.RED).build());
    static IIcon MF_CLOCK = IIcon.of(ItemStack.builder().itemType(ItemTypes.CLOCK).build());
    static IIcon MF_INFO = IIcon.of(ItemStack.builder().itemType(ItemTypes.BOOK).build());
    static IIcon MF_SKULL = IIcon.of(ItemStack.builder().itemType(ItemTypes.SKULL).add(Keys.SKULL_TYPE, SkullTypes.SKELETON).build());
    static IIcon MF_CHICKENDINNER = IIcon.of(ItemStack.builder().itemType(ItemTypes.COOKED_CHICKEN).build());
    static IIcon[] MF_BOMBS = {
            IIcon.of(ItemStack.empty()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(1).add(Keys.DYE_COLOR, DyeColors.LIGHT_BLUE).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(2).add(Keys.DYE_COLOR, DyeColors.GREEN).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(3).add(Keys.DYE_COLOR, DyeColors.RED).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(4).add(Keys.DYE_COLOR, DyeColors.BLUE).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(5).add(Keys.DYE_COLOR, DyeColors.BROWN).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(6).add(Keys.DYE_COLOR, DyeColors.CYAN).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(7).add(Keys.DYE_COLOR, DyeColors.BLACK).build()),
            IIcon.of(ItemStack.builder().itemType(ItemTypes.WOOL).quantity(8).add(Keys.DYE_COLOR, DyeColors.GRAY).build()),
    };

}
