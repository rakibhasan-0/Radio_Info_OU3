package Controll;

public interface ScheduleSubject {
    public void registerObserver(ScheduleObserver observer);
    public void removeObserver(ScheduleObserver observer);
    public void notifyObservers();
}
