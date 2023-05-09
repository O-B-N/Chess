import java.util.List;

/**
 * knight class
 */
public class Knight extends Piece {

    /**
     * creates a knight piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public Knight(boolean color,  Square s) {
        super(color, 'N', s, 3);
    }

    @Override
    public Piece copy() {
        return new Knight(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        return this.knightMoves(b, checkForChecks);
    }
}
