package org.jainteto.itp.core.transport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class ServerTransportExecutor {

    private final ServerTransportRunnable transportRunnable;
    private final ExecutorService         executorService;

    private Future transportFuture;

    public ServerTransportExecutor(ServerTransportRunnable transportRunnable, ThreadFactory threadFactory) {
        this.transportRunnable = transportRunnable;
        this.executorService   = Executors.newSingleThreadExecutor(threadFactory);
    }

    public ServerTransportExecutor(ServerTransportRunnable transportPerformer) {
        this.transportRunnable = transportPerformer;
        this.executorService   = Executors.newSingleThreadExecutor();
    }

    public void start() {
        transportFuture = executorService.submit(transportRunnable);
    }

    public void stop() {
        if (transportFuture != null) {
            transportFuture.cancel(true);
        }
    }

}
