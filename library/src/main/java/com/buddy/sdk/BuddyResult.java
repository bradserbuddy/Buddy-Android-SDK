package com.buddy.sdk;


public class BuddyResult <T> extends BuddyResultBase {

   public BuddyResult() {
        super();
    }

    public BuddyResult(JsonEnvelope<T> env) {
        super(env);
    }

    public T getResult() {
        return ((JsonEnvelope<T>)env).result;
    }


    public <T2> BuddyResult<T2> convert(T2 newResult) {
        JsonEnvelope<T2> env2 = ((JsonEnvelope<T>)env).convert(newResult);
        return  new BuddyResult<T2>(env2);
    }

}