import java.util.List;

/**
 * a bishop class
 */
public class Bishop extends Piece {

    /**
     * creates a bishop piece
     * @param color of the piece
     */
    public Bishop(boolean color) {
        super(color, 'B', 300);
    }

    @Override
    public Piece copy() {
        return new Bishop(this.color);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        return this.allDiagonalMoves(b, s);
    }
}
