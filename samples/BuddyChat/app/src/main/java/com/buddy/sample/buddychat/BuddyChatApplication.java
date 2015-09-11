package com.buddy.sample.buddychat;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyResult;
import com.buddy.sdk.ConnectivityLevel;
import com.buddy.sdk.ConnectivityLevelChangedCallback;
import com.buddy.sdk.UserAuthenticationRequiredCallback;
import com.buddy.sdk.models.User;
import android.widget.Toast;
import android.os.Looper;
import android.view.Gravity;

public class BuddyChatApplication extends Application {

    /**
     * Substitute your own sender ID here. This is the Project Number you got
     * from the Google Developers Console, as described in the accompanying README.md.
     */
    public static final String SENDER_ID = "MY_SENDER_ID";


    /**
     * Substitute your Buddy app's App ID and App Key here. You can create a Buddy app
     * at http://dev.buddyplatform.com. For more details see the accompanying README.md.
     */
    public static final String APPID = "bbbbbc.hKpFwzglHBrK";
    public static final String APPKEY = "616bf446-f005-fe9a-9fd4-e62ba8913bf5";
    //public static final String APPID = "MY_APP_ID";
    //public static final String APPKEY = "MY_APP_KEY";


    public static BuddyChatApplication instance;
    public static Chat activeChat;
    public User currentUser;
    boolean loginVisible;

    public BuddyChatApplication() {
        instance = this;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Buddy.init(getApplicationContext(), APPID, APPKEY);

        // Automatically show the Login activity whenever
        // authentication fails for a user-level API call
        Buddy.setUserAuthenticationRequiredCallback(new UserAuthenticationRequiredCallback() {
            @Override
            public void authenticate() {
                if (loginVisible) {
                    return;
                }
                loginVisible = true;
                Intent loginIntent = new Intent(BuddyChatApplication.this, Login.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
            }
        });

        Buddy.setConnectivityLevelChangedCallback(new ConnectivityLevelChangedCallback() {
            @Override
            public void connectivityLevelChanged(ConnectivityLevel level) {
            String message = getResources().getString((level == ConnectivityLevel.None) ?
                    R.string.connection_lost :
                    R.string.reconnected);

            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
            }
        });
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void getCurrentUser(final boolean refresh, final GetCurrentUserCallback callback) {

        if (currentUser != null && !refresh) {
            if (callback != null) {
                callback.complete(currentUser);
            }
        } else {
            Buddy.getCurrentUser(new BuddyCallback<User>(User.class) {
                @Override
                public void completed(BuddyResult<User> result) {
                    if (result.getIsSuccess() && result.getResult() != null) {
                        currentUser = result.getResult();
                    }
                    if (callback != null) {
                        callback.complete(currentUser);
                    }
                }
            });
        }
    }
}
