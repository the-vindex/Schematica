package com.github.lunatrius.schematica.client.printer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SafetyStateTest {
    @Test
    public void pausesOnDangerAndResumesAfterQuietTicks() {
        final SafetyState s = new SafetyState(3);
        assertEquals(SafetyState.Transition.NONE, s.tick(null));
        assertEquals(SafetyState.Transition.PAUSED, s.tick("slime nearby"));
        assertTrue(s.isPaused());
        assertEquals("slime nearby", s.reason());
        assertEquals(SafetyState.Transition.NONE, s.tick(null));
        assertEquals(SafetyState.Transition.NONE, s.tick(null));
        assertEquals(SafetyState.Transition.RESUMED, s.tick(null));
        assertFalse(s.isPaused());
    }

    @Test
    public void dangerRestartsTheQuietCount() {
        final SafetyState s = new SafetyState(2);
        s.tick("hurt");
        s.tick(null);
        assertEquals(SafetyState.Transition.NONE, s.tick("zombie nearby"));
        assertEquals("zombie nearby", s.reason());
        assertEquals(SafetyState.Transition.NONE, s.tick(null));
        assertEquals(SafetyState.Transition.RESUMED, s.tick(null));
    }

    @Test
    public void resetClearsThePause() {
        final SafetyState s = new SafetyState(100);
        s.tick("hurt");
        s.reset();
        assertFalse(s.isPaused());
        assertEquals(SafetyState.Transition.NONE, s.tick(null));
    }
}
