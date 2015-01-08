package uk.ac.ucl.sns.group4.snsmusic.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.ac.ucl.sns.group4.snsmusic.DownloadData;

/**
 * Created by oljas on 05/01/15.
 */
public class LyricsDownloader {
    private String BASE_URL= "http://api.musixmatch.com/ws/1.1/";
    private String API_KEY = "9f2b5f27ed75e32dc64d0c4dc92a3bc0";

    public LyricsDownloader() {}
    public String downloadContent(String url) throws IOException {
        return new String(getUrlBytes(url));
    }

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
}
