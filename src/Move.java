import java.util.ArrayList;
import java.util.List;

public class Move {
    private int value = 0;
    private Square start;
    private Square end;
    private Piece piece;
    private Piece capture;
    private Board b;
    private boolean doublePawnPush = false;
    private final boolean color;
    private boolean castling = false;
    private boolean isKingSide = false;
    private boolean promotion = false;
    private Piece promoted;
    private Move castleKing = null;
    private Move castleRook = null;
    private boolean enPassant = false;

    public Move(Square start, Square end, Board b, boolean color) {
        this.b = b;
        this.piece = this.b.getPiece(start.getRank(), start.getFile());
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public static Move createDoublePawnMove(Square start, Square end, Board b, boolean color) {
        Move m = new Move(start, end, b, color);
        m.doublePawnPush = true;
        return m;
    }

    public static Move createCastlingMove(boolean kingSide, Board b, boolean color, Square king, Square rook) {
        Move m = new Move(null, null, b, color);
        m.castling = true;
        Square endRook, endKing;
        m.isKingSide = kingSide;
        if (kingSide) {
            endKing = new Square(king.getRank(), king.getFile() + 2);
            endRook = new Square(rook.getRank(), rook.getFile() - 2);
        } else {
            endKing = new Square(king.getRank(), king.getFile() - 2);
            endRook = new Square(rook.getRank(), rook.getFile() + 3);
        }
        m.castleKing = new Move(king, endKing, b, color);
        m.castleRook = new Move(rook, endRook, b, color);
        return m;

    }

    public static List<Move> createPromotionMove(Square start, Square end, Board b, boolean color) {
        List<Move> moves = new ArrayList<>();
        Move m = new Move(start, end, b, color);
        moves.add(m);
        m.promotion = true;
        m.promoted = new Knight(color, end);
        m = new Move(start, end, b, color);
        moves.add(m);
        m.promotion = true;
        m.promoted = new Queen(color, end);
        m = new Move(start, end, b, color);
        moves.add(m);
        m.promotion = true;
        m.promoted = new Bishop(color, end);
        m = new Move(start, end, b, color);
        moves.add(m);
        m.promotion = true;
        m.promoted = new Rook(color, end);
        m = new Move(start, end, b, color);
        moves.add(m);
        m.promotion = true;
        m.promoted = new Rook(color, end);
        return moves;
    }

    public static Move createEnPassantMove(Square start, Square end, Board b, boolean color) {
        Move m = new Move(start, end, b, color);
        m.enPassant = true;
        return m;

    }

 //  //change to static create castleMove
 //  public Move (boolean kingSide, Board b, boolean color, Square king, Square rook) {
 //      this.castling = true;
 //      Square endRook, endKing;
 //      this.isKingSide = kingSide;
 //      if (kingSide) {
 //          endKing = new Square(king.getRank(), king.getFile() + 2);
 //          endRook = new Square(rook.getRank(), rook.getFile() - 2);
 //      } else {
 //          endKing = new Square(king.getRank(), king.getFile() - 2);
 //          endRook = new Square(rook.getRank(), rook.getFile() + 3);
 //      }
 //      this.castleKing = new Move(king, endKing, b, color);
 //      this.castleRook = new Move(rook, endRook, b, color);
 //  }

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
        List<Move> moves = this.piece.allLegalMoves(this.b.copy(), true);
        for (Move m : moves) {
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
        List<Move> moves = b.allMoves(!this.color, false);
        int index = 0;
        Move temp = moves.get(index);
        while (temp != null && temp.isCapture() && temp.getCapture().getType() != 'K') {
            index++;
            temp = moves.get(index);
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
        //check for doubles pieces from the same type that can take
        //check for checks and promotion
        if (this.isCapture()) {
            return this.piece + "x" + this.end;
        }
        return this.piece + "" + this.end;
    }

    public boolean isPromotion() {
        return this.promotion;
    }

    public Piece getPromoted() {
        return this.promoted;
    }

    public boolean isEnPassant() {
        return this.enPassant;
    }
}
