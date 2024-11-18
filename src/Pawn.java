import java.util.ArrayList;
import java.util.List;

/**
 * a pawn class
 */
public class Pawn extends Piece {

    /**
     * creates a pawn piece
     *
     * @param color of the piece
     */
    public Pawn(boolean color) {
        super(color, 'P', 1);
    }

    @Override
    public Piece copy() {
        return new Pawn(this.color);
    }

    @Override
    public List<Move> allLegalMoves(Board b, Square s, boolean checkForChecks) {
        List<Move> moves = new ArrayList<>();
        int direction = this.color ? 1 : -1, file = s.getFile(), finalRank = s.getRank() + direction;
        //keep track if the next rank is the starting\ending ones
        boolean promote = finalRank == 0 || finalRank == 7;
        Square forwardSquare = new Square(finalRank, file);
        if (b.getPiece(forwardSquare) == null) {
            if (promote) {
                moves.addAll(Move.createPromotionMoves(s, forwardSquare, b, this.color));
            } else {
                //adding the forward move
                moves.add(new Move(s, forwardSquare, b));
                //checking if the pawn is in his starting rank (hence not moved tet and can move 2 forward
                boolean onStartingSquare = ((s.getRank() - direction == 0) || (s.getRank() - direction == 7));
                Square SecondAdvance = new Square(finalRank + direction, file);
                //if the second square is clear too and the pawn didn't move yet
                if (onStartingSquare && b.getPiece(SecondAdvance) == null) {
                    moves.add(new Move(s, SecondAdvance, b));
                }
            }
        }
        //getting captures from both sides of the pawn
        moves.addAll((capture(s, promote, b, 1, finalRank)));
        moves.addAll((capture(s, promote, b, -1, finalRank)));
        Square enPassant = b.getEnPassant();
        //en passant handling needs more testing
        if (enPassant != null && s.getRank() == enPassant.getRank() && (s.getFile() == enPassant.getFile() - 1 || s.getFile() == enPassant.getFile() + 1)) {
            moves.add(Move.createEnPassantMove(s, new Square(enPassant.getRank() + direction, enPassant.getFile()), b));
        }
        return moves;
    }

    /**
     * the method returns all possible captures moves except en passant
     *
     * @param s         the start square
     * @param promote   true if the move is a promotion
     * @param b         the board
     * @param side      the side the pawn moves, 1 for right -1 for left
     * @param finalRank the final rank of the capture
     * @return the list of possible moves
     */
    private List<Move> capture(Square s, boolean promote, Board b, int side, int finalRank) {
        List<Move> moves = new ArrayList<>();
        Square end = Square.create(finalRank, s.getFile() + side);
        if (end != null && b.getPiece(end) != null) {
            if (promote) {
                return Move.createPromotionMoves(s, end, b, this.color);
            }
            moves.add(new Move(s, end, b));
        }
        return moves;
    }
}
