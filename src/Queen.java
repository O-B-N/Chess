import java.util.List;

/**
 * a queen class
 */
public class Queen extends Piece {

    /**
     * creates a queen piece
     * @param color of the piece
     */
    public Queen(boolean color) {
        super(color, 'Q', 9);
    }

    @Override
    public Queen copy() {
        return new Queen(this.getColor());
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        List<Move> moves = this.allStraightMoves(b, s);
        moves.addAll(this.allDiagonalMoves(b, s));
        return moves;
    }
}
