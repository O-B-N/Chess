import java.util.List;

/**
 * queen class
 */
public class Queen extends Piece {

    /**
     * creates a queen piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public Queen(boolean color, Square s) {
        super(color, 'Q', s, 9);
    }

    @Override
    public Queen copy() {
        return new Queen(this.getColor(), this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.allStraightMoves(b, checkForChecks);
        moves.addAll(this.allDiagonalMoves(b, checkForChecks));
        return moves;
    }
}
