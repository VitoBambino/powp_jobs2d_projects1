package edu.kis.powp.jobs2d.features;

import edu.kis.powp.jobs2d.drivers.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.drivers.visitor.VisitableDriver;
import edu.kis.powp.observer.Publisher;
import edu.kis.powp.observer.Subscriber;

import java.util.function.BiConsumer;

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

    @Override
    public void setPosition(int x, int y) {
        executeMove(x, y, driver::setPosition, false);
    }

    @Override
    public void operateTo(int x, int y) {
        executeMove(x, y, driver::operateTo, true);
    }

    private void executeMove(
            int x,
            int y,
            BiConsumer<Integer, Integer> action,
            boolean operation
    ) {

        if (!initialized) {
            initialize(x, y);
            action.accept(x, y);
            return;
        }

        double d = distance(lastX, lastY, x, y);

        totalDistance += d;

        if (operation) {
            operationDistance = d;
        }

        updateLastPosition(x, y);

        action.accept(x, y);

        publisher.notifyObservers();
    }

    private void initialize(int x, int y) {
        lastX = x;
        lastY = y;
        initialized = true;
    }

    private void updateLastPosition(int x, int y) {
        lastX = x;
        lastY = y;
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
        driver.accept(visitor);
    }
}