package com.github.lunatrius.schematica.client.printer.dig;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ToolChoiceTest {
    private static ToolChoice.Candidate pick(final int slot, final String name, final int level, final float speed, final int durability, final int fortune, final boolean silk) {
        return new ToolChoice.Candidate(slot, name, level, "pickaxe", speed, durability, fortune, silk);
    }

    private static final ToolChoice.Need STONE = new ToolChoice.Need("pickaxe", 0, false);
    private static final ToolChoice.Need OBSIDIAN = new ToolChoice.Need("pickaxe", 3, false);
    private static final ToolChoice.Need DIAMOND_ORE = new ToolChoice.Need("pickaxe", 2, true);

    @Test
    public void fastestCapableToolForStone() {
        final List<ToolChoice.Candidate> tools = Arrays.asList(pick(0, "stone", 1, 4f, 100, 0, false), pick(1, "iron", 2, 6f, 200, 0, false));
        assertEquals("iron", ToolChoice.pick(tools, STONE, true, 5).name);
    }

    @Test
    public void harvestLevelIsRequired() {
        final List<ToolChoice.Candidate> tools = Collections.singletonList(pick(0, "iron", 2, 6f, 200, 0, false));
        assertNull(ToolChoice.pick(tools, OBSIDIAN, true, 5));
        assertNull(ToolChoice.worn(tools, OBSIDIAN, 5)); // not "worn": simply not capable
    }

    @Test
    public void wrongToolClassIsNotCapable() {
        final ToolChoice.Candidate shovel = new ToolChoice.Candidate(0, "shovel", 3, "shovel", 8f, 500, 0, false);
        assertNull(ToolChoice.pick(Collections.singletonList(shovel), STONE, true, 5));
    }

    @Test
    public void fortunePreferredWhereItMultipliesDrops() {
        final List<ToolChoice.Candidate> tools = Arrays.asList(pick(0, "diamond-eff5", 3, 20f, 900, 0, false), pick(4, "iron-fortune3", 2, 6f, 200, 3, false));
        assertEquals("iron-fortune3", ToolChoice.pick(tools, DIAMOND_ORE, true, 5).name);
        assertEquals("diamond-eff5", ToolChoice.pick(tools, DIAMOND_ORE, false, 5).name);
        assertEquals("diamond-eff5", ToolChoice.pick(tools, STONE, true, 5).name);
    }

    @Test
    public void wornToolsAreSkippedAndReported() {
        final List<ToolChoice.Candidate> tools = Arrays.asList(pick(0, "stone", 1, 4f, 3, 0, false), pick(1, "wood", 0, 2f, 50, 0, false));
        assertEquals("wood", ToolChoice.pick(tools, STONE, true, 5).name);
        final List<ToolChoice.Candidate> onlyWorn = Collections.singletonList(pick(0, "stone", 1, 4f, 5, 0, false));
        assertNull(ToolChoice.pick(onlyWorn, STONE, true, 5));
        assertEquals("stone", ToolChoice.worn(onlyWorn, STONE, 5));
    }

    @Test
    public void unbreakableToolsAreNeverWorn() {
        assertEquals("unbreakable", ToolChoice.pick(Collections.singletonList(pick(0, "unbreakable", 3, 8f, -1, 0, false)), STONE, true, 5).name);
    }

    @Test
    public void silkTouchIsNeverChosen() {
        final List<ToolChoice.Candidate> tools = Arrays.asList(pick(0, "silk", 3, 20f, 900, 0, true), pick(2, "plain", 2, 6f, 200, 0, false));
        assertEquals("plain", ToolChoice.pick(tools, DIAMOND_ORE, true, 5).name);
        assertNull(ToolChoice.pick(Collections.singletonList(pick(0, "silk", 3, 20f, 900, 0, true)), STONE, true, 5));
    }

    @Test
    public void tiesGoToTheLowerSlot() {
        final List<ToolChoice.Candidate> tools = Arrays.asList(pick(5, "b", 2, 6f, 200, 0, false), pick(2, "a", 2, 6f, 200, 0, false));
        assertEquals("a", ToolChoice.pick(tools, STONE, true, 5).name);
    }
}
