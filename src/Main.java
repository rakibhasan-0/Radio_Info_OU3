import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.awt.*;


public class Main {
    public static void main(String[] args){
        XMLParser xmlParser = new XMLParser();

        ArrayList<Channel> channels = xmlParser.getChannels();

        if (channels.isEmpty()) {
            System.out.println("No channels were fetched.");
        } else {
            for (Channel channel : channels) {
                System.out.println("Channel ID: " + channel.getId());
                System.out.println("Channel Name: " + channel.getChannelName());
                System.out.println("Schedule URL: " + channel.getSchemaURL());
                System.out.println("Image URL: " + channel.getImageURL());
                System.out.println("---------------------------------------");
            }
        }
    }


}