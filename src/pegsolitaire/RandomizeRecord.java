package pegsolitaire;

public class RandomizeRecord extends MoveRecord {

    public boolean[][] newBoardState;

    public RandomizeRecord(boolean[][] state) {
        // dummy values for MoveRecord fields (not used for randomize)
        super(-1, -1, -1, -1, -1, -1, System.currentTimeMillis());
        this.newBoardState = state;
    }
}
