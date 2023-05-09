import java.util.ArrayList;
import java.util.List;

/**
 * king class
 */
public class King extends Piece {

    /**
     * creates a queen piece
     *
     * @param color of the piece
     * @param s     the square of the piece
     */
    public King(boolean color, Square s) {
        super(color, 'K', s, 0);
    }

    @Override
    public Piece copy() {
        return new King(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        List<Move> moves = new ArrayList<>();
        Piece capture;
        Move m;
        Square end;
        int newRow, newColumn;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                newRow = this.s.getRank() + i;
                newColumn = this.s.getFile() + j;
                end = Square.create(newRow, newColumn);
                if (end != null && !end.equal(this.s)) {
                    capture = b.getPiece(end);
                    m = new Move(this.s, end, b);
                    if ((capture == null || !this.sameColor(capture)) && (!checkForChecks || !m.isInCheck(this.color))) {
                        moves.add(m);
                    }
                }
            }
        }
        char q = 'q', k = 'k';
        if (this.color) {
            q = Character.toUpperCase(q);
            k = Character.toUpperCase(k);
        }
        for (char c : b.getCastlingString().toCharArray()) {
            if (c == k && Character.isUpperCase(c) == this.color) {
                moves.add(this.castling(b, true));
            }
            if (c == q && Character.isUpperCase(c) == this.color) {
                moves.add(this.castling(b, false));
            }
        }
        return moves;
    }

    /**
     *
     * @param b the board
     * @param kingSide castling side
     * @return a castling move if it's legal, null otherwise
     */
    private Move castling(Board b, boolean kingSide) {
        int[] files = b.getRookFiles();
        Square current;
        Piece temp;
        int rank = this.s.getRank(), kingFinal = 6, file = this.s.getFile(), rookFinal = 5;
        if (!kingSide) {
            kingFinal = 2;
            rookFinal = 3;
        }
        Square rookSquare = new Square(rank, rookFinal);
        int direction = kingFinal - this.getSquare().getFile() == 1 ? 1 : -1;
        direction = kingFinal == this.getSquare().getFile() ? 0 : direction;
        if (!b.isEmpty(rookSquare)) {
            temp = b.getPiece(rookSquare);
            if (!(temp.equal(this) || temp instanceof Rook && files[kingSide ? 1:0] == rookFinal)) {
                return null;
            }
        }
            file += direction;
            do {
                current = new Square(rank, file);
                temp = b.getPiece(current);
                if (temp != null) {
                    if ((temp instanceof Rook && temp.color == this.color && files[kingSide ? 1 : 0] == file)
                            || (b.Controlled(current, !this.color, true) != 0)) {
                        return null;
                    }
                }
                if (kingFinal == file) {
                    return Move.createCastlingMove(kingSide, b, this.s, rookSquare);
                }
                file += direction;
            } while (file >= 0 && file < 8);
        return null;
    }
}