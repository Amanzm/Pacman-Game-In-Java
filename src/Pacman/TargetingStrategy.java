package Pacman;

public interface TargetingStrategy {
    int[] getTarget(Ghost ghost, Player player, Ghost blinky);
}
