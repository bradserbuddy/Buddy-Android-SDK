package com.buddy.sdk;

import android.content.Context;
import com.buddy.sdk.BuddyClientImpl;
import com.buddy.sdk.BuddyServiceClient;

/**
 * Created by tyler on 4/27/15.
 */
public class NetworkIsolatedBuddyClient extends BuddyClientImpl {
    private final BuddyServiceClient _client;
    public NetworkIsolatedBuddyClient(Context context, String appId, String appKey, BuddyServiceClient client) {
        super(context, appId, appKey);
        _client = client;
    }

    @Override
    protected BuddyServiceClient getServiceClient(){
        return _client;
    }
}
