package org.jainteto.itp.core.transport;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerTransportThreadFactory implements ThreadFactory {

    private static final String        THREAD_NAME_SUFFIX  = "-N_";
    private static final AtomicInteger THREAD_COUNTER      = new AtomicInteger();

    private final String transportName;

    public ServerTransportThreadFactory(Transport transport) {
        this.transportName = transport.getName();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        if (runnable == null) throw new NullPointerException();
        return new Thread(ServerTransportThreadGroup.getInstance(), runnable, getNextName());
    }

    private String getNextName() {
        return (transportName == null ? "Transport" : transportName)
                + THREAD_NAME_SUFFIX
                + THREAD_COUNTER.getAndIncrement();
    }

}
