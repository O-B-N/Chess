import java.util.ArrayList;
import java.util.List;

/**
 * a class to represent playable moves on the chessboard
 */
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

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @param color the color of the side playing
     */
    public Move(Square start, Square end, Board b, boolean color) {
        createMove(start, end, b, color, false);
    }

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @param color the color of the side playing
     * @param doubleMove if there is a double move
     */
    public Move(Square start, Square end, Board b, boolean color, boolean doubleMove) {
        createMove(start,end,b,color,doubleMove);
    }

    /**
     * a function to create moves
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @param color the color of the side playing
     * @param doubleMove if there is a double move
     */
    private void createMove(Square start, Square end, Board b, boolean color, boolean doubleMove) {
        this.copyBoard = b.copy();
        this.piece = b.getPiece(start);
        if (this.piece == null) {
            this.copyBoard.printBoard();
            System.out.println("no piece in move creation");
            System.out.println(start + " " + end);
            System.exit(1);
        }
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

    /**
     *
     * @param kingSide if true, queen's side if false
     * @param b the board played on
     * @param color the color of the side playing
     * @param king king's piece
     * @param rook rook's piece
     * @return the castling move
     */
    public static Move createCastlingMove(boolean kingSide, Board b, boolean color, Square king, Square rook) {
        Square end = kingSide ? new Square(king.getRank(), 6) : new Square(king.getRank(),2);
        Move m = new Move(king, end, b, color);
        m.castling = true;
       // System.out.println(end + " castling move");
        Square endRook, endKing;
        m.isKingSide = kingSide;
        if (kingSide) {
    //        System.out.println("doing king side");
            endKing = new Square(king.getRank(), 6);
            endRook = new Square(rook.getRank(), 5);
        } else {
     //       System.out.println("doing queen side");
            endKing = new Square(king.getRank(), 2);
            endRook = new Square(rook.getRank(), 3);
        }
        m.castleKing = new Move(king, endKing, b, color);
        m.castleRook = new Move(rook, endRook, b, color);
        return m;

    }

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @param color the color of the side playing
     * @return the list of promotion moves
     */
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

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @param color the color of the side playing
     * @return the move en passant move
     */
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

    /**
     *
     * @return true if the move is castling
     */
    public boolean isCastling() {
        return castling;
    }

    /**
     *
     * @return true is the move is legal
     */
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

    //**is IN check** after the move

    /**
     *
     * @param color the color of the move's side
     * @return true is the move is checking the king
     */
    public boolean isInCheck(boolean color) {
        Board copy = copyBoard.copy();
        copy.makeMove(this);
        return copy.inCheck(color);
    }

    /**
     *
     * @return the stating square of the move
     */
    public Square getStart() {
        return this.start;
    }

    /**
     *
     * @return the ending square of the move
     */
    public Square getEnd() {
        return this.end;
    }

    /**
     *
     * @return the board the move is played on
     */
    public Board getBoard() {
        return copyBoard;
    }

    /**
     *
     * @return the king move of the castling
     */
    public Move getCastleKing() {
        return castleKing;
    }

    /**
     *
     * @return the rook move of the castling
     */
    public Move getCastleRook() {
        return castleRook;
    }

    /**
     *
     * @return true if the move is a double pawn push
     */
    public boolean isDoublePawnPush() {
        return this.doublePawnPush;
    }

    /**
     *
     * @return true if the move is capture
     */
    public boolean isCapture() {
        this.capture = copyBoard.getPiece(this.end);
        return this.capture != null;
    }

    /**
     *
     * @return the captured piece
     */
    public Piece getCapture() {
        this.capture = copyBoard.getPiece(this.end);
        return this.capture;
    }

    /**
     *
     * @param m a move to compare to
     * @return true if the moves are equal
     */
    public boolean equal(Move m) {
        return copyBoard.equal(m.getBoard()) && this.start.equal(m.getStart()) && this.end.equal(m.getEnd());
    }

    /**
     *
     * @return the move as a string
     */
    public String toString() {
        //check, mate and stalemate
        Piece piece = this.piece;
        String check = "";
        if (this.isInCheck(!piece.getColor())) {
            check = "+";
            System.out.println("her1!");
            Board copy = this.copyBoard.copy();
            copy.makeMove(this);
            if (copy.isCheckmate()) {
                check = "#";
            }
        }
        String end = check;
        if (this.isCastling()) {
            if (isKingSide) {
                return "O-O" + check;
            }
            return "O-O-O" + check;
        }


        if (this.promotion) {
            end = "=" + this.promoted + check;
        }

        //check for doubles pieces from the same type that can take
        //check for checks and
        if (this.isCapture() || this.enPassant) {
            String pawn = piece.getSquare().toString().toCharArray()[0] + "";
            return ((piece instanceof Pawn ? pawn : piece) + "x" + this.end.toString() + end);
        } else if (piece instanceof Pawn) {
            return (this.end.toString() + end);
        }
        return (this.piece.toString() + "" + this.end.toString() + end);
    }

    /**
     *
     * @return true of the move is promotion
     */
    public boolean isPromotion() {
        return this.promotion;
    }

    /**
     *
     * @return the piece promoted to
     */
    public Piece getPromoted() {
        return this.promoted;
    }

    /**
     *
     * @return true if the move is en passant
     */
    public boolean isEnPassant() {
        return this.enPassant;
    }
}
