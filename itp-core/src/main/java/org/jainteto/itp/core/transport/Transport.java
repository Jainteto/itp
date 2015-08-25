package org.jainteto.itp.core.transport;

public interface Transport {

    String getName();
    String getDescription();

    /**
     * This method will be invoked before start a process of this transport
     * @throws Exception
     */
    default void init()    throws Exception {}

    /**
     * This method will be invoked after stop processing of this transport
     * @throws Exception
     */
    default void destroy() throws Exception {}

}
