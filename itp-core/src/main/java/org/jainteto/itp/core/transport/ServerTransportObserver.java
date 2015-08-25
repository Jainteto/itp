package org.jainteto.itp.core.transport;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServerTransportObserver implements ServerTransportRunnable.Callback {

    private ServerTransportStatus status = ServerTransportStatus.DISABLE;

    private CountDownLatch runningLatch;
    private CountDownLatch stoppingLatch;

    private Exception startingException;
    private Exception runningException;
    private Exception stoppingException;

    public ServerTransportObserver() {
        resetWaiters();
    }

    public ServerTransportStatus getStatus() { return status; }

    @Override
    public void initBegin() {
        startingException = null;
        runningException  = null;
        stoppingException = null;
        status = ServerTransportStatus.STARTING;
    }

    @Override
    public void runningBegin() {
        status = ServerTransportStatus.RUNNING;
        runningLatch.countDown();
    }

    @Override
    public void destroyBegin() { status = ServerTransportStatus.STOPPING; }

    @Override
    public void destroyEnd() {
        status = ServerTransportStatus.DISABLE;
        stoppingLatch.countDown();
    }

    @Override
    public void initError(Exception e) {
        startingException = e;
        runningLatch.countDown();
    }

    @Override
    public void runningError(Exception e) { runningException = e; }

    @Override
    public void destroyError(Exception e) {
        stoppingException = e;
        stoppingLatch.countDown();
    }

    public Exception getStartingException() { return startingException; }

    public Exception getRunningException() { return runningException; }

    public Exception getStoppingException() { return stoppingException; }

    public boolean waitRunning() throws InterruptedException {
        return waitRunning(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public boolean waitRunning(long timeout, TimeUnit unit) throws InterruptedException {
        return runningLatch.await(timeout, unit);
    }

    public boolean waitStopping() throws InterruptedException {
        return waitStopping(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public boolean waitStopping(long timeout, TimeUnit unit) throws InterruptedException {
        return stoppingLatch.await(timeout, unit);
    }

    public void resetWaiters() {
        runningLatch  = new CountDownLatch(1);
        stoppingLatch = new CountDownLatch(1);
    }

}
