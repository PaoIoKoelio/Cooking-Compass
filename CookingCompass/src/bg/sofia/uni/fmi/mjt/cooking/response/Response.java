package bg.sofia.uni.fmi.mjt.cooking.response;

import bg.sofia.uni.fmi.mjt.cooking.response.hits.Hit;
import bg.sofia.uni.fmi.mjt.cooking.response.next.Links;
import com.google.gson.annotations.SerializedName;

import java.util.Collection;

public class Response {
    @SerializedName("_links")
    private Links links;
    private Collection<Hit> hits;

    public Collection<Hit> getHits() {
        return hits;
    }

    public Links getLinks() {
        return links;
    }
}

