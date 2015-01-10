package uk.ac.ucl.sns.group4.snsmusic.fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Created by oljas on 01/01/15.
 */
public class LyricsParser {


    //get lyrics of song
    public static String getLyricsOfSong(String uri) {
        //
        String query = uri.replace(" ", "+").replace("é", "e").replace("&", "").replace("!", "");
        String url = "http://search.azlyrics.com/search.php?q=" + query;
        String error = "Sorry, lyrics not found :(";
        Document doc = null;
        //Getting page content from 'url' as a Document object
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //select all anchor tag elements with href attribute from <div class="sen">
        Elements links = doc.select("div.sen a[href]");
        if(links.size() == 0) {
            //if searching result list is empty, return an error message
            return error;
        }
        //get first "href" attribute
        String secondLink = links.get(0).attr("href");
        //Connecting to the link extracted, and getting its content as a Document object
        try {
            doc = Jsoup.connect(secondLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //select the <div> which is the child element of <div id="main">
        Elements body = doc.select("div#main div");

        String lyrics = "";
        //get the content of 5th <div> (excluding <div> element itself)
        List<Node> lyricNodes = body.get(4).childNodes();
        //removing of comments such as <!--start of lyrics--> and <!--end of lyrics-->
        // in the beginning and end of the content
        for (int i = 2; i < lyricNodes.size() - 2; i++) {
            Node node = lyricNodes.get(i);
            //converting all <br /> and <br> HTML tags to a new line "\n";
            if (lyricNodes.get(i).toString().equals("<br />") ||lyricNodes.get(i).toString().equals("<br>") ) {
                lyrics = lyrics + "\n";
                //replacing all tags <i> and </i> to empty line "";
            } else if (lyricNodes.get(i).toString().startsWith("<i>")) {
                lyrics = lyrics + lyricNodes.get(i).toString().replace("<i>", "").replace("</i>","");
            } else {
                lyrics = lyrics + lyricNodes.get(i);
            }


        }
        //if url link exists but gives wrong page displays error;
        if (lyrics.length() == 0) {
            lyrics = error;
        }
        return lyrics;
    }
}
