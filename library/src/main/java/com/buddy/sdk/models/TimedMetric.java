package com.buddy.sdk.models;

import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyClient;
import com.buddy.sdk.BuddyResult;

public class TimedMetric extends ModelBase {

    private BuddyClient client;
    public void setBuddyClient(BuddyClient client) {
        this.client = client;
    }

    public void finish(final BuddyCallback<Boolean> callback) {
        if (client != null && id != null) {



            client.<Boolean>delete("/metrics/events/" + id, null, new BuddyCallback<Boolean>(Boolean.class) {
                @Override
                public void completed(BuddyResult<Boolean> result) {
                    TimedMetric.this.client = null;
                    callback.completed(result);
                }
            });

        }
    }
}
