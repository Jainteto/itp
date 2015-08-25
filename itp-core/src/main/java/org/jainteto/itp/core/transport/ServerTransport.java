package org.jainteto.itp.core.transport;

public interface ServerTransport extends Transport {

    /**<pre>
     * This method working into cycle and it is invoking every iteration of cycle.
     *
     * If you have a good message then
     * you will must return {@link Message#message(String)}
     * for construct some Message.
     * After that, {@link ServerTransport#setResponse(Message)} will be invoked.
     *
     * If you have no message then
     * you will must return {@link Message#noMessage()}.
     * After that, {@link ServerTransport#setResponse(Message)} will not be invoked.
     *
     * If you have a bad message then
     * you will must return {@link Message#error(String)}.
     * After that, {@link ServerTransport#setResponse(Message)} will be invoked.
     *
     * </pre>
     * @see Message
     * @return return some message Message. Not NULL!
     * @throws Exception some error
     */
    Message getRequest() throws Exception;

    /** <pre>
     * This method will be invoked
     * if you received {@link Message#message(String)}
     * from {@link ServerTransport#getRequest()} method
     * </pre>
     * @see ServerTransport#getRequest()
     * @param message
     * @throws Exception
     */
    void setResponse(Message message) throws Exception;

    /**<pre>
     * This method work in a cycle.
     * If you want to stop the cycle process than you must return false value.
     * </pre>
     * @return true value if you want to continue or false if you want to stop.
     * @throws Exception some error.
     */
    boolean loop() throws Exception;

}
