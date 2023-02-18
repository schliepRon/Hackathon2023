import classes.Coordinate;
import classes.board.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<Coordinate> abst = new ArrayList<>();
        abst.add(new Coordinate(3,0));
        abst.add(new Coordinate(2,0));
        abst.add(new Coordinate(1,0));
        abst.add(new Coordinate(0,0));
        abst.sort(new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate abstaendeO1, Coordinate abstaendeO2) {
                if (abstaendeO1.compare(abstaendeO2, Direction.HORIZONTAL) != 0) {
                    return abstaendeO1.compare(abstaendeO2, Direction.HORIZONTAL);
                }
                return abstaendeO1.compare(abstaendeO2, Direction.VERTICAL);
            }
        });
        abst.forEach(ab -> {
            System.out.println(ab.toString());
        });

    }
}