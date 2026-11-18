package sokoban.model;

public class LevelProgress {

    private int highestCompleted;

    public boolean isUnlocked(int level) {
        return level <= nextLevel();
    }

    public boolean isCompleted(int level) {
        return level <= highestCompleted;
    }

    public void complete(int level) {
        highestCompleted = Math.max(highestCompleted, level);
    }

    public int nextLevel() {
        return highestCompleted + 1;
    }
}
