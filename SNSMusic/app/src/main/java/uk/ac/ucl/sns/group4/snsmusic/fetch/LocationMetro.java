package uk.ac.ucl.sns.group4.snsmusic.fetch;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dangweiyi on 08/01/2015.
 */
public class LocationMetro {
    private static final String TAG = "LocationAddress";

    public static ArrayList <String> getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        ArrayList <String> result = new ArrayList<String>();
            try {
                List<Address> addressList = geocoder.getFromLocation(
                        latitude, longitude, 1);
                if (addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    result.add(address.getCountryName());
                    result.add(address.getLocality());
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable connect to Geocoder", e);
        }
        return result;
    }
}


