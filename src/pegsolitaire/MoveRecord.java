package pegsolitaire;

public class MoveRecord {

    public final int fromR, fromC;
    public final int overR, overC;
    public final int toR, toC;
    public final long timestamp;

    public MoveRecord(int fromR, int fromC,
                      int overR, int overC,
                      int toR, int toC,
                      long timestamp) {

        this.fromR = fromR;
        this.fromC = fromC;
        this.overR = overR;
        this.overC = overC;
        this.toR = toR;
        this.toC = toC;
        this.timestamp = timestamp;
    }
}

