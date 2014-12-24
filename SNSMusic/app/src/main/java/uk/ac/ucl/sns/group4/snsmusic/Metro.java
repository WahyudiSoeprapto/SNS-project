package uk.ac.ucl.sns.group4.snsmusic;



import java.util.ArrayList;

/**
 * Country and Location container
 * Created by andi on 24/12/2014.
 */
public class Metro {
    String country;
    ArrayList<String> location;

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
