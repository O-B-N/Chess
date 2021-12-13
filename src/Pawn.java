import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pawn extends Piece {

    ///check for check!!!

    public Pawn(boolean color, Square s) {
        super(color, 'P', s, 1);
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color, this.s);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
        Square pawn = this.getSquare();
        List<Move> moves = new ArrayList<>(), temp = new ArrayList<>();
        boolean c = this.color;
        Move m;
        int direction = this.color ? 1 : -1, rank = pawn.getRank(), file = pawn.getFile();
        Square forwardSquare = new Square(rank + direction, file);
        Piece forwardPiece = b.getPiece(forwardSquare);
        if (forwardPiece == null) {
            if (rank + direction == 0 || rank + direction == 7) {
                moves.addAll(promote(forwardSquare, b));
            } else {
                moves.add(Move.createDoublePawnMove(pawn, forwardSquare, b, c));
                if (rank == 1 || rank == 6) {
                    forwardSquare = new Square(rank + 2 * direction, file);
                    moves.add(new Move(pawn, forwardSquare, b, c));
                }
            }
        }
        boolean promote = c && rank == 6 || !c && rank == 1;
        moves.addAll(Objects.requireNonNull(capture(promote, b, 1, direction)));
        moves.addAll(Objects.requireNonNull(capture(promote, b, -1, direction)));
        moves.add(enPassant(b, -1, direction));
        moves.add(enPassant(b, -1, direction));
        temp.addAll(moves);
        for (Move move : temp) {
            if(checkForChecks && !move.isCheck()) {
                moves.remove(move);
            }
        }
        return moves;
    }

    private List<Move> promote(Square end, Board b) {
        return Move.createPromotionMove(this.s, end, b, this.color);
    }

    private Move enPassant(Board b, int side, int direction) {
        int file = this.s.getFile(), rank = this.s.getRank();
        Square end = Square.create(rank + direction, file + side);
        Square enPassant = Square.create(rank , file + side);
        Move lastMove = b.getLastMove();
        if (lastMove.isEnPassant() && lastMove.getEnd().equal(enPassant)) {
            return Move.createEnPassantMove(this.s, end, b, color);
        }
        return  null;
    }

    private List<Move> capture(boolean promote, Board b, int side, int direction) {
        List<Move> moves = new ArrayList<>();
        Square end = Square.create(s.getRank() + direction, s.getFile() + side);
        if (end != null) {
            Piece capture = b.getPiece(end);
            if (capture != null && capture.getColor() != this.color) {
                if (promote) {
                        return this.promote(end, b);
                } else {
                    moves.add(new Move(this.getSquare(), end, b, this.color));
                    return moves;
                }
            }
        }
        return  null;
    }
}
