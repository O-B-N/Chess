import java.util.List;

/**
 * bishop class
 */
public class Bishop extends Piece {

    /**
     * creates a bishop piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public Bishop(boolean color, Square s) {
        super(color, 'B', s, 300);
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
