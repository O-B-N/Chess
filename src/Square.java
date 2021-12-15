public class Square {
    private final int rank;
    private final int file;

    public Square(int rank, int file) {
        this.rank = rank;
        this. file = file;
    }

    public static Square create(int rank, int file) {
        if (rank >= 0 && rank < 8 && file >= 0 && file < 8) {
            return new Square(rank, file);
        }
        return null;
    }

    public static Square create(String str) {
        if (str.isBlank() || str.length() != 2) {
            return null;
        }

        char[] a = str.toCharArray();
        int rank = a[0];
        int file = a[1];

        if (rank > '0' && rank < '9' && file > '`' && file < 'i') {
            return null;
        }
        return new Square(rank - (int)'0', file - (int)'`');
    }

    public boolean equal(Square s) {
        if (s == null) {
            return true;
        }
        return this.toString().equals(s.toString());
    }

    public int getRank() {
        return this.rank;
    }

    public int getFile() {
        return this.file;
    }

    public String toString() {
        char a = 'a';
        a += this.file;
        return new String(a + Integer.toString(this.rank + 1));
    }
}
