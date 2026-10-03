package com.github.lunatrius.schematica.client.printer.dig;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/** Fork: is it safe to dig this block? */
public final class HazardCheck {
    /** Digging a block with at least this many air blocks under it would open a dangerous drop. */
    public static final int DROP_LIMIT = 4;
    private static final int MAX_SCAN = 64;

    private HazardCheck() {
    }

    /** Returns null when safe, else a reason such as "lava next to 1 2 3" (lava is reported before water). */
    public static String check(final BlockView view, final BlockPos target) {
        String water = null;
        for (final EnumFacing side : EnumFacing.VALUES) {
            final BlockPos n = target.offset(side);
            if (view.isLava(n)) {
                return "lava next to " + str(target);
            }
            if (water == null && view.isLiquid(n)) {
                water = "water next to " + str(target);
            }
        }
        if (water != null) {
            return water;
        }
        int air = 0;
        for (BlockPos p = target.down(); air < MAX_SCAN && p.getY() >= 0 && view.isAir(p); p = p.down()) {
            air++;
        }
        if (air >= DROP_LIMIT) {
            return "drop of " + air + " below " + str(target);
        }
        return null;
    }

    public static String str(final BlockPos p) {
        return p.getX() + " " + p.getY() + " " + p.getZ();
    }
}
