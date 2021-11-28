import java.util.List;

public class Bishop extends Piece {

    public Bishop(boolean color, Square s) {
        super(color, 'B', s);
    }

    @Override
    public Piece copy() {
        return new Bishop(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        return this.allDiagonalMoves(b, checkForChecks);
    }
}
