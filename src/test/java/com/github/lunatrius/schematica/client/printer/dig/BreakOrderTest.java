package com.github.lunatrius.schematica.client.printer.dig;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BreakOrderTest {
    private static final BlockPos FEET = new BlockPos(0, 64, 0);
    private static final Vec3d EYE = new Vec3d(0.5, 65.62, 0.5);

    @Test
    public void columnIsDugTopDownEvenWhenTheLowerBlockIsCloser() {
        // Column at x=2: y=65 (eye level, farther from the floor) above y=64.
        final List<BlockPos> toDig = Arrays.asList(new BlockPos(2, 64, 0), new BlockPos(2, 66, 0), new BlockPos(2, 65, 0));
        assertEquals(new BlockPos(2, 66, 0), BreakOrder.next(toDig, FEET, 4.5, EYE));
    }

    @Test
    public void nearestColumnFirst() {
        final List<BlockPos> toDig = Arrays.asList(new BlockPos(3, 65, 0), new BlockPos(1, 65, 0));
        assertEquals(new BlockPos(1, 65, 0), BreakOrder.next(toDig, FEET, 4.5, EYE));
    }

    @Test
    public void outOfReachIsExcluded() {
        assertNull(BreakOrder.next(Collections.singletonList(new BlockPos(9, 65, 0)), FEET, 4.5, EYE));
    }

    @Test
    public void neverTheBlockUnderTheFeet() {
        assertNull(BreakOrder.next(Collections.singletonList(new BlockPos(0, 63, 0)), FEET, 4.5, EYE));
        // ... but the next block of the column below the feet is fine once something else is chosen first
        final List<BlockPos> toDig = Arrays.asList(new BlockPos(0, 63, 0), new BlockPos(1, 63, 0));
        assertEquals(new BlockPos(1, 63, 0), BreakOrder.next(toDig, FEET, 4.5, EYE));
    }

    @Test
    public void tiesAreDeterministic() {
        final List<BlockPos> toDig = Arrays.asList(new BlockPos(0, 65, 1), new BlockPos(1, 65, 0), new BlockPos(0, 65, -1), new BlockPos(-1, 65, 0));
        assertEquals(new BlockPos(-1, 65, 0), BreakOrder.next(toDig, FEET, 4.5, EYE));
    }
}
