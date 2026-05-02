package Pacman;

public class GhostModeManager {
    private static final int[] PHASE_DURATIONS = {58, 17, 58, 17, 42, 17, -1};
    private static final int FRIGHTENED_DURATION = 50;

    private GhostMode currentMode;
    private int modeTimer;
    private int modePhase;
    private boolean frightened;
    private int frightenedTimer;

    public GhostModeManager() {
        reset();
    }

    public void reset() {
        currentMode = GhostMode.SCATTER;
        modeTimer = 0;
        modePhase = 0;
        frightened = false;
        frightenedTimer = 0;
    }

    public void update() {
        if (frightened) {
            frightenedTimer++;
            if (frightenedTimer >= FRIGHTENED_DURATION) {
                frightened = false;
                frightenedTimer = 0;
            }
            return;
        }

        modeTimer++;
        if (modePhase < PHASE_DURATIONS.length) {
            int duration = PHASE_DURATIONS[modePhase];
            if (duration != -1 && modeTimer >= duration) {
                modeTimer = 0;
                modePhase++;
                currentMode = (modePhase % 2 == 0) ? GhostMode.SCATTER : GhostMode.CHASE;
            }
        }
    }

    public void startFrightened() {
        frightened = true;
        frightenedTimer = 0;
    }

    public GhostMode getActiveMode() {
        if (frightened) return GhostMode.FRIGHTENED;
        return currentMode;
    }

    public boolean isFrightened() {
        return frightened;
    }
}
