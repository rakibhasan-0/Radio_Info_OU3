package Test;
import Model.*;
import View.MenuBarView;
import View.ProgramView;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CacheTest {
    @Test
    public void checkCacheHasContains() throws InterruptedException {
        try {
            Class<?> cls = Class.forName("Controll.Controller");
            Constructor<?> constructor = cls.getConstructor(MenuBarView.class, ProgramView.class);
            MenuBarView menuBarView = new MenuBarView();
            ProgramView programView = new ProgramView();
            Object controllerInstance = constructor.newInstance(menuBarView, programView);
            Field field = cls.getDeclaredField("cache");
            field.setAccessible(true);
            Cache cacheInstance = (Cache) field.get(controllerInstance);
            Field map = cacheInstance.getClass().getDeclaredField("caches");
            map.setAccessible(true);
            Map<?, ?> cacheMap = (Map<?, ?>) map.get(cacheInstance);

            Channel channel = new ChannelBuilder()
                    .setScheduleURL("http://api.sr.se/v2/scheduledepisodes?channelid=132")
                    .setChannelName("P1")
                    .setChannelId(132)
                    .setChannelType("Rikskanal")
                    .build();

            assertTrue(cacheMap.isEmpty());
            ScheduleParser parser = new ScheduleParser(channel);
            ArrayList<Schedule> schedules = parser.getScheduleList();
            Method method = cls.getDeclaredMethod("getSchedule", Channel.class, ArrayList.class);
            method.setAccessible(true);
            method.invoke(controllerInstance,channel,schedules);
            assertFalse(cacheMap.isEmpty());

        } catch (ClassNotFoundException e) {
            System.out.println("class not found");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 NoSuchFieldException e) {
            System.out.println("something went wrong");
        }
    }


}