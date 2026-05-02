package Pacman;

public class CollisionDetector {

    public static class CollisionResult {
        public final boolean playerDied;
        public final int pointsEarned;

        public CollisionResult(boolean playerDied, int pointsEarned) {
            this.playerDied = playerDied;
            this.pointsEarned = pointsEarned;
        }
    }

    private int ghostsEatenCombo;

    public CollisionDetector() {
        this.ghostsEatenCombo = 0;
    }

    public void resetCombo() {
        ghostsEatenCombo = 0;
    }

    public CollisionResult check(Player player, Ghost[] ghosts, GhostModeManager modeManager) {
        int totalPoints = 0;
        for (Ghost ghost : ghosts) {
            if (ghost.getX() == player.getX() && ghost.getY() == player.getY()) {
                if (ghost.isInPrison()) {
                    continue;
                }
                if (modeManager.isFrightened()) {
                    ghostsEatenCombo++;
                    int points = 200;
                    for (int i = 1; i < ghostsEatenCombo; i++) points *= 2;
                    totalPoints += points;
                    ghost.sendToPrison();
                } else {
                    return new CollisionResult(true, 0);
                }
            }
        }
        return new CollisionResult(false, totalPoints);
    }
}
