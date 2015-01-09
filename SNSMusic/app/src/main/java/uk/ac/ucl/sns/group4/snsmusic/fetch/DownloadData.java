package uk.ac.ucl.sns.group4.snsmusic.fetch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *  To fetch data from URL, mainly for:
 *      - Last FM API JSON Object
 *      - Image from Last FM resource
 *
 *  Originally part of DownloadGeoMetroTrackChart by Wahyudi
 *  but separated for further reuse - Andi
 *
 *
 */
public class DownloadData {

    private static String API_KEY = "8a5f74ff83a28ec070f9ec283723ba79"; // Place Last FM API key here

    // Download data byte particularly for compatibility with image download
    byte[] getUrlBytes(String urlSpec) throws IOException {
        //Prepare URL
        URL url = new URL(urlSpec);

        //Open connection
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        // Read data as Byte array and watch if HTTP is OK
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[65535];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    // Download data in String particularly for JSON
    public String getUrl(String urlSpec) throws IOException {

        return new String(getUrlBytes(urlSpec+"&format=json&api_key="+API_KEY));
    }

    // Download data in String particularly for JSON with pagination
    public String getUrl(String urlSpec,int pageSize,int page) throws IOException {

        return new String(getUrl(urlSpec+"&limit="+pageSize+"&page="+page));
    }
}
