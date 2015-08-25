package org.jainteto.itp.core.transport;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mockito.Mockito.*;

public class ServerTransportExecutorTest {


    private final Message request  = Message.message("Hello");
    private final Message response = Message.message("Hello World!");

    private final static String SERVER_TRANSPORT_CLASS_NAME                = ServerTransport.class.getCanonicalName();
    private final static String SERVER_TRANSPORT_EXECUTOR_CLASS_NAME       = ServerTransportExecutor.class.getCanonicalName();
    private final static String SERVER_TRANSPORT_OBSERVER_CLASS_NAME       = ServerTransportObserver.class.getCanonicalName();
    private static final String SERVER_TRANSPORT_RUNNABLE_CLASS_NAME       = ServerTransportRunnable.class.getCanonicalName();
    private static final String SERVER_TRANSPORT_THREAD_FACTORY_CLASS_NAME = ServerTransportThreadFactory.class.getCanonicalName();

    @Test
    public void testRunServerTransportExecutor() throws Exception {
        ServerTransport transport = createServerTransportMock();
        when(transport.getRequest()).thenReturn(request);

        // make it work 3 times
        when(transport.loop()).thenAnswer(new Answer<Object>() {
            int count;

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                count++;
                return count < 3;
            }
        });

        Map<String, Object> holder = runStopTransport(transport, false);

        /* Verify Server Transport Observer */
        ServerTransportObserver observer = cast(holder.get(SERVER_TRANSPORT_OBSERVER_CLASS_NAME));
        verify(observer, times(1)).initBegin();
        verify(observer, times(1)).initEnd();
        verify(observer, times(1)).runningBegin();
        verify(observer, times(1)).runningEnd();
        verify(observer, times(1)).destroyBegin();
        verify(observer, times(1)).destroyEnd();

        /* Verify Server Transport */
        verify(transport, times(1)).init();
        verify(transport, times(3)).getRequest();
        verify(transport, times(3)).setResponse(response);
        verify(transport, times(3)).loop();
        verify(transport, times(1)).destroy();
    }

    @Test
    public void saveExceptionTest() throws Exception {
        /* Starting Exception */
        {
            RuntimeException exception = new RuntimeException();
            saveExceptionTest(
                    "Validate starting Exception (init method):",
                    transport -> doThrow(exception).when(transport).init(),
                    () -> exception,
                    ServerTransportObserver::getStartingException,
                    observer -> verify(observer, times(1)).initError(exception)
            );
        }

        /* Stopping Exception */
        {
            RuntimeException exception = new RuntimeException();
            saveExceptionTest(
                    "Validate stopping Exception(destroy method):",
                    transport -> doThrow(exception).when(transport).destroy(),
                    () -> exception,
                    ServerTransportObserver::getStoppingException,
                    observer -> verify(observer, times(1)).destroyError(exception)
            );
        }

        /* Get Request Exception */
        {
            RuntimeException exception = new RuntimeException();
            saveExceptionTest(
                    "Validate running exception(getRequest method):",
                    transport -> when(transport.getRequest()).thenThrow(exception),
                    () -> exception,
                    ServerTransportObserver::getRunningException,
                    observer -> verify(observer, times(1)).runningError(exception)
            );
        }

        /* Set Response Exception */
        {
            RuntimeException exception = new RuntimeException();
            saveExceptionTest(
                    "Validate running exception(setResponse method):",
                    transport -> doThrow(exception).when(transport).setResponse(response),
                    () -> exception,
                    ServerTransportObserver::getRunningException,
                    observer -> verify(observer, times(1)).runningError(exception)
            );
        }

        /* Loop Exception */
        {
            RuntimeException exception = new RuntimeException();
            saveExceptionTest(
                    "Validate running exception(loop method):",
                    transport -> doThrow(exception).when(transport).loop(),
                    () -> exception,
                    ServerTransportObserver::getRunningException,
                    observer -> verify(observer, times(1)).runningError(exception)
            );
        }
    }

    private ServerTransport createServerTransportMock() {
        ServerTransport transport = mock(ServerTransport.class);
        when(transport.getName()).thenReturn("ServerTransportStub");
        when(transport.getDescription()).thenReturn("This transport for tests");
        return transport;
    }

    private Map<String, Object> saveExceptionTest(String message,
                                   Consumer<ServerTransport> transportPrepare,
                                   Supplier<Exception> expect,
                                   Function<ServerTransportObserver, Exception> actual,
                                   Consumer<ServerTransportObserver> observerTest)
            throws Exception
    {
        ServerTransport transport = createServerTransportMock();
        when(transport.getRequest()).thenReturn(request);

        transportPrepare.accept(transport);

        Map<String, Object>     holder   = runStopTransport(transport, true);
        ServerTransportObserver observer = cast(holder.get(SERVER_TRANSPORT_OBSERVER_CLASS_NAME));

        Exception expectException = expect.get();
        Exception actualException = actual.apply(observer);

        Assert.assertEquals(message, expectException, actualException);

        observerTest.accept(observer);

        return holder;
    }

    private Map<String, Object> runStopTransport(ServerTransport transport, boolean forciblyStop) throws InterruptedException {
        MessageHandler handler = message -> {
            Assert.assertEquals(request, message);
            return response;
        };

        ServerTransportObserver observer = new ServerTransportObserver();
        observer = spy(observer);

        ServerTransportRunnable runnable = new ServerTransportRunnable(transport, handler, observer);
        runnable = spy(runnable);

        ServerTransportThreadFactory factory  = new ServerTransportThreadFactory(transport);
        factory = spy(factory);

        ServerTransportExecutor executor = new ServerTransportExecutor(runnable, factory);
        executor = spy(executor);

        executor.start();
        observer.waitRunning();
        if (forciblyStop) executor.stop();
        observer.waitStopping();

        Map<String, Object> holder = new HashMap<>();
        holder.put(SERVER_TRANSPORT_CLASS_NAME, transport);
        holder.put(SERVER_TRANSPORT_OBSERVER_CLASS_NAME, observer);
        holder.put(SERVER_TRANSPORT_RUNNABLE_CLASS_NAME, runnable);
        holder.put(SERVER_TRANSPORT_THREAD_FACTORY_CLASS_NAME, factory);
        holder.put(SERVER_TRANSPORT_EXECUTOR_CLASS_NAME, executor);
        return holder;
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object object) { return (T)object; }

    @FunctionalInterface
    public interface Consumer<T> { void accept(T t) throws Exception; }

}
