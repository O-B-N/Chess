import java.util.ArrayList;
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
        List<Move> moves = new ArrayList<>();
        Square s = this.s;
        Square end;
        Piece capture;
        for (int rank = -2; rank < 3; rank++) {
            for (int file = -2; file < 3; file++) {
                if ((Math.abs(rank) == 2 && Math.abs(file) == 1) || (Math.abs(file) == 2 && Math.abs(rank) == 1)) {
                    end = Square.create(s.getRank() + rank, s.getFile() + file);
                    if (end != null) {
                        capture = b.getPiece(end);
                        if ((capture == null || !this.sameColor(capture))) {
                            moves.add(new Move(s, end, b, this.color));
                        }
                    }
                }
            }
        }
     //   System.out.println("finished knight moves" + moves);
        return moves;
    }
}
