package com.buddy.sdk;

abstract class BuddyFutureCallback<V> {
    public abstract void completed(BuddyFuture<V> future);
}
