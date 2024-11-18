import java.util.ArrayList;
import java.util.List;

/**
 * a class to create playable moves on the chessboard
 */
public class Move {
    private Board boardCopy;
    private Square start;
    private Square end;
    private Piece piece;
    private Piece capture;
    private Piece promoted;
    private final boolean doublePawnPush;
    private boolean enPassant = false;
    private boolean castling = false;
    private boolean isKingSide = false;
    private boolean promotion = false;
    private Move castleKing = null;
    private Move castleRook = null;

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     */
    public Move(Square start, Square end, Board b) {
        this.boardCopy = b.copy();
        if (end == null || start == null) {
            System.exit(3);
        }
        this.piece = b.getPiece(start);
        //System.out.println(start + end.toString() + this.piece);
        this.capture = boardCopy.getPiece(end);
        if (this.piece == null) {
         //   this.boardCopy.printBoard();
          //  System.out.println("no piece in move creation");
        }
        this.start = start;
        this.end = end;
        this.doublePawnPush = this.piece instanceof Pawn && Math.abs(start.getRank() - end.getRank()) == 2;
    }

    /**
     *
     * @param kingSide if true, queen's side if false
     * @param b the board played on
     * @param king king's piece
     * @param rook rook's piece
     * @return the castling move
     */
    public static Move createCastlingMove(boolean kingSide, Board b, Square king, Square rook) {
        Square end = kingSide ? new Square(king.getRank(), 6) : new Square(king.getRank(),2);
        Move m = new Move(king, end, b);
        m.castling = true;
        m.isKingSide = kingSide;
        Square endRook, endKing;
        if (kingSide) {
            endKing = new Square(king.getRank(), 6);
            endRook = new Square(rook.getRank(), 5);
        } else {
            endKing = new Square(king.getRank(), 2);
            endRook = new Square(rook.getRank(), 3);
        }
        m.castleKing = new Move(king, endKing, b);
        m.castleRook = new Move(rook, endRook, b);
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
    public static List<Move> createPromotionMoves(Square start, Square end, Board b, boolean color) {
        List<Move> moves = new ArrayList<>();
        Move m = new Move(start, end, b);
        m.promotion = true;
        m.promoted = new Knight(color);
        moves.add(m);
        m = new Move(start, end, b);
        m.promotion = true;
        m.promoted = new Queen(color);
        moves.add(m);
        m = new Move(start, end, b);
        m.promotion = true;
        m.promoted = new Bishop(color);
        moves.add(m);
        m = new Move(start, end, b);
        m.promotion = true;
        m.promoted = new Rook(color);
        moves.add(m);
        return moves;
    }

    /**
     *
     * @param start starting square
     * @param end ending square
     * @param b the board played on
     * @return the move en passant move
     */
    public static Move createEnPassantMove(Square start, Square end, Board b) {
        Move m = new Move(start, end, b);
        m.enPassant = true;
        m.capture = b.getPiece(new Square(m.end.getRank() + (b.getColor() ? 1 : -1), m.end.getFile()));
        return m;
    }

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
        boolean isAMove = false;
        List<Move> moves = this.piece.allLegalMoves(boardCopy.copy(), this.start, true);
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

    /**
     * if the king is in check after the move played
     * @param color the color of the move's side
     * @return true is the move is checking the king
     */
    public boolean isInCheck(boolean color) {
        Board copy = boardCopy.copy();
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
        return boardCopy;
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
        return this.capture != null;
    }

    /**
     *
     * @return the piece making the move
     */
    public Piece getPiece() {
        return this.piece;
    }

    /**
     *
     * @return the captured piece
     */
    public Piece getCapture() {
        return this.capture;
    }

    /**
     *
     * @param m a move to compare to
     * @return true if the moves are equal
     */
    public boolean equal(Move m) {
        return boardCopy.equal(m.getBoard()) && this.start.equals(m.getStart()) && this.end.equals(m.getEnd());
    }

    /**
     * @param l list of all the legal moves
     * @return the move as a string
     */
    public String toString(List<Move> l) {
        Piece piece = this.piece;
        String check = "";
        if (this.isInCheck(!piece.getColor())) {
            check = "+";
            Board copy = this.boardCopy.copy();
            copy.makeMove(this);
            if (copy.isCheckmate()) {
                check = "#";
            } else if (copy.isStalemate()) {
                check = "$";
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
        String pieceString = piece.toString();
        boolean sameRank = false, sameFile = false, ambiguous = false;
        //determine if the move is ambiguous
        for (Move m : l) {
            if (m.getEnd().equals(this.end) && m.getPiece().getClass() == this.piece.getClass() && !m.getStart().equals(this.getStart())) {
                ambiguous = true;
                if (m.getStart().getFile() == this.start.getFile()) {
                    sameFile = true;
                }
                if (m.getStart().getRank() == this.start.getRank()) {
                    sameRank = true;
                }
            }
        }
        if (ambiguous) {
            System.out.println("a");
            if (!sameFile) {
                System.out.println("b");
                pieceString = pieceString + this.start.toString().charAt(0);
            } else if (!sameRank) {
                System.out.println("c");
                pieceString = pieceString + this.start.toString().charAt(1);
                System.out.println(pieceString);
            } else {
                System.out.println("d");
                pieceString = pieceString + this.start.toString();
            }
        }
        if (this.isCapture()) {
            String pawn = this.start.toString().toCharArray()[0] + "";
            return ((piece instanceof Pawn ? pawn : pieceString) + "x" + this.end.toString() + end);
        } else if (piece instanceof Pawn) {
            return (this.end.toString() + end);
        }
        return (pieceString + "" + this.end.toString() + end);
    }

    /**
     *
     * @return the move as a string
     */
    @Override
    public String toString() {
        Piece piece = this.piece;
        String check = "";
        if (this.isInCheck(!piece.getColor())) {
            check = "+";
            Board copy = this.boardCopy.copy();
            copy.makeMove(this);
            if (copy.isCheckmate()) {
                check = "#";
            } else if (copy.isStalemate()) {
                check = "$";
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
        String pieceString = piece.toString();
        boolean sameRank = false, sameFile = false, ambiguous = false;
        //determine if the move is ambiguous
        for (Move m : this.boardCopy.allMoves(boardCopy.getColor(), true)) {
            if (m.getEnd().equals(this.end) && m.getPiece().getClass() == this.piece.getClass() && !m.getStart().equals(this.getStart())) {
                ambiguous = true;
                if (m.getStart().getFile() == this.start.getFile()) {
                    sameFile = true;
                }
                if (m.getStart().getRank() == this.start.getRank()) {
                    sameRank = true;
                }
            }
        }
        if (ambiguous) {
            System.out.println("a");
            if (!sameFile) {
                System.out.println("b");
                pieceString = pieceString + this.start.toString().charAt(0);
            } else if (!sameRank) {
                System.out.println("c");
                pieceString = pieceString + this.start.toString().charAt(1);
                System.out.println(pieceString);
            } else {
                System.out.println("d");
                pieceString = pieceString + this.start.toString();
            }
        }
        if (this.isCapture()) {
            String pawn = this.start.toString().toCharArray()[0] + "";
            return ((piece instanceof Pawn ? pawn : pieceString) + "x" + this.end.toString() + end);
        } else if (piece instanceof Pawn) {
            return (this.end.toString() + end);
        }
        return (pieceString + "" + this.end.toString() + end);
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