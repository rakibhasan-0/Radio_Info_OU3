package Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * That class represents the parsing of the XML file, it will fetch the channels from the
 * Sverige Radio, and then parse them accordingly.
 * @author Gazi Md Rakibul Hasan
 */
public class XMLParser implements DataFetchStrategy <Channel>{
    private final ArrayList<Channel> channels = new ArrayList<Channel>();

    public XMLParser() {

    }

    /**
     * It used for the testing usage.
     */
    public XMLParser(HttpURLConnection mockConnection) {
        try {
            mockConnection.setRequestMethod("GET");
            if (mockConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(mockConnection.getInputStream());
                doc.normalize();
                processChannels(doc);
            } else {
                System.err.println("Error: HTTP request failed with code " + mockConnection.getResponseCode()); // show that on the JOPtion.Pane()
            }
        }
        catch (Exception e) {
            manageErrors(e);
        }
    }


    /**
     * That method initiates the parsing of the XML file, It will use the DOM parser to
     * parse the XML file.
     */
    private void initiateParsingOfXML() {
        String baseUrl = "http://api.sr.se/api/v2/channels?pagination=false";
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(baseUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                inputStream = connection.getInputStream();
                Document doc = builder.parse(inputStream);
                doc.normalize();
                processChannels(doc);
            } else {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(null, "Error: HTTP request failed " ) // Ensure this runs on EDT.
                );
            }
        } catch (Exception e) {
            manageErrors(e);
        } finally {
            closeResources(inputStream, connection);
        }
    }


    /**
     * It closes the input stream and the connection.
     * @param inputStream the input stream.
     * @param connection the connection.
     */
    private void closeResources(InputStream inputStream, HttpURLConnection connection) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
               SwingUtilities.invokeLater(() ->
                       JOptionPane.showMessageDialog(null, "Error: Failed to close input stream") // Ensure this runs on EDT.
               );
            }
        }
        if (connection != null) {
            connection.disconnect();
        }
    }


    /**
     * That class will manage the exceptions that could be occurring while initiate DOM parser.
     * @param e Exception instance.
     */
    private void manageErrors(Exception e) {
//        if (!SwingUtilities.isEventDispatchThread()) {
//            System.out.println("Not on EDT: I am from the schedule parser class " + Thread.currentThread().getName());
//        }
        SwingUtilities.invokeLater(() -> {
//            if (!SwingUtilities.isEventDispatchThread()) {
//                System.out.println("Still not on EDT: " + Thread.currentThread().getName());
//            } else {
//                System.out.println("Now on EDT: " + Thread.currentThread().getName());
//            }

            if (e instanceof MalformedURLException) {
                JOptionPane.showMessageDialog(null, "Error: The URL is malformed");
            } else if (e instanceof IOException) {
                JOptionPane.showMessageDialog(null, "I/O Error: Problem occurred during communication with the API");
            } else if (e instanceof ParserConfigurationException) {
                JOptionPane.showMessageDialog(null, "Error: Parser configuration issue");
            } else if (e instanceof SAXException) {
                JOptionPane.showMessageDialog(null, "Error: Issue while parsing the XML document");
            } else {
                JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage());
            }
        });
    }



    /**
     * It will parse the DOM file according to the tag name.
     * @param doc DOM parser document.
     */
    private void processChannels(Document doc) {
        NodeList channelNodes = doc.getElementsByTagName("channel");

        for (int i = 0; i < channelNodes.getLength(); i++) {
            Element element = (Element) channelNodes.item(i);
            int id = Integer.parseInt(element.getAttribute("id"));
            String name = element.getAttribute("name");
            String image = getElementTextContent(element, "image");
            String scheduleUrl = getElementTextContent(element, "scheduleurl");
            String tagLine = getElementTextContent(element, "tagline");

            if(scheduleUrl != null){
                scheduleUrl = scheduleUrl+"&pagination=false";
            }
            String channelType = getElementTextContent(element, "channeltype");
            creatingAChannel(name, id, scheduleUrl, tagLine, channelType, image);
        }
    }


    /**
     * Creates a channel instance and store it in the list of channels.
     * @param name the name of the channel.
     * @param id the id of the channel.
     * @param scheduleUrl the schedule url of the channel, which stores the channel programs inforamtion.
     * @param tagLine the channels tag line.
     * @param channelType the type of the channel.
     * @param image the icon image of the channel.
     */
    private void creatingAChannel(String name, int id, String scheduleUrl, String tagLine, String channelType, String image) {
        Channel channel = new ChannelBuilder()
                .setChannelName(name)
                .setChannelId(id)
                .setScheduleURL(scheduleUrl)
                .setTagline(tagLine)
                .setChannelType(channelType)
                .setImage(image)
                .build();

        channels.add(channel);
    }


    /**
     *  it returns the list of channels.
     * @return the list of channels.
     */
    public ArrayList<Channel> getChannels() {
        return channels;
    }


    /**
     * It gets the first child element with a specified tag name
     * within the parent element.
     * @param parentElement The parent element.
     * @param childElementName The tag name of the child element, and its content will
     *                         be retrieved.
     * @return The first child element with the specified tag name, or null if no such
     *         element is found within the parent element.
     */

    private String getElementTextContent(Element parentElement, String childElementName) {
        NodeList list = parentElement.getElementsByTagName(childElementName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    @Override
    public ArrayList<Channel> fetchData() {
        initiateParsingOfXML();
        return this.getChannels();
    }
}
