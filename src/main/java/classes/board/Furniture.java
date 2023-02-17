package classes.board;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Furniture {


    @SerializedName("start")
    @Expose
    private int[] start;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("size")
    @Expose
    private Integer size;

    public Furniture(Integer size) {
        this.start = new int[2];
        this.direction = "h";
        this.size = size;
    }

    public Furniture(int[] start, String direction, Integer size) {
        this.start = start;
        this.direction = direction;
        this.size = size;
    }

    public int[] getStart() {
        return start;
    }

    public void setStart(int[] start) {
        this.start = start;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Furniture furniture = (Furniture) o;

        if (!Arrays.equals(start, furniture.start)) {
            return false;
        }
        if (direction != furniture.direction) {
            return false;
        }
        return size != null ? size.equals(furniture.size) : furniture.size == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(start);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }
}
