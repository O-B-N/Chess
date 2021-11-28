import java.util.List;

public class Move {

    private final Square start;
    private final Square end;
    private final Piece piece;
    private Piece capture;
    private Board b;
    boolean doublePawnPush = false;
    boolean color;

    public Move(Square start, Square end, Board b, boolean color) {
        this.piece = this.b.getPiece(start.getRow(), start.getColumn());
        this.start = start;
        this.end = end;
        this.b = b;
        this.color = color;
    }

    public boolean isLegalMove() {
        /*
     //   if (this.piece == null) {
     //       System.out.println("no piece on the start square");
     //   } else if (this.color != this.piece.getColor()) {
     //       if (this.color) {
     //           System.out.println("it's white's turn");
     //       } else {
     //           System.out.println("it's black's turn");
     //       }
     //   }
     //
     //   if (this.isCapture() && (!this.capture.getColor() ^ (this.piece.getColor()))) {
     //       System.out.println("can't capture a piece with the same color");
     //   }
         */
        boolean isAMove = false;
        List<Move> l = this.piece.allLegalMoves(this.b.copy(), true);
        for (Move m : l) {
            if (this.equal(m)) {
                isAMove = true;
            }
        }
        /*
        if (!isAMove) {
            System.out.println("not a legal move");
        }

         */
        return isAMove;
    }

    public boolean isCheck() {
        this.b = this.b.copy();
        this.b.makeMove(this);
        List<Move> l = b.allMoves(!this.color, false);
        int index = 0;
        Move temp = l.get(index);
        while (temp != null && temp.isCapture() && temp.getCapture().getType() != 'K') {
            index++;
            temp = l.get(index);
        }
        return temp != null && temp.isCapture() && temp.getCapture().getType() == 'K';
    }

    public Square getStart() {
        return this.start;
    }

    public Square getEnd() {
        return this.end;
    }
    
    public Board getBoard() {
        return this.b;
    }

    public boolean isDoublePawnPush() {
        return this.doublePawnPush;
    }

    public boolean isCapture() {
        this.capture = this.b.getPiece(this.end.getRow(), this.end.getColumn());
        return capture != null;
    }

    public Piece getCapture() {
        return capture;
    }

    public boolean equal(Move m) {
        return this.b.equal(m.getBoard()) && this.start.equal(m.getStart()) && this.end.equal(m.getEnd());
    }



}
