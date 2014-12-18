package uk.ac.ucl.sns.group4.snsmusic;

import android.graphics.drawable.BitmapDrawable;

import java.net.URL;

/**
 * Created by andi on 18/12/2014.
 */
public class Artist {
    private String mbid,name;
    private URL url;
    private BitmapDrawable image;

    public String getMbid() {
        return mbid;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public BitmapDrawable getImage() {
        return image;
    }

    public void setImage(BitmapDrawable image) {
        this.image = image;
    }
}
