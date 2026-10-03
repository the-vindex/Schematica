package com.github.lunatrius.schematica.client.printer.dig;

import net.minecraft.util.math.BlockPos;

/** Fork: the few world facts the digging logic needs, so it can be tested without a world. */
public interface BlockView {
    boolean isAir(BlockPos pos);

    /** Any fluid (water, lava, modded), source or flowing. */
    boolean isLiquid(BlockPos pos);

    boolean isLava(BlockPos pos);

    /** A tile entity with an inventory (chest, machine...): never dug. */
    boolean hasInventory(BlockPos pos);
}
