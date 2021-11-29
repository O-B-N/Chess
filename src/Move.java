import java.util.List;

public class Move {
    private int value = 0;
    private Square start;
    private Square end;
    private Piece piece;
    private Piece capture;
    private Board b;
    private boolean doublePawnPush = false;
    private boolean color;
    private boolean castling = false;
    private boolean isKingSide = false;
    private Move castleKing = null;
    private Move castleRook = null;

    public Move(Square start, Square end, Board b, boolean color) {
        this.b = b;
        this.piece = this.b.getPiece(start.getRow(), start.getColumn());
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public Move (boolean kingSide, Board b, boolean color, Square king, Square rook) {
        this.b = b;
        this.piece = null;
        this.start = null;
        this.end = null;
        this.color = color;
        this.castling = true;
        Square endRook, endKing;
        this.isKingSide = kingSide;
        if (kingSide) {
            endKing = new Square(king.getRow(), king.getColumn() + 2);
            endRook = new Square(rook.getRow(), rook.getColumn() - 2);
        } else {
            endKing = new Square(king.getRow(), king.getColumn() - 2);
            endRook = new Square(rook.getRow(), rook.getColumn() + 3);
        }
        this.castleKing = new Move(king, endKing, b, color);
        this.castleRook = new Move(rook, endRook, b, color);
    }

    public boolean isCastling() {
        return castling;
    }

    public boolean isLegalMove() {
        /*
        if (this.piece == null) {
            System.out.println("no piece on the start square");
        } else if (this.color != this.piece.getColor()) {
            if (this.color) {
                System.out.println("it's white's turn");
            } else {
                System.out.println("it's black's turn");
            }
        }

        if (this.isCapture() && (!this.capture.getColor() ^ (this.piece.getColor()))) {
            System.out.println("can't capture a piece with the same color");
        }
       //*/
        boolean isAMove = false;
        List<Move> l = this.piece.allLegalMoves(this.b.copy(), true);
        for (Move m : l) {
            if (this.equal(m)) {
                isAMove = true;
            }
        }
        if (!isAMove) {
            System.out.println("not a legal move");
        }
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

    public Move getCastleKing() {
        return castleKing;
    }

    public Move getCastleRook() {
        return castleRook;
    }

    public boolean isDoublePawnPush() {
        return this.doublePawnPush;
    }

    public boolean isCapture() {
        this.capture = this.b.getPiece(this.end);
        return capture != null;
    }

    public Piece getCapture() {
        this.capture = this.b.getPiece(this.end);
        return this.capture;
    }

    public boolean equal(Move m) {
        return this.b.equal(m.getBoard()) && this.start.equal(m.getStart()) && this.end.equal(m.getEnd());
    }

    public String toString() {
        if (this.isCastling()) {
            if (isKingSide) {
                return "O-O";
            }
            return "O-O-O";
        }
        //check for doubles pieces that can take
        //check for checks and promotion
        if (this.isCapture()) {
            return this.piece + "x" + this.end;
        }
        return this.piece + "" + this.end;
    }
}
