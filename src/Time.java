import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Time {
    public static void main(String[] args){
       if(needToFetchDataFromYesterday()){
           LocalDate yesterday = LocalDate.now().minusDays(1);
           System.out.println(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE));
       }
       if(needToFetchDataFromTomorrow()){
           System.out.println("helo");
       }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("time after "+ getTwelveHoursAfterwards().format(formatter));
        System.out.println("time before "+ getTwelveHoursFromBackward().format(formatter));


        System.out.println(getTommorowDate());
    }

    public static boolean needToFetchDataFromYesterday() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime twelveHoursAgo = now.minusHours(12);
        ZonedDateTime startOfToday = now.toLocalDate().atStartOfDay(now.getZone());
        System.out.println(startOfToday);
        return twelveHoursAgo.isBefore(startOfToday);
    }

    public static boolean needToFetchDataFromTomorrow() {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime twelveHoursAfter = now.plusHours(12);
        ZonedDateTime startOfTomorrow = now.toLocalDate().plusDays(1).atStartOfDay(now.getZone());
        return twelveHoursAfter.isAfter(startOfTomorrow);
    }


    public static LocalDateTime getTwelveHoursFromBackward() {
        ZonedDateTime now = ZonedDateTime.now();
        return now.minusHours(12).toLocalDateTime();
    }

    public static String getTommorowDate(){
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        now = now.plusDays(1);
        return format.format(now);
    }

    public static LocalDateTime getTwelveHoursAfterwards() {
        ZonedDateTime now = ZonedDateTime.now();
        return now.plusHours(12).toLocalDateTime();
    }

}
