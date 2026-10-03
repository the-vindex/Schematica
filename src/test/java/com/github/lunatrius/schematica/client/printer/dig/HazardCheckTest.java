package com.github.lunatrius.schematica.client.printer.dig;

import net.minecraft.util.math.BlockPos;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HazardCheckTest {
    /** Map-backed view: unset = stone (solid). */
    private static final class View implements BlockView {
        final Map<BlockPos, String> blocks = new HashMap<BlockPos, String>();

        @Override public boolean isAir(final BlockPos pos) { return "air".equals(this.blocks.get(pos)); }
        @Override public boolean isLiquid(final BlockPos pos) { final String b = this.blocks.get(pos); return "lava".equals(b) || "water".equals(b); }
        @Override public boolean isLava(final BlockPos pos) { return "lava".equals(this.blocks.get(pos)); }
        @Override public boolean hasInventory(final BlockPos pos) { return "chest".equals(this.blocks.get(pos)); }
    }

    private final View view = new View();
    private final BlockPos target = new BlockPos(10, 40, 10);

    @Test
    public void safeWhenSurroundedBySolidBlocks() {
        assertNull(HazardCheck.check(this.view, this.target));
    }

    @Test
    public void lavaNeighbour() {
        this.view.blocks.put(this.target.east(), "lava");
        assertEquals("lava next to 10 40 10", HazardCheck.check(this.view, this.target));
    }

    @Test
    public void waterNeighbour() {
        this.view.blocks.put(this.target.up(), "water");
        assertEquals("water next to 10 40 10", HazardCheck.check(this.view, this.target));
    }

    @Test
    public void lavaWinsOverWater() {
        this.view.blocks.put(this.target.north(), "water");
        this.view.blocks.put(this.target.south(), "lava");
        assertEquals("lava next to 10 40 10", HazardCheck.check(this.view, this.target));
    }

    @Test
    public void dropOfFiveBelow() {
        for (int i = 1; i <= 5; i++) {
            this.view.blocks.put(this.target.down(i), "air");
        }
        assertEquals("drop of 5 below 10 40 10", HazardCheck.check(this.view, this.target));
    }

    @Test
    public void shallowDropIsFine() {
        for (int i = 1; i <= 3; i++) {
            this.view.blocks.put(this.target.down(i), "air");
        }
        assertNull(HazardCheck.check(this.view, this.target));
    }
}
