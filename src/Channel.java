import java.awt.*;

public class Channel {
    private  String channelName;
    private String schemaURL;
    private int id;
    private String imageURL;
    private Color color;

    public String getChannelName() {
        return channelName;
    }

    public String getSchemaURL() {
        return schemaURL;
    }

    public int getId() {
        return id;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setSchemaURL(String schemaURL) {
        this.schemaURL = schemaURL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Color getColor() {
        return color;
    }

    public Channel(String channelName, int id, String schemaURL, String imageURL){
        this.channelName = channelName;
        this.id = id;
        this.schemaURL = schemaURL;
        this.imageURL = imageURL;
    }
}
