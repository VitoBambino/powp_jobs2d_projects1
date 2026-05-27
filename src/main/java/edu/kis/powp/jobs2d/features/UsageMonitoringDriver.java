package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.observer.Publisher;
import edu.kis.powp.observer.Subscriber;

public class UsageMonitoringDriver implements VisitableDriver {

    private final VisitableDriver driver;
    private final Publisher publisher = new Publisher();

    private int lastX;
    private int lastY;
    private boolean initialized = false;

    private double totalDistance = 0;
    private double operationDistance = 0;

    public UsageMonitoringDriver(VisitableDriver driver) {
        this.driver = driver;
    }

    public void addSubscriber(Subscriber subscriber) {
        publisher.addSubscriber(subscriber);
    }

    public void clearSubscribers() {
        publisher.clearObservers();
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void Inicialize(int x , int y)
    {
        lastX = x;
        lastY = y;
        initialized = true;
    }

    public void UpdateLast(int x , int y)
    {
        lastX = x;
        lastY = y;
    }

    @Override
    public void setPosition(int x, int y) {
        if (!initialized) {
            Inicialize(x ,y);
            driver.setPosition(x, y);
            return;
        }

        totalDistance += distance(lastX, lastY, x, y);
        UpdateLast(x,y);

        driver.setPosition(x, y);
        publisher.notifyObservers();
    }

    @Override
    public void operateTo(int x, int y) {

        if (!initialized) {
            Inicialize(x ,y);
            driver.operateTo(x, y);
            return;
        }

        double d = distance(lastX, lastY, x, y);
        totalDistance += d;
        operationDistance = d;
        UpdateLast(x,y);

        driver.operateTo(x, y);
        publisher.notifyObservers();
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getOperationDistance() {
        return operationDistance;
    }

    private double distance(int x1, int y1, int x2, int y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    @Override
    public void accept(DriverVisitor visitor) {
        (driver).accept(visitor);
    }
}