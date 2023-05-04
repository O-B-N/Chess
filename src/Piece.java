import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * an abstract class for all the pieces played on the board
 */
abstract class Piece {
    protected boolean color;
    protected char type;
    protected Square s;
    protected int value;
    protected boolean moved = false;


    /**
     *  creates a piece
     * @param color the color of the piece
     * @param type the type of the piece
     * @param s the square of the piece
     * @param value piece value
     */
    public Piece(boolean color, char type, Square s, int value) {
        this.color = color;
        if (!color) {
            String r = (type + "").toLowerCase(Locale.ROOT);
            type = r.toCharArray()[0];
        }
        this.type = type;
        this.s = s;
        this.value = value;
    }

    /**
     * check if two pieces are equal
     * @param p piece to compare
     * @return true if pieces are the save type and same color and square?
     */
    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.s.equal(p.getSquare()));
    }

    /**
     *
     * @return a square that the piece is on
     */
    public Square getSquare() {
        return this.s;
    }

    /**
     *
     * @param b a board to check in
     * @param checkForChecks
     * @return a list of all legal moves
     */
    public abstract List<Move> allLegalMoves(Board b, boolean checkForChecks);

    /**
     *
     * @return a copy of the piece
     * maybe can be shared q b n?
     */
    public abstract Piece copy();

    /**
     *
     * @return the type of the piece
     */
    public char getType() {
        return type;
    }

    /**
     *
     * @return the color of the piece
     */
    public boolean getColor() {
        return color;
    }

    /**
     *
     * @param p a piece to compare
     * @return true if the pieces are the same color
     */
    public boolean sameColor(Piece p) {
        return p.getColor() == this.color;
    }

    /**
     *
     * @param s the square to move to
     * @return true if there is an issue
     */
    public int move(Square s) {
     //   System.out.println(this + " updated to: " + s);
        this.s = s;
        return 0;
    }

    /**
     *
     * @param b on the board bv
     * @param checkForChecks
     * @return a list of all the legal straight moves
     */
    public List<Move> allStraightMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.straightMoves(b, true, true, checkForChecks);
        moves.addAll(this.straightMoves(b, true, false, checkForChecks));
        moves.addAll(this.straightMoves(b, false, true, checkForChecks));
        moves.addAll(this.straightMoves(b, false, false, checkForChecks));
        return moves;
    }

    /**
     *
     * @param b on the board b
     * @param isRank if the axis is y
     * @param increase if going up the axis
     * @param checkForChecks
     * @return a list of moves in the specified direction
     */
    public List<Move> straightMoves(Board b, boolean isRank, boolean increase, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        Move m;
        List<Move> moves = new ArrayList<>();
        int i = s.getRank();
        int j = s.getFile();
        Piece pTemp = null;
        int x;
        if (isRank) {
            if (increase) {
                i++;
            } else {
                i--;
            }
            x = i;
        } else {
            if (increase) {
                j++;
            } else {
                j--;
            }
            x = j;
        }
        while (x >= 0 && x < 8 && pTemp == null) {
            pTemp = a[i][j];
            if (pTemp == null || pTemp.getColor() != this.color) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (!checkForChecks || !m.isInCheck(this.color)) {
                    moves.add(m);
                }
            }
            if (isRank) {
                if (increase) {
                    i++;
                } else {
                    i--;
                }
                x = i;
            } else {
                if (increase) {
                    j++;
                } else {
                    j--;
                }
                x = j;
            }
        }
        return moves;
    }

    /**
     *
     * @param b board to check moves on
     * @param checkForChecks if true
     * @return a list of all diagonal moves
     */
    public List<Move> allDiagonalMoves(Board b, boolean checkForChecks) {
        List<Move> moves = this.diagonalMoves(b, true, true, checkForChecks);
        moves.addAll(this.diagonalMoves(b, true, false, checkForChecks));
        moves.addAll(this.diagonalMoves(b, false, true, checkForChecks));
        moves.addAll(this.diagonalMoves(b, false, false, checkForChecks));
        return moves;
    }

    /**
     *
     * @param b board to check moves on
     * @param checkForChecks if true
     * @return a all diagonal move in the specified direction
     */
    public List<Move> diagonalMoves(Board b, boolean up, boolean right, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        List<Move> moves = new ArrayList<>();
        Move m;
        int i = s.getRank();
        int j = s.getFile();

        if (up) {
            i++;
        } else {
            i--;
        }
        if (right) {
            j++;
        } else {
            j--;
        }
        Piece pTemp = null;
        while (i >= 0 && i < 8 && j >= 0 && j < 8 && pTemp == null) {
            pTemp = a[i][j];
            if (pTemp == null || !pTemp.sameColor(this)) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (!checkForChecks || !m.isInCheck(this.color)) {
                    moves.add(m);
                }
            }
                if (up) {
                    i++;
                } else {
                    i--;
                }
                if (right) {
                    j++;
                } else {
                    j--;
                }
            }
        return moves;
    }

    /**
     *
     * @return the piece as a string
     */
    public String toString() {
        return this.type + "";
    }

    public boolean wasMoved() {
        return this.moved;
    }
}
