package com.buddy.sdk;


import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;



class BuddyServiceClient {

    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String PATCH = "PATCH";
    public static final String DELETE = "DELETE";

    BuddyClient _parent;
    AsyncHttpClient client;
    static Map<String, Method> clientMethods = new HashMap<String,Method>();
    private boolean syncMode;

    public BuddyServiceClient(BuddyClient parent) {
        _parent = parent;

        setSynchronousMode(false);
    }

    public void setSynchronousMode(boolean value) {
       if (value != syncMode) {
           client = null;
           syncMode = value;
       }

    }

    public boolean getSynchronousMode() {
        return syncMode;
    }

    private AsyncHttpClient getHttpClient() {

        boolean isSyncMode = Looper.myLooper() == null || syncMode;

        if (client == null || (client instanceof SyncHttpClient) != isSyncMode) {
            if (isSyncMode) {
                client = new SyncHttpClient();
            }
            else {
                client = new AsyncHttpClient();
            }
        }
        return client;
    }

    private static <T> BuddyResult<T> parseBuddyResponse(Class<T> type, int statusCode, String response) {

        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Date.class, new BuddyDateDeserializer())
                        .registerTypeAdapter(JsonEnvelope.class, new JsonEnvelopeDeserializer(type))
                        .create();


        JsonEnvelope<T> result = gson.fromJson(response, JsonEnvelope.class);
        result.status = statusCode;
        return new BuddyResult<T>(result);
    }



    private static boolean isFile(Object obj) {
        return obj instanceof BuddyFile;
    }

    private Method getClientMethod(String verb) {

        Method m = clientMethods.get(verb.toUpperCase(Locale.getDefault()));

        if (m != null) {
            return m;
        }

        try {
            m = AsyncHttpClient.class.getDeclaredMethod(verb.toLowerCase(Locale.getDefault()),
                    Context.class,
                    String.class,
                    Header[].class,
                    HttpEntity.class,
                    String.class,
                    ResponseHandlerInterface.class
            );

        }
        catch (NoSuchMethodException nsmEx) {
            m = null;
        }

        clientMethods.put(verb.toUpperCase(Locale.getDefault()), m);
        return m;
    }

    private final static String DefaultContentType = "application/json";

    private void logResult(JSONObject result) {
        String json = result.toString();
        Log.d("BuddySdk", json);
    }


    private <T> BuddyFuture<BuddyResult<T>> makeRequestCore(String verb, String path, final String accessToken, final Map<String,Object> parameters, final BuddyCallback<T> callback, final Class<T> clazz) {
        List<Header> headerList = new ArrayList<Header>();
        String root = _parent.getServiceRoot();

        if (root.endsWith("/")) {
            root = root.substring(0, root.length()-1);
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        final String url = String.format("%s/%s", root, path);

        if (accessToken != null) {
            headerList.add(new BasicHeader("Authorization", String.format("Buddy %s",accessToken)));
        }

        final BuddyFuture<BuddyResult<T>> promise = new BuddyFuture<BuddyResult<T>>();


        Class rClass = clazz;

        if (rClass == null && callback != null) {
            rClass = callback.getResultClass();
        }

        final Class resultClass = rClass;

        final JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                // here we need to deserialize the full result object,
                // which could be arbitrarily complex, so we take the hit of converting
                // back to a string, then running the full envelope
                // through the parser.
                logResult(response);
                String json = response.toString();
                BuddyResult<T> result = BuddyServiceClient.<T>parseBuddyResponse(resultClass, statusCode, json);
                if (callback != null) callback.completed(result);
                promise.setValue(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                JsonEnvelope<T> env = null;
                if (errorResponse != null) {
                    env = new JsonEnvelope<T>(errorResponse, null);
                    logResult(errorResponse);
                }
                else {
                    env = new JsonEnvelope<T>();
                    env.error = "NoInternetConnection";
                    env.message = "No internet connection is available.";
                }
                BuddyResult<T> result = new BuddyResult<T>(env);
                if (callback != null) callback.completed(result);
                promise.setValue(result);
                _parent.handleError(result);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // something bad happened.
                //
                JsonEnvelope<T> env = null;

                env = new JsonEnvelope<T>();
                env.error = "UnexpectedServiceError";
                env.message = responseString;

                BuddyResult<T> result = new BuddyResult<T>(env);
                if (callback != null) callback.completed(result);
                promise.setValue(result);
            }
        };


        Header[] headers = headerList.toArray(new Header[0]);

        headerList.add(new BasicHeader("Accept", DefaultContentType));

        final RequestParams requestParams = new RequestParams();
        AsyncHttpClient httpClient = getHttpClient();

        if (verb.toUpperCase(Locale.getDefault()).equals(GET)) {


            boolean isFile = resultClass != null && BuddyFile.class.isAssignableFrom(resultClass);
            if (parameters != null) {
                for (Map.Entry<String, Object> cursor : parameters.entrySet()) {
                    requestParams.put(cursor.getKey(), cursor.getValue());
                }
            }
            ResponseHandlerInterface handler = jsonHandler;

            Log.d("BuddySdk", String.format("%s %s", verb, url));

            if (isFile) {
                handler = new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        InputStream result = new ByteArrayInputStream(responseBody);

                        String contentTypeHeader = "application/octet-stream";

                        for (Header h : headers) {
                            if (h.getName().toLowerCase(Locale.getDefault()).equals("content-type")) {
                                contentTypeHeader = h.getValue();
                            }
                        }

                        BuddyFile file = new BuddyFile(result, contentTypeHeader);
                        JsonEnvelope env = new JsonEnvelope();
                        env.result = file;

                        BuddyResult<T> r = new BuddyResult<T>(env);

                        if (callback != null) callback.completed(r);
                        promise.setValue(r);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        jsonHandler.setUseSynchronousMode(this.getUseSynchronousMode());
                        // send failures over to the json handler.
                        jsonHandler.onFailure(statusCode, headers, responseBody, error);
                    }
                };
            }
            httpClient.get(null, url, headers, requestParams, handler);
        }
        else {

            // loop through and pull out any files.
            //
            HttpEntity entity = null;
            String contentType = DefaultContentType;

            if (parameters != null) {
                Map<String, Object> files = new HashMap<String, Object>();
                Map<String, Object> nonFiles = new HashMap<String, Object>();

                for (Map.Entry<String, Object> cursor : parameters.entrySet()) {

                    Object obj = cursor.getValue();

                    if (isFile(obj)) {
                        files.put(cursor.getKey(), obj);
                    } else {
                        nonFiles.put(cursor.getKey(), obj);
                    }
                }
                String bodyJson = new Gson().toJson(nonFiles);
                Log.d("BuddySdk", String.format("%s %s \r\n -> %s", verb, url, bodyJson));
                if (files.size() > 0) {
                    InputStream stream = new ByteArrayInputStream(bodyJson.getBytes());

                    // here we do N entities:
                    // 1. is called body with all the non file items
                    // 2. each file
                    requestParams.put("body", stream, "body", "application/json");

                    // now put the files
                    for (Map.Entry<String, Object> cursor : files.entrySet()) {

                        BuddyFile file = (BuddyFile) cursor.getValue();
                        if (file.getStream() != null) {
                            requestParams.put(cursor.getKey(), file.getStream(), cursor.getKey(), file.getContentType());
                        } else if (file.getFile() != null) {
                            try {
                                requestParams.put(cursor.getKey(), file.getFile(), file.getContentType());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        entity = requestParams.getEntity(new ResponseHandlerInterface() {
                            @Override
                            public void sendResponseMessage(HttpResponse response) throws IOException {

                            }

                            @Override
                            public void sendStartMessage() {

                            }

                            @Override
                            public void sendFinishMessage() {

                            }

                            @Override
                            public void sendProgressMessage(int bytesWritten, int bytesTotal) {
                                Log.d("BuddySdk", String.format("%d/%d", bytesWritten, bytesTotal));
                            }

                            @Override
                            public void sendCancelMessage() {

                            }

                            @Override
                            public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {

                            }

                            @Override
                            public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }

                            @Override
                            public void sendRetryMessage(int retryNo) {

                            }

                            @Override
                            public URI getRequestURI() {
                                return null;
                            }

                            @Override
                            public Header[] getRequestHeaders() {
                                return new Header[0];
                            }

                            @Override
                            public void setRequestURI(URI requestURI) {

                            }

                            @Override
                            public void setRequestHeaders(Header[] requestHeaders) {

                            }

                            @Override
                            public void setUseSynchronousMode(boolean useSynchronousMode) {

                            }

                            @Override
                            public boolean getUseSynchronousMode() {
                                return syncMode;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    contentType = null;
                } else {
                    try {
                        entity = new StringEntity(bodyJson);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            Method methodToInvoke = getClientMethod(verb);

            if (methodToInvoke == null) {

                jsonHandler.onFailure(0, new Header[0], "", new MethodNotSupportedException("Verb " + verb + " not supported."));
            }
            else {
                try {
                    try {

                        methodToInvoke.invoke(httpClient, null, url, headers, entity, contentType, jsonHandler);
                    } catch (InvocationTargetException e) {
                        JsonEnvelope<T> env = new JsonEnvelope<T>();
                        env.error = "UnexpectedSdkError";
                        env.status = 0;
                        env.errorCode = -1;
                        env.message = e.getTargetException().toString();

                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.getTargetException().printStackTrace(pw);
                        Log.e("BuddySdk", sw.toString());

                        BuddyResult<T> newResult = new BuddyResult<T>(env);

                        if (callback != null) callback.completed(newResult);
                        promise.setValue(newResult);
                    }
                } catch (IllegalAccessException e) {

                }
            }

        }
        return promise;
    }




    protected  <T> Future<BuddyResult<T>> makeRequest(final String verb, final String path, final Map<String, Object> parameters, final BuddyCallback<T> callback, final Class<T> clazz) {


        boolean autoRegister = true;

        if (parameters != null) {
            // should we disable auto register?
            //
            if (parameters.containsKey(BuddyClient.NoRegisterDevice)) {
                parameters.remove(BuddyClient.NoRegisterDevice);
                autoRegister = false;
            }
        }


        final BuddyFuture<BuddyResult<T>> promise = new BuddyFuture<BuddyResult<T>>();


        // get the access token.
        //
        _parent.getAccessToken(autoRegister, new AccessTokenCallback(){

                @Override
                public void completed(BuddyResult<Boolean> error, final String accessToken) {

                    if (error != null) {
                        // propagate the error
                        BuddyResult<T> newResult = error.convert((T)null);
                        if (callback != null) callback.completed(newResult);
                        promise.setValue(newResult);
                    }
                    else {
                        final BuddyFuture<BuddyResult<T>> innerPromise = BuddyServiceClient.this.<T>makeRequestCore(verb, path, accessToken, parameters, callback, clazz);

                        innerPromise.continueWith(new BuddyFutureCallback() {
                            @Override
                            public void completed(BuddyFuture future) {
                                try {
                                    promise.setValue(innerPromise.get());
                                } catch (InterruptedException e) {

                                } catch (ExecutionException e) {

                                }
                            }
                        });
                    }
                }
            }
        );
        return promise;

    }
}
