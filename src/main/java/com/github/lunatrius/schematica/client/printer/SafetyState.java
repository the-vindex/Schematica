package com.github.lunatrius.schematica.client.printer;

/** Printer pause state: pauses on any danger, resumes after a number of consecutive quiet ticks. */
public final class SafetyState {
    public enum Transition { NONE, PAUSED, RESUMED }

    private final int resumeAfterTicks;
    private boolean paused;
    private String reason;
    private int quietTicks;

    public SafetyState(final int resumeAfterTicks) {
        this.resumeAfterTicks = Math.max(1, resumeAfterTicks);
    }

    /** @param danger why the printer should not place right now, or null when it is safe */
    public Transition tick(final String danger) {
        if (danger != null) {
            this.quietTicks = 0;
            this.reason = danger;
            if (!this.paused) {
                this.paused = true;
                return Transition.PAUSED;
            }
            return Transition.NONE;
        }
        if (this.paused && ++this.quietTicks >= this.resumeAfterTicks) {
            this.paused = false;
            this.reason = null;
            return Transition.RESUMED;
        }
        return Transition.NONE;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public String reason() {
        return this.reason;
    }

    public void reset() {
        this.paused = false;
        this.reason = null;
        this.quietTicks = 0;
    }
}
