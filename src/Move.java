import java.util.ArrayList;
import java.util.List;

public class Move {
    private int value = 0;
    private Square start;
    private Square end;
    private Piece piece;
    private Piece capture;
    private Board copyBoard;
    private boolean doublePawnPush = false;
    private boolean color;
    private boolean castling = false;
    private boolean isKingSide = false;
    private boolean promotion = false;
    private Piece promoted;
    private Move castleKing = null;
    private Move castleRook = null;
    private boolean enPassant = false;
    private Piece[][] a;

    public Move(Square start, Square end, Board b, boolean color) {
        createMove(start, end, b, color, false);
    }

    public Move(Square start, Square end, Board b, boolean color, boolean doubleMove) {
        createMove(start,end,b,color,doubleMove);
    }

    private void createMove(Square start, Square end, Board b, boolean color, boolean doubleMove) {
        this.copyBoard = b.copy();
        this.piece = copyBoard.getPiece(start);
     //   if (this.piece == null) {
     //       System.out.println("no piece in move creation");
     //       System.exit(1);
     //   }
        this.start = start;
        this.end = end;
      //  if (end == null) {
      //      System.out.println("null end move");
      //      System.out.println(start);
      //      System.exit(2);
      //  }
        this.doublePawnPush = doubleMove;
        this.color = color;
        isCapture();
        this.a = b.getArray();

    }

    public static Move createCastlingMove(boolean kingSide, Board b, boolean color, Square king, Square rook) {
        Square end = kingSide ? new Square(king.getRank(), 6) : new Square(king.getRank(),2);
        Move m = new Move(king, end, b, color);
        m.castling = true;
        System.out.println(end + " castling move");
        Square endRook, endKing;
        m.isKingSide = kingSide;
        if (kingSide) {
    //        System.out.println("doing king side");
            endKing = new Square(king.getRank(), king.getFile() + 2);
            endRook = new Square(rook.getRank(), rook.getFile() - 2);
        } else {
     //       System.out.println("doing queen side");
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
        m.promotion = true;
        m.promoted = new Knight(color, end);
        moves.add(m);
        m = new Move(start, end, b, color);
        m.promotion = true;
        m.promoted = new Queen(color, end);
        moves.add(m);
        m = new Move(start, end, b, color);
        m.promotion = true;
        m.promoted = new Bishop(color, end);
        moves.add(m);
        m = new Move(start, end, b, color);
        m.promotion = true;
        m.promoted = new Rook(color, end);
        moves.add(m);
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
        List<Move> moves = this.piece.allLegalMoves(copyBoard.copy(), true);
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

    public boolean isInCheck() {
        return isInCheck(false);
    }

    //**is IN check**
    public boolean isInCheck(boolean after) {
        Board copy = copyBoard.copy();
        copy.makeMove(this);
        boolean c = copy.getColor();
        if (after) {
            c = !c;
        }
        List<Move> moves = copy.allMoves(c, false);
        for (Move m : moves) {
            if (m != null && m.isCapture() && m.getCapture() instanceof King) {
                return true;
            }
        }
        return false;
    }

    public Square getStart() {
        return this.start;
    }

    public Square getEnd() {
        return this.end;
    }
    
    public Board getBoard() {
        return copyBoard;
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
        this.capture = copyBoard.getPiece(this.end);
        return this.capture != null;
    }

    public Piece getCapture() {
        this.capture = copyBoard.getPiece(this.end);
        return this.capture;
    }

    public boolean equal(Move m) {
        return copyBoard.equal(m.getBoard()) && this.start.equal(m.getStart()) && this.end.equal(m.getEnd());
    }

    public String toString() {
        //check, mate and stalemate
        Piece piece = this.piece;
        Board copy = this.copyBoard.copy();
        copy.makeMove(this);
        String check = "";
        if (isInCheck(true)) {
            check = "+";
            if (copy.isCheckmate()) {
                check = "#";
            }
        }
        if (copy.isCheckmate()) {
            check = "#";
        }
        String promotion = "";
        if (this.isCastling()) {
            if (isKingSide) {
                return "O-O" + check;
            }
            return "O-O-O" + check;
        }
        if (this.promotion) {
            promotion = "=" + this.promoted + check;
        }
        //check for doubles pieces from the same type that can take
        //check for checks and
        if (this.isCapture() || this.enPassant) {
            String pawn = piece.getSquare().toString().toCharArray()[0] + "";
            return ((piece instanceof Pawn ? pawn : piece) + "x" + this.end.toString() + promotion + check);
        } else if (piece instanceof Pawn) {
            return (this.end.toString() + promotion + check);
        }
        return (this.piece.toString() + "" + this.end.toString() + check);
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
