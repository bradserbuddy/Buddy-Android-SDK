package com.buddy.sdk;

/**
 * Created by shawn on 7/8/14.
 */
public class BuddyClientOptions {

    public String settingsPrefix;
    public boolean persistToken;
    public String serviceRoot;
    public String deviceTag;
    public String appVersion;
    public boolean synchronousMode;
    public String instanceName;

    // Not serialized to persistent store
    public String sharedSecret;
}
