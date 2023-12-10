import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import org.w3c.dom.Document;

public class XMLParser {
    ArrayList<Channel> channels = new ArrayList<Channel>();
    boolean hasNextPage = false;
    public XMLParser() {
        String baseUrl = "http://api.sr.se/api/v2/channels/";
        try {
            do {
                URL url = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(null, "Error fetching channels");
                    break;
                }

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(connection.getInputStream());
                doc.normalize();

                // Process the channels from the current page
                processChannels(doc);
                NodeList nodeList = doc.getElementsByTagName("nextpage");

                if (nodeList.getLength() > 0) {
                    baseUrl = nodeList.item(0).getTextContent();
                    hasNextPage = true;
                } else {
                    hasNextPage = false;
                }
            } while (hasNextPage);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processChannels(Document doc) {
        NodeList channelNodes = doc.getElementsByTagName("channel");
        for (int i = 0; i < channelNodes.getLength(); i++) {
            Element element = (Element) channelNodes.item(i);
            int id = Integer.parseInt(element.getAttribute("id"));
            String name = element.getAttribute("name");

            // Assuming channel name is stored in the 'name' attribute, no need for separate channelName
            String image = getElementTextContent(element, "image");
            String scheduleUrl = getElementTextContent(element, "scheduleurl");

            System.out.println("id = " + id);
            System.out.println("name = " + name);
            System.out.println("image = " + image);
            System.out.println("scheduleUrl = " + scheduleUrl);
            Channel channel = new Channel(name, id, scheduleUrl, image);
            channels.add(channel);
        }
    }

    private String getElementTextContent(Element parentElement, String childElementName) {
        NodeList list = parentElement.getElementsByTagName(childElementName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }


    ArrayList<Channel> getChannels(){
        return channels;
    }

}
