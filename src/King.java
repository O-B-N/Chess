import java.util.ArrayList;
import java.util.List;

/**
 * a king class
 */
public class King extends Piece {

    /**
     * creates a king piece
     *
     * @param color of the piece
     */
    public King(boolean color) {
        super(color, 'K', 0);
    }

    @Override
    public Piece copy() {
        return new King(this.color);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        List<Move> moves = new ArrayList<>();
        Piece capture;
        Move m;
        Square end;
        int newRow, newColumn;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                newRow = s.getRank() + i;
                newColumn = s.getFile() + j;
                end = Square.create(newRow, newColumn);
                if (end != null && !end.equals(s)) {
                    capture = b.getPiece(end);
                    m = new Move(s, end, b);
                    if (capture == null || this.differentColor(capture)) {
                        moves.add(m);
                    }
                }
            }
        }
        if (checkForChecks) {
            int kingFinal = 6, rookFinal = 5;
            char q = 'q', k = 'k';
            if (this.color) {
                q = Character.toUpperCase(q);
                k = Character.toUpperCase(k);
            }
            for (char c : b.getCastlingString().toCharArray()) {
                if (c == k && Character.isUpperCase(c) == this.color) {
                    moves.add(this.castling(b, s, new Square(s.getRank(), kingFinal), new Square(s.getRank(), rookFinal), true));
                }
                kingFinal = 2;
                rookFinal = 3;
                if (c == q && Character.isUpperCase(c) == this.color) {
                    moves.add(this.castling(b, s, new Square(s.getRank(), kingFinal), new Square(s.getRank(), rookFinal), false));
                }
            }
        }
        return moves;
    }

    /**
     * the method is checking if the king can castle in a specific direction
     *
     * @param b        the board
     * @param s        the start square
     * @param kingSide castling side
     * @return a castling move if it's legal, null otherwise
     */
    private Move castling(Board b, Square s, Square kingFinal, Square rookFinal, boolean kingSide) {
        int rookFile = b.getRookFiles()[kingSide ? 1 : 0];
        Square rookStart = new Square(s.getRank(), rookFile);
        //getting the correct rook file of that side
        if (safeSquares(b, s, rookStart, kingFinal, rookFinal)) {
            return Move.createCastlingMove(kingSide, b, s, rookStart);
        }
        return null;
    }

    /**
     * this method calculate which squares needed to be checked for castling
     * @param b         the board
     * @param kingStart king's start square
     * @param rookStart rook's start square
     * @param kingFinal king's final square
     * @param rookFinal rook's final square
     * @return true if castling is possible in that side
     */

    private boolean safeSquares(Board b, Square kingStart, Square rookStart, Square kingFinal, Square rookFinal) {
        List<Square> king = new ArrayList<>(), rook = new ArrayList<>();
        //rook and king list are to hold which squares checking every piece needs
        int rookDirection = rookFinal.getFile() - rookStart.getFile() > 0 ? 1 : -1,
                kingDirection = kingFinal.getFile() - kingStart.getFile() > 0 ? 1 : -1;
        Square temp = rookStart;
        while (!temp.equals(rookFinal)) {
            temp = new Square(temp.getRank(), temp.getFile() + rookDirection);
            rook.add(temp);
        }
        king.add(kingStart);
        temp = kingStart;
        while (!temp.equals(kingFinal)) {
            temp = new Square(temp.getRank(), temp.getFile() + kingDirection);
            king.add(temp);
        }
        return b.squaresClear(king, this.color, true, kingStart, rookStart) &&
                b.squaresClear(rook, this.color, false, kingStart, rookStart);
    }
}