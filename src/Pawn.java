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
    //    System.out.println("started pawn moves");
        Square pawn = this.getSquare();
        List<Move> moves = new ArrayList<>(), temp;
        boolean white = this.color;
        int direction = white ? 1 : -1, rank = pawn.getRank(), file = pawn.getFile(), finalRank = rank + direction;
        Square forwardSquare = new Square(finalRank, file);
        Piece forwardPiece = b.getPiece(forwardSquare);
        boolean promote = finalRank == 0 || finalRank == 7;
        if (forwardPiece == null && checkForChecks) {
            if (promote) {
    //            System.out.println("added forward promote");
                moves.addAll(promote(forwardSquare, b));
            } else {
   //             System.out.println("added forward pawn");
                moves.add(new Move(pawn, forwardSquare, b, white));
                boolean onStartingSquare = ((pawn.getRank() - direction == 0) || (pawn.getRank() - direction == 7));
                if (onStartingSquare && b.getPiece(finalRank + direction , file) == null) {
           //         System.out.println("added double forward");
                    moves.add(new Move(pawn, new Square(finalRank + direction, file), b, white, true));
                }
            }
        }
   //     System.out.println("done forward moves" + moves);
        moves.addAll((capture(promote, b, 1, finalRank)));
        moves.addAll((capture(promote, b, -1, finalRank)));
    //    System.out.println("done capture");
        if (checkForChecks) {
            moves.add(enPassant(b, 1, direction));
            moves.add(enPassant(b, -1, direction));
        }
        /*
        // else {
    //        System.out.println("skipping en passant, check for checks is: " + checkForChecks);
     //   }
    //   temp = new ArrayList<>(moves);
    //    for (Move move : temp) {
    //        System.out.println("reached here");
    //        if (move == null || (checkForChecks && move.isCheck())) {
    //            System.out.println("removed " + move);
    //            moves.remove(move);
    //        }
    //    }
        /*
        List<Move> l = new ArrayList<>(moves);
        for (int i = l.size() - 1; i >= 0; i--) {
            if (l.get(i) == null || (checkForChecks && l.get(i).isCheck())) {
                moves.remove(i);
            }
        }
      //  System.out.println("all pawn moves: " + moves);

         */
        return moves;
    }

    private boolean canDouble() {
        int rank = this.getSquare().getRank();
        return this.color && rank == 1 || !this.color && rank == 6;
    }

    private List<Move> promote(Square end, Board b) {
        return Move.createPromotionMove(this.s, end, b, this.color);
    }

    private Move enPassant(Board b, int side, int direction) {
      //  System.out.println("done capture, starting en passant");
        int file = this.s.getFile() + side, rank = this.s.getRank();
        Square end = Square.create(rank + direction, file);
        Square enPassant = Square.create(rank , file);
        Move lastMove = b.getLastMove();
        if (end != null && lastMove != null && lastMove.isDoublePawnPush() && lastMove.getEnd().equal(enPassant)) {
            return Move.createEnPassantMove(this.s, end, b, color);
        }
        return null;
    }

    private List<Move> capture(boolean promote, Board b, int side, int finalRank) {
       // System.out.println("started capture moves");
        List<Move> moves = new ArrayList<>();
        Square end = Square.create(finalRank, s.getFile() + side);
        if (end != null) {
            Piece capture = b.getPiece(end);
            if (capture != null && !capture.sameColor(this)) {
                if (promote) {
                        return this.promote(end, b);
                } else {
                    moves.add(new Move(this.getSquare(), end, b, this.color));
                    return moves;
                }
            }
        }
        return moves;
    }
}
