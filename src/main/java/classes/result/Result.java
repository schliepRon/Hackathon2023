package classes.result;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Result implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("players")
    @Expose
    private List<ResultPlayer> players = null;
    @SerializedName("boards")
    @Expose
    private List<List<List<String>>> boards = null;
//
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("self")
    @Expose
    private String self;
    private final static long serialVersionUID = 8243041135500189102L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ResultPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<ResultPlayer> players) {
        this.players = players;
    }


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

    public List<List<List<String>>> getBoards() {
        return boards;
    }

    public void setBoards(List<List<List<String>>> boards) {
        this.boards = boards;
    }


}