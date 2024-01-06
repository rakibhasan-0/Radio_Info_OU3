package Model;

import Controll.Observer;
import Controll.Subject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ScheduleWorker extends SwingWorker<ArrayList<Schedule>,Void> implements Subject {
    private ArrayList<Schedule> schedules;
    private final ArrayList<Observer> observers;
    private final Channel channel;

    public ScheduleWorker(Channel channel){
        this.channel = channel;
        observers = new ArrayList<Observer>();
        this.schedules = new ArrayList<Schedule>();
    }

    @Override
    protected ArrayList<Schedule> doInBackground()  {
        DataFetchStrategy<Schedule> parser = new ScheduleParser(channel);
        return parser.fetchData();
    }

    @Override
    protected void done() {
        try {
            schedules = get();
            notifyObservers();
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Interrupted: " + "Could not get schedules");
        } catch (ExecutionException e) {
           JOptionPane.showMessageDialog(null, "Execution: " + "Could not get schedules");
        }
    }

    @Override
    public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : observers) {
            o.scheduleUpdate(channel,schedules);
        }
    }
}
