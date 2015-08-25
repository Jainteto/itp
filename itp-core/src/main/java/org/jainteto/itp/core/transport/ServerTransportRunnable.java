package org.jainteto.itp.core.transport;

public class ServerTransportRunnable implements Runnable {

    public interface Callback {
        default void initBegin() {}
        default void initEnd() {}
        default void runningBegin() {}
        default void runningEnd() {}
        default void destroyBegin() {}
        default void destroyEnd() {}

        default void initError(Exception e) {}
        default void runningError(Exception e) {}
        default void destroyError(Exception e) {}
    }

    private final ServerTransport transport;
    private final Callback        callback;
    private final MessageHandler  messageHandler;

    public ServerTransportRunnable(ServerTransport transport, MessageHandler messageHandler, Callback callback) {
        this.transport      = transport;
        this.callback       = callback;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run() {
        if (init()) process();
        destroy();
    }

    private boolean init() {
        try {
            callback.initBegin();
            transport.init();
            callback.initEnd();
            return true;
        } catch (Exception e) {
            callback.initError(e);
            return false;
        }
    }

    private void process() {
        try {
            callback.runningBegin();
            do {
                Message message = transport.getRequest();
                if (message != null && message.getMessageStatus() != Message.STATUS_NO) {
                    message = messageHandler.handle(message);
                    transport.setResponse(message);
                }
                Thread.yield();
            } while (transport.loop() && !Thread.currentThread().isInterrupted());
            callback.runningEnd();
        } catch (Exception e) {
            callback.runningError(e);
        }
    }

    private void destroy() {
        try {
            callback.destroyBegin();
            transport.destroy();
            callback.destroyEnd();
        } catch (Exception e) {
            callback.destroyError(e);
        }
    }

}
