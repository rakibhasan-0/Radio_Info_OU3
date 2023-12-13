package Model;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;

public class ScheduleBuilder {
    private String programName;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private Image image;


    public ScheduleBuilder(){
    }

    public ScheduleBuilder setProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public ScheduleBuilder setDescription(String description) {
        this.description = description;
        return this;
    }


    public ScheduleBuilder setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }


    public ScheduleBuilder setEndTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }


    public ScheduleBuilder setImage(String image) {
        this.image = processChannelsImage(image);
        return this;
    }


    private Image processChannelsImage(String imageUrl) {
        int IMAGE_WIDTH = 280, IMAGE_HEIGHT = 280;
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }try {
            URL url = new URL(imageUrl);
            BufferedImage image = ImageIO.read(url);
            return image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            // try to find the right expetion
            JOptionPane.showMessageDialog(null, "Couldn't find image");
            return null;
        }
    }


    public Schedule build(){
        Schedule schedule = new Schedule();
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setDescription(description);
        schedule.setProgramName(programName);
        schedule.setImage(image);
        return schedule;
    }

}
