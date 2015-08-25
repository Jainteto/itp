package org.jainteto.itp.core.transport;

public enum ServerTransportStatus {
    DISABLE("Disable"),
    STARTING("Starting"),
    RUNNING("Running"),
    STOPPING("Stopping");

    private final String string;

    ServerTransportStatus(String string) { this.string = string; }

    public String toString() { return string; }

}
