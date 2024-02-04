package Model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * That class will retrive a specific channels program information such as start time, end time,
 * image and more. Those data will be retrieved and parsed from the API of the Sverige Radio.
 * @author Gazi Md Rakibul Hasan
 *
 */
public class ScheduleParser implements DataFetchStrategy<Schedule>{
    private final ArrayList<Schedule> schedules = new ArrayList<Schedule>();
    private final TimeChecker timeChecker =  new TimeChecker();
    private final DateTimeFormatter parser = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private String scheduleURL;
    private final Channel channel;

    /**
     *
     * It is a constructor of the ScheduleParser class, it will take the given channel to fetch
     * its schedules.
     * @param channel the channel.
     */
    public ScheduleParser(Channel channel) {
        this.channel = channel;
        initializeScheduleURL(channel);
    }


    /**
     * That mehtod initialize the given channel's url and calculate time frame
     * 12 hours after form the current time and 12 hours before from the current time.
     * @param channel the channel.
     */
    private void initializeScheduleURL(Channel channel) {
        this.scheduleURL = channel.getScheduleURL();
        if (scheduleURL != null) {
            scheduleURL = "http://api.sr.se/api/v2/scheduledepisodes?channelid="+channel.getId()+"&pagination=false";
            timeChecker.setTwelveHoursAfterwards();
            timeChecker.setTwelveHoursFromBackward();
        }
    }


    /**
     * It checks if we need to fetch data from the specific time or not, based on the current
     * time and date it will edit the schedule url based on the current date and time.
     * @param channel the channel.
     */
    private void fetchDataBasedOnTime(Channel channel) {
        if (timeChecker.needToFetchDataFromTomorrow()) {
            String tomorrowURL = scheduleURL + "&date=" + timeChecker.getTommorowDate();
            fetchDataFromAPI(channel.getScheduleURL());
            fetchDataFromAPI(tomorrowURL);
        }

        if (timeChecker.needToFetchDataFromYesterday()) {
            String yesterdayURL = scheduleURL + "&date=" + timeChecker.getYesterdayDate();
            fetchDataFromAPI(channel.getScheduleURL());
            fetchDataFromAPI(yesterdayURL);
        }
    }


    /**
     * It fetches data from the API based on the schedule url, it inititase the DOM parser
     * to parse the XML file.
     * @param scheduleURL the schedule url.
     */
    private void fetchDataFromAPI(String scheduleURL) {
        if (scheduleURL != null) {
            try {
                URL url = new URL(scheduleURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(connection.getInputStream());
                    doc.normalize();
                    processSchedule(doc);
                } else {
                    invokeErrorDialog("Error: HTTP response code " + connection.getResponseCode());
                }
            } catch (ParserConfigurationException e) {
                invokeErrorDialog("Parser Configuration Error");
            } catch (SAXException e) {
                invokeErrorDialog("XML Parsing Error");
            } catch (IOException e) {
                invokeErrorDialog("I/O Error: " + e.getMessage());
            }
        } else {
            invokeErrorDialog("There is nothing to fetch");
        }
    }


    /**
     * It invokes the error dialog with the given message.
     * @param message the message.
     */
    private void invokeErrorDialog(String message) {
//        if (!SwingUtilities.isEventDispatchThread()) {
//            System.out.println("Not on EDT: I am from the schedule parser class " + Thread.currentThread().getName());
//        }
        SwingUtilities.invokeLater(() -> {
            if (!SwingUtilities.isEventDispatchThread()) {
                System.out.println("Still not on EDT: " + Thread.currentThread().getName());
            } else {
                System.out.println("Now on EDT: " + Thread.currentThread().getName());
            }
            JOptionPane.showMessageDialog(null, message);
        });
    }





    /**
     * Processes each 'scheduledepisode' element in the provided XML document.
     * It filters and processes episodes based on their start times, comparing them to a
     * specified time range.This method iterates through all 'scheduledepisode' elements,
     * parses their start times, and converts these times to the local time zone.
     * @param document The DOM document containing elements to be processed.
     */

