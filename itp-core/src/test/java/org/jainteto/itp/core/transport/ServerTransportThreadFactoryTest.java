package org.jainteto.itp.core.transport;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ServerTransportThreadFactoryTest {

    @Test
    public void testServerTransportWithNullName() {
        ServerTransport transport = mock(ServerTransport.class);
        ServerTransportThreadFactory factory = new ServerTransportThreadFactory(transport);

        Thread thread = factory.newThread(() -> {});
        Assert.assertNotNull("Validate thread on null", thread);

        String name = thread.getName();
        Assert.assertNotNull("Validate thread name on null", name);
        Assert.assertNotEquals("Validate thread name on an empty", "", name);
    }

    @Test
    public void testFactoryWithNullTransport() {
        try {
            new ServerTransportThreadFactory(null);
        } catch (NullPointerException npe) {
            return;
        }
        throw new AssertionError("Thread factory didn't throw NullPointerException");
    }

}
