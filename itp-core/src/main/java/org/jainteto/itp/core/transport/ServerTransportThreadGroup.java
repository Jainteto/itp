package org.jainteto.itp.core.transport;

public class ServerTransportThreadGroup extends ThreadGroup {

    private static final ServerTransportThreadGroup INSTANCE = new ServerTransportThreadGroup();

    public static final String NAME = "ServerTransportThreads";

    private ServerTransportThreadGroup() { super(NAME); }

    public static ServerTransportThreadGroup getInstance() { return INSTANCE; }

}
