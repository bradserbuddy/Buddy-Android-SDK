package com.buddy.sdk;

public abstract  class BuddyCallback <T> {


    private Class<T> returnTypeClass;

    Class<T> getResultClass() {
        return returnTypeClass;
    }

    public BuddyCallback(Class<T> clazz) {
        returnTypeClass = clazz;
    }

    public abstract void completed(BuddyResult<T> result);
}

