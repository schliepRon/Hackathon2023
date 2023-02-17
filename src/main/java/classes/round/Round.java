package classes.round;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Round implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("players")
    @Expose
    private List<RoundPlayer> players = null;

    @SerializedName("boards")
    @Expose
    private List<List<List<String>>> boards = null;
//    @SerializedName("log")
//    @Expose
//    private List<RoundLog> log = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("self")
    @Expose
    private String self;
    private final static long serialVersionUID = -585078663587995582L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RoundPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<RoundPlayer> players) {
        this.players = players;
    }

    public List<List<List<String>>> getBoards() {
        return boards;
    }

    public void setBoards(List<List<List<String>>> boards) {
        this.boards = boards;
    }

//    public List<RoundLog> getLog() {
//        return log;
//    }
//
//    public void setLog(List<RoundLog> log) {
//        this.log = log;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Round) == false) {
            return false;
        }

        return false;
    }

}