package com.buddy.sdk;

import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.buddy.sdk.models.TimedMetric;
import com.buddy.sdk.models.User;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Future;

public class Buddy {
    private static BuddyClient _currentClient;

    private static Map<String, BuddyClient> _clients = new HashMap<String, BuddyClient>();

    private static String GetClientKey(String appId, BuddyClientOptions options)
    {
        return appId + options.instanceName;
    }

    public static BuddyClient init(Context context, String appId, String appKey, BuddyClientOptions options)
    {

        BuddyClient client = null;

        String key = GetClientKey(appId, options);

        if(_clients.containsKey(key))
        {
            client = _clients.get(key);
        }
        else {
            client = new BuddyClient(context, appId, appKey, options);
            _clients.put(key, client);
        }

        _currentClient = client;

        return client;
    }

    public static <T> Future<BuddyResult<T>> get(String path, Map<String, Object> parameters, Class<T> clazz)
    {
        return _currentClient.<T>get(path, parameters, clazz);
    }

    public static <T> Future<BuddyResult<T>> get(String path, Map<String, Object> parameters, final BuddyCallback<T> callback)
    {
        return _currentClient.<T>get(path, parameters, callback);
    }

    public static <T> Future<BuddyResult<T>> post(String path, Map<String, Object> parameters, Class<T> clazz)
    {
        return _currentClient.<T>post(path, parameters, clazz);
    }

    public static <T> Future<BuddyResult<T>> post(String path, Map<String, Object> parameters, final BuddyCallback<T> callback)
    {
        return _currentClient.<T>post(path, parameters, callback);
    }

    public static <T> Future<BuddyResult<T>> patch(String path, Map<String, Object> parameters, Class<T> clazz)
    {
        return _currentClient.<T>patch(path, parameters, clazz);
    }

    public static <T> Future<BuddyResult<T>> patch(String path, Map<String, Object> parameters, final BuddyCallback<T> callback)
    {
        return _currentClient.<T>patch(path, parameters, callback);
    }

    public static <T> Future<BuddyResult<T>> delete(String path, Map<String, Object> parameters, Class<T> clazz)
    {
        return _currentClient.<T>delete(path, parameters, clazz);
    }

    public static <T> Future<BuddyResult<T>> delete(String path, Map<String, Object> parameters, final BuddyCallback<T> callback)
    {
        return _currentClient.<T>delete(path, parameters, callback);
    }

    public static <T> Future<BuddyResult<T>> put(String path, Map<String, Object> parameters, Class<T> clazz)
    {
        return _currentClient.<T>put(path, parameters, clazz);
    }

    public static <T> Future<BuddyResult<T>> put(String path, Map<String, Object> parameters, final BuddyCallback<T> callback)
    {
        return _currentClient.<T>put(path, parameters, callback);
    }

    public static void setUserAuthenticationRequriedCallback(UserAuthenticationRequiredCallback callback)
    {
        _currentClient.setUserAuthenticationRequiredCallback(callback);
    }

    public static void setLastLocation(Location loc){
        _currentClient.setLastLocation(loc);
    }

    public static Location getLastLocation()
    {
        return _currentClient.getLastLocation();
    }

    public static String getServiceRoot()
    {
        return _currentClient.getServiceRoot();
    }

    public static Future<BuddyResult<User>> getCurrentUser(final BuddyCallback<User> callback){
        return _currentClient.getCurrentUser(callback);
    }

    public static Future<BuddyResult<User>> createUser(String username, String password,
            String firstName, String lastName, String email, Date dateOfBirth, String gender,
            String tag, final BuddyCallback<User> callback)
    {
        return _currentClient.createUser(username, password, firstName, lastName, email, dateOfBirth,
                gender, tag, callback);
    }

    public static Future<BuddyResult<User>> loginUser(String username, String password, final BuddyCallback<User> callback)
    {
        return _currentClient.loginUser(username, password, callback);
    }

    public static Future<BuddyResult<Boolean>> logoutUser(final BuddyCallback<Boolean> callback)
    {
        return _currentClient.logoutUser(callback);
    }

    public static Future<BuddyResult<TimedMetric>> recordMetricEvent(String eventName, Map<String,Object> values,
        final int timeoutInSeconds, final BuddyCallback<TimedMetric> callback)
    {
        return _currentClient.recordMetricEvent(eventName, values, timeoutInSeconds, callback);
    }

    public static Future<BuddyResult<Boolean>> setPushToken(String pushToken, final BuddyCallback<Boolean> callback)
    {
        return _currentClient.setPushToken(pushToken, callback);
    }

    public static void recordNotificationReceived(Intent message)
    {
        _currentClient.recordNotificationReceived(message);
    }
}