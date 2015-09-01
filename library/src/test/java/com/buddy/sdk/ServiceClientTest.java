package com.buddy.sdk;

import android.content.Context;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

public class ServiceClientTest extends TestCase {

    private static final String TEST_APP_ID = "bbbb.ccdd";

    @Mock private BuddyServiceClient client;
    @Mock private Context context;

    @Override
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    public void testJsonSerialization() throws Exception {
        when(client.makeRequest(eq(BuddyServiceClient.POST), eq("/notifications"), any(HashMap.class), any(BuddyCallback.class) , any(Class.class) ))
                .thenReturn(new Future<BuddyResult<Map<String, Object>>>() {
                    @Override
                    public boolean cancel(boolean b) {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isDone() {
                        return true;
                    }

                    @Override
                    public BuddyResult<Map<String, Object>> get() throws InterruptedException, ExecutionException {
                        return new BuddyResult<Map<String, Object>>(new JsonEnvelope<Map<String, Object>>());
                    }

                    @Override
                    public BuddyResult<Map<String, Object>> get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                        return null;
                    }
                });
        BuddyClient isolatedClient = new NetworkIsolatedBuddyClient(context,
                TEST_APP_ID, UUID.randomUUID().toString(), client);

        isolatedClient.post("/notifications", new HashMap<String, Object>() {{
            put("recipients", Lists.newArrayList("expectedUser"));
            put("title", "This is a test message");
        }}, HashMap.class);
//TODO Fix this
//        verify(client)
//                .makeRequest(eq(BuddyServiceClient.POST),
//                        eq("/notifications"),
//                        argThat(hasEntry(eq("recipients"), any(ArrayList.class))),
//                        any(BuddyCallback.class),
//                        any(Class.class));




    }
}
