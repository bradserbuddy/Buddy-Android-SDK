package com.buddy.sdk;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by tyler on 4/27/15.
 */
public interface BuddyServiceClient {
    String GET = "GET";
    String PUT = "PUT";
    String POST = "POST";
    String PATCH = "PATCH";
    String DELETE = "DELETE";

    void setSynchronousMode(boolean value);

    boolean getSynchronousMode();

    String signString(String stringToSign, String secret);
    <T> Future<BuddyResult<T>> makeRequest(final String verb, final String path, final Map<? extends String, ? extends Object> parameters, final BuddyCallback<T> callback, final Class<T> clazz);

}
