import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class King extends Piece implements Moved {
    boolean moved = false;

    public King(boolean color, Square s) {
        super(color, 'K', s, 10000);
    }

    public King(boolean color, Square s, boolean moved) {
        super(color, 'K', s, 0);
        this.moved = moved;
    }

    @Override
    public Piece copy() {
        return new King(this.color, this.s, this.moved);
    }

    @Override
    public List<Move> allLegalMoves(Board b, boolean checkForChecks) {
      //  System.out.println("starting king moves");
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
                    m = new Move(this.s, end, b, this.color);
                    if ((capture == null || !this.sameColor(capture)) && (!checkForChecks || !m.isInCheck())) {
                       // System.out.println("king move added: " + m);
                        moves.add(m);
                    }
                }
            }
        }
     //   System.out.println("finished regular king moves");

        if (!this.moved && checkForChecks) {
  //          System.out.println("kingside");
            moves.add(this.castling(b, true));
  //          System.out.println("queenside");
            moves.add(this.castling(b, false));
 //           System.out.println("done");
        }
  //      System.out.println("finished king moves");
        return moves;
    }

    @Override
    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.moved == ((King) p).wasMoved()
                && this.s.equal(p.getSquare()));
    }

    public boolean wasMoved() {
        return this.moved;
    }

    @Override
    public void move(Square s) {
        this.s = s;
        this.moved = true;
    }

    //add not while in check
    public Move castling(Board b, boolean kingSide) {
        Piece[][] a = b.getArray();
        int i = this.s.getRank(), j = 7, first = 6, second = 5;
        if (!kingSide) {
            j = 0;
            first -= 3;
            second -= 3;
            if (a[i][1] != null) {
      //          System.out.println(new Square(i, 1)  + " is not clear");
                return null;
            }
        }
        Piece rook = a[i][j];
        List<Move> moves = b.allMoves(!this.color, false);
        //if the rook hasn't moved and in the corner and the squers between are clear
        if (rook instanceof Rook && !((Rook)rook).wasMoved() && a[i][first] == null && a[i][second] == null) {
     //       System.out.println(Arrays.toString(b.getArray()[rook.getSquare().getRank()]));
            //check if the squares are attacted
            for (Move m : moves) {
                if (m != null) {
                    Square end = m.getEnd();
                    if (end.getRank() == i && (end.getFile() == first || end.getFile() == second)) {
        //                System.out.println(new Square(i, first) + " or "  + new Square(i, second) + " is not clear " + m);
                        return null;
                    }
                }
            }
                return Move.createCastlingMove(kingSide, b, color, this.s, rook.getSquare());
        }
    //     System.out.println("rook on " + new Square(i, j) + " moved, or there are stuff in the way or just not");
        return null;
    }
}