    private void processSchedule(Document document) {
        NodeList nodeList = document.getElementsByTagName("scheduledepisode");
        ZonedDateTime upperTime = timeChecker.getTwelveHoursFromAfterwards();
        ZonedDateTime lowerTime = timeChecker.getTwelveHoursFromBackward();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            String starttimeutc = getElementTextContent(element, "starttimeutc");
            if(starttimeutc == null){
                continue;
            }else{
                ZonedDateTime startTimeZoned = ZonedDateTime.parse(starttimeutc, parser);
                ZonedDateTime startTimeInLocalZone = startTimeZoned.withZoneSameInstant(ZoneId.systemDefault());
                createSchedule(startTimeInLocalZone, lowerTime, upperTime, element);
            }
        }
    }




    /**
     * Creates schedules for a specific channel using the schedule url.
     * It checks if the program's start time falls within the specified
     * 12-hour time frame, both before and after the current time.
     * If the condition is met, then it fetches information based on the tag name from the element.
     * It prepares the schedule for instantiation.
     *
     * @param startTimeInLocalZone the start time of a program.
     * @param lowerTime lower bound of the time.
     * @param upperTime upper bound of the time.
     * @param element element that contains the information, the information will be stored based on
     *                the tag name.
     */

    private void createSchedule(ZonedDateTime startTimeInLocalZone, ZonedDateTime lowerTime, ZonedDateTime upperTime, Element element) {
        if(startTimeInLocalZone.isAfter(lowerTime) && startTimeInLocalZone.isBefore(upperTime)){
            String startTime = startTimeInLocalZone.format(formatter);
            String programName = getElementTextContent(element, "title");
            String description = getElementTextContent(element, "description");
            String imageURL = getElementTextContent(element, "imageurl");
            String endtimeutc = getElementTextContent(element, "endtimeutc");
            String endTime  = null;
            if (endtimeutc != null) {
                ZonedDateTime endTimeZoned = ZonedDateTime.parse(endtimeutc,parser);
                ZonedDateTime endTimeInLocalZone = endTimeZoned.withZoneSameInstant(ZoneId.systemDefault());
                endTime  = endTimeInLocalZone.format(formatter);
            }
            instanstiateSchedule(startTime, endTime, programName, imageURL, description);
        }
    }


    /**
     * It creates a schedule of the given program with specified information.
     * @param startTime the start time of the program
     * @param endTime the end time of the program
     * @param programName the name of the program
     * @param imageURL the image URL of the program.
     * @param description the brief description of the program.
     */
    private void instanstiateSchedule(String startTime, String endTime, String programName, String imageURL, String description) {
        Schedule schedule = new ScheduleBuilder()
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setProgramName(programName)
                .setImage(imageURL)
                .setDescription(description)
                .build();

        schedules.add(schedule);
    }



    private String getElementTextContent(Element parentElement, String childElementName) {
        NodeList list = parentElement.getElementsByTagName(childElementName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }


    /**
     * It returns the schedule of the given channel.
     * @return the schedule of the given channel.
     */
    public ArrayList<Schedule> getScheduleList() {
        return schedules;
    }




    @Override
    public ArrayList<Schedule> fetchData() {
        if (scheduleURL != null) {
            fetchDataBasedOnTime(channel);
        }
        return this.getScheduleList();
    }


    /**
     * For the testing purpose
     */
    public void fetchChannelScedule(HttpURLConnection scheduleURL) {
        try {
            scheduleURL.setRequestMethod("GET");
            if (scheduleURL.getResponseCode() == HttpURLConnection.HTTP_OK) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = factory.newDocumentBuilder();
                Document doc = documentBuilder.parse(scheduleURL.getInputStream());
                doc.normalize();
                processSchedule(doc);
            } else {
                invokeErrorDialog("Error: HTTP request failed with code " + scheduleURL.getResponseCode());
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            invokeErrorDialog("Error: " + e.getMessage());
        }
    }




}
