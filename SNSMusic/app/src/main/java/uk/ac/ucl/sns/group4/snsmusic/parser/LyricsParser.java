package uk.ac.ucl.sns.group4.snsmusic.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import uk.ac.ucl.sns.group4.snsmusic.model.Lyrics;

/**
 * Created by oljas on 05/01/15.
 */
public class LyricsParser {



    public static String getLinkOfSong(String uri) {
        String query = uri.replace(" ", "+").replace("é", "e");
        String url = "http://search.azlyrics.com/search.php?q=" + query;
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements links = doc.select("div.sen a[href]");
        if(links.size() > 0) {

        } else {
            String error = "Sorry, lyrics not found :(";
            return error;
        }
        String secondLink = links.get(0).attr("href");

        try {
            doc = Jsoup.connect(secondLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements body = doc.select("div#main div");

        String lyrics = "";
        List<Node> lyricNodes = body.get(4).childNodes();
        for (int i = 2; i < lyricNodes.size() - 2; i++) {
            Node node = lyricNodes.get(i);

            if (lyricNodes.get(i).toString().equals("<br />")) {
                lyrics = lyrics + "\n";
            } else if (lyricNodes.get(i).toString().startsWith("<i")) {
                lyrics = lyrics + "";
            } /*else if (lyricNodes.get(i).toString().equals("\"")) { need to replace &quote
                lyrics = lyrics + " ";
            }*/ else {
                lyrics = lyrics + lyricNodes.get(i);
            }


        }



        return lyrics;


    }
}
    /**public String parseJsonLyrics() {

        try {
            String content = "";
            JSONObject reader = new JSONObject(content);
            JSONObject message = reader.getJSONObject("message");
            JSONObject body = message.getJSONObject("body");
            JSONObject lyrics = body.getJSONObject("lyrics");
            String lyricsBody = lyrics.getString("lyrics_body");

            String lbody = new JSONObject(content).getJSONObject("message")
                    .getJSONObject("body").getJSONObject("lyrics").getString("lyrics_body");
            return lbody;
        } catch (JSONException e) {
            e.printStackTrace();
            return "Could not parse JSON";
        }

    }

}*/
