public class Square {
    private final int row;
    private final int column;

    public Square(int i, int j) {
        this.row = i;
        this. column = j;
    }

    public static Square create(int i, int j) {
        if(i >= 0 && i < 8 && j >= 0 && j < 8) {
            return new Square(i, j);
        }
        return null;
    }

    public static Square create(String str) {
        if (str.isBlank() || str.length() != 2) {
            return null;
        }

        char[] a = str.toCharArray();
        char i = a[0];
        char j = a[1];

        if (i > '0' && i < '9' && j > '`' && j < 'i') {
            return null;
        }
        return new Square((int)i - (int)'0',(int)j - (int)'`');
    }

    public boolean equal(Square s) {
        return this.row == s.getRow() && this.column == s.getColumn();
    }

    public int getRow() {
        return this.row;
    }

    public int getColumn() {
        return this.column;
    }

    public String toString() {
       // return new String('a' + (this.column - '0') + this.row);
        return null;
    }
}
