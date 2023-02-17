package classes.result;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class ResultLog implements Serializable
{

    @SerializedName("round")
    @Expose
    private Integer round;
    @SerializedName("results")
    @Expose
    private List<String> results = null;
    @SerializedName("rating")
    @Expose
    private List<Integer> rating = null;
    private final static long serialVersionUID = 5086497827702696257L;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public void setRating(List<Integer> rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ResultLog.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("round");
        sb.append('=');
        sb.append(((this.round == null)?"<null>":this.round));
        sb.append(',');
        sb.append("results");
        sb.append('=');
        sb.append(((this.results == null)?"<null>":this.results));
        sb.append(',');
        sb.append("rating");
        sb.append('=');
        sb.append(((this.rating == null)?"<null>":this.rating));
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
        result = ((result* 31)+((this.rating == null)? 0 :this.rating.hashCode()));
        result = ((result* 31)+((this.round == null)? 0 :this.round.hashCode()));
        result = ((result* 31)+((this.results == null)? 0 :this.results.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ResultLog) == false) {
            return false;
        }
        ResultLog rhs = ((ResultLog) other);
        return ((((this.rating == rhs.rating)||((this.rating!= null)&&this.rating.equals(rhs.rating)))&&((this.round == rhs.round)||((this.round!= null)&&this.round.equals(rhs.round))))&&((this.results == rhs.results)||((this.results!= null)&&this.results.equals(rhs.results))));
    }

}
