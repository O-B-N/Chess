import java.util.ArrayList;
import java.util.List;

abstract class Piece {
    protected boolean color;
    protected char type;
    protected Square s;

    public Piece(boolean color, char type, Square s) {
        this.color = color;
        this.type = type;
        this.s = s;
    }

    public boolean equal(Piece p) {
        return (this.color == p.getColor() && this.type == p.getType() && this.s.equal(p.getSquare()));
    }

    public Square getSquare() {
        return this.s;
    }

    public abstract List<Move> allLegalMoves(Board b, boolean checkForChecks);

    public abstract Piece copy();

    public char getType() {
        return type;
    }

    public boolean getColor() {
        return color;
    }

    public boolean sameColor(Piece p) {
        return p.getColor() ^ this.color;
    }

    public void move(Square s) {
        this.s = s;
    }

    public List<Move> allStraightMoves(Board b, boolean checkForChecks) {
        List<Move> l = this.straightMoves(b, true, true, checkForChecks);
        l.addAll(this.straightMoves(b, true, false, checkForChecks));
        l.addAll(this.straightMoves(b, false, true, checkForChecks));
        l.addAll(this.straightMoves(b, false, false, checkForChecks));
        return l;
    }


    public List<Move> straightMoves(Board b, boolean row, boolean increase, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        Move m;
        List<Move> l = new ArrayList<>();
        int i = s.getRow();
        int j = s.getColumn();
        Piece pTemp = null;
        int x;
        if (row) {
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
        while (x >= 0 && x <= 7 && pTemp == null) {
            pTemp = a[i][j];
            if (pTemp == null || pTemp.getColor() != this.color) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (m.isLegalMove() && (!checkForChecks || !m.isCheck())) {
                    l.add(m);
                }
            }
            if (row) {
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
        return l;
    }

    public List<Move> allDiagonalMoves(Board b, boolean checkForChecks) {
        List<Move> l = this.diagonalMoves(b, true, true, checkForChecks);
        l.addAll(this.diagonalMoves(b, true, false, checkForChecks));
        l.addAll(this.diagonalMoves(b, false, true, checkForChecks));
        l.addAll(this.diagonalMoves(b, false, false, checkForChecks));
        return l;
    }

    public List<Move> diagonalMoves(Board b, boolean up, boolean right, boolean checkForChecks) {
        Piece[][] a = b.getArray();
        Square s = this.getSquare();
        List<Move> l = new ArrayList<>();
        Move m;
        int i = s.getRow();
        int j = s.getColumn();
        Piece pTemp = null;
        while (i >= 0 && i <= 7 && j >= 0 && j <= 7 && pTemp == null) {
            pTemp = a[i][j];
            if (pTemp == null || pTemp.getColor() != this.color) {
                m = new Move(this.s, new Square(i, j), b, this.color);
                if (m.isLegalMove() && (!checkForChecks || !m.isCheck())) {
                    l.add(m);
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
        return l;
    }
}
