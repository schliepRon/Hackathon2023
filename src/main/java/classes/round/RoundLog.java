package classes.round;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RoundLog implements Serializable
{

    @SerializedName("player")
    @Expose
    private String player;
    @SerializedName("move")
    @Expose
    private int[][] move = null;

    private final static long serialVersionUID = 5086497827702696257L;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public int[][] getMove() {
        return move;
    }

    public void setMove(int[][] move) {
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoundLog roundLog = (RoundLog) o;

        if (player != null ? !player.equals(roundLog.player) : roundLog.player != null) {
            return false;
        }
        if (!Arrays.deepEquals(move, roundLog.move)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = player != null ? player.hashCode() : 0;
        result = 31 * result + Arrays.deepHashCode(move);
        return result;
    }
}