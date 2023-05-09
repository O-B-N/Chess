import java.util.ArrayList;
import java.util.List;

/**
 * pawn class
 */
public class Pawn extends Piece {

    /**
     * creates a pawn piece
     * @param color of the piece
     * @param s the square of the piece
     */
    public Pawn(boolean color, Square s) {
        super(color, 'P', s, 1);
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        Square s = this.getSquare();
        List<Move> moves = new ArrayList<>(), temp = new ArrayList<>();
        boolean c = this.color;
        int direction = c ? 1 : -1, file = s.getFile(), finalRank = s.getRank() + direction;
        boolean promote = finalRank == 0 || finalRank == 7;
        Square forwardSquare = new Square(finalRank, file);
        Piece forwardPiece = b.getPiece(forwardSquare);
        if (forwardPiece == null && checkForChecks) {
            if (promote) {
                temp.addAll(Move.createPromotionMoves(this.s, forwardSquare, b, this.color));
            } else {
                temp.add(new Move(s, forwardSquare, b));
                boolean onStartingSquare = ((s.getRank() - direction == 0) || (s.getRank() - direction == 7));
                if (onStartingSquare && b.getPiece(finalRank + direction , file) == null) {
                    temp.add(new Move(s, new Square(finalRank + direction, file), b));
                }
            }
        }
        temp.addAll((capture(promote, b, 1, finalRank)));
        temp.addAll((capture(promote, b, -1, finalRank)));
        Square enPassant = b.getEnPassant();
        if (enPassant != null && s.getRank() == enPassant.getRank() && (s.getFile() == enPassant.getFile() - 1 || s.getFile() == enPassant.getFile() + 1)) {
            temp.add(Move.createEnPassantMove(this.s, new Square(enPassant.getRank() + direction, enPassant.getFile()), b));
        }
        if (checkForChecks) {
            for (Move m: temp) {
                if (m.isInCheck(this.color)) {
                    moves.add(m);
                }
            }
            return moves;
        }
        moves.addAll(temp);
        return moves;
    }

    /**
     *
     * @param promote true if the move is a promotion
     * @param b the board
     * @param side the side the pawn moves, 1 for right -1 for left
     * @param finalRank the final rank of the capture
     * @return the list of possible moves
     */
    private List<Move> capture(boolean promote, Board b, int side, int finalRank) {
        List<Move> moves = new ArrayList<>();
        Square end = Square.create(finalRank, s.getFile() + side);
        if (end != null) {
            Piece capture = b.getPiece(end);
            if (capture != null && !capture.sameColor(this)) {
                if (promote) {
                        return Move.createPromotionMoves(this.s, end, b, this.color);
                } else {
                    moves.add(new Move(this.getSquare(), end, b));
                    return moves;
                }
            }
        }
        return moves;
    }
}
