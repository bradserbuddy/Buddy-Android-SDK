package com.buddy.sdk;

abstract  class AccessTokenCallback  {
    public abstract void completed(BuddyResult<Boolean> error, String accessToken);
}