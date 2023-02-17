package classes.init;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Init implements Serializable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("players")
    @Expose
    private List<InitPlayer> players = null;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("self")
    @Expose
    private String self;
    private final static long serialVersionUID = 6596580182474014168L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<InitPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<InitPlayer> players) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Init.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null)?"<null>":this.id));
        sb.append(',');
        sb.append("players");
        sb.append('=');
        sb.append(((this.players == null)?"<null>":this.players));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("self");
        sb.append('=');
        sb.append(((this.self == null)?"<null>":this.self));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.self == null)? 0 :this.self.hashCode()));
        result = ((result* 31)+((this.id == null)? 0 :this.id.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.players == null)? 0 :this.players.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Init) == false) {
            return false;
        }
        Init rhs = ((Init) other);
        return (((((this.self == rhs.self)||((this.self!= null)&&this.self.equals(rhs.self)))&&((this.id == rhs.id)||((this.id!= null)&&this.id.equals(rhs.id))))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))))&&((this.players == rhs.players)||((this.players!= null)&&this.players.equals(rhs.players))));
    }

}