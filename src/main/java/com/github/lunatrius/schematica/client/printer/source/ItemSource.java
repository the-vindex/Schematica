package com.github.lunatrius.schematica.client.printer.source;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** A place the printer can take blocks from besides loose stacks in the player inventory (e.g. a Dank/Null). */
public interface ItemSource {
    /** Moves an inventory slot into the hotbar; returns the hotbar slot, or -1 when no swap slot is allowed. */
    interface HotbarSwapper {
        int toHotbar(int inventorySlot);
    }

    /** How many of {@code wanted} this source can supply for placement. */
    int count(EntityPlayer player, ItemStack wanted);

    /**
     * Makes a right-click with the main hand place {@code wanted}: selects the holding item in the hotbar
     * (swapping it in if needed) and prepares it. Returns false when this source cannot supply the item.
     */
    boolean prepare(EntityPlayerSP player, ItemStack wanted, HotbarSwapper swapper);
}
