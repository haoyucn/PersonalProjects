package communication;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jalton on 10/24/18.
 */

public class Message {
    private String playerID;
    private String text;

    public Message(String playerID) {
        this.playerID = playerID;
        text = "";
    }

    public Message(String playerID, String text) {
        this.playerID = playerID;
        try{
            this.text = URLEncoder.encode(text,"UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.text = text;
        }

    }

    public String getPlayerID() {
        return playerID;
    }

    public String getText() {
        URLDecoder decoder = new URLDecoder();
        try {
            return decoder.decode(this.text, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this.text;
    }

    public void setText(String text) {
        try{
            this.text = URLEncoder.encode(text,"UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            this.text = text;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (playerID != null ? !playerID.equals(message.playerID) : message.playerID != null)
            return false;
        return text != null ? text.equals(message.text) : message.text == null;
    }

    @Override
    public int hashCode() {
        int result = playerID != null ? playerID.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }
}
