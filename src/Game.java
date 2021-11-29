import java.util.ArrayList;
import java.util.List;

public class Game {

    public void Main(String[] args) {
        Board b = Board.newGame();
        List<Move> l = new ArrayList<>();
        for (Move m : l) {
            if (m == null) {
                System.out.println("asd");
            }
        }
        System.out.println("adsasd");
     //   while (!b.isGameOver()) {
     //       //is game over? mate, stalemate, repetition
//
     //   }
    }
}
