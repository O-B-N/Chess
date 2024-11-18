import java.util.List;

/**
 * a knight class
 */
public class Knight extends Piece {

    /**
     * creates a knight piece
     * @param color of the piece
     */
    public Knight(boolean color) {
        super(color, 'N', 3);
    }

    @Override
    public Piece copy() {
        return new Knight(this.color);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        return this.knightMoves(b, s);
    }
}
