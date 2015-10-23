### Overview

The Buddy Android SDK helps you get up and running in seconds.  

For the most part, the Buddy Android SDK takes care of all the housekeeping of making requests against the Buddy REST API:

* Building and formatting requests
* Managing authentication
* Parsing responses
* Loading and saving credentials

With that handled, all you have to do is initialize the SDK and start making some calls!


### Getting Started

To get started with the Buddy Platform SDK, please reference the _Getting Started_ series of documents at [buddyplatform.com/docs](https://buddyplatform.com/docs). You will need an Application ID and Key before you can use the SDK. The _Getting Started_ documents will walk you through obtaining everything you need and show you where to find the SDK for your platform.

Application IDs and Keys are obtained at the Buddy Developer Dashboard at [buddyplatform.com](https://buddyplatform.com/login).

Full documentation for Buddy's services are available at [buddyplatform.com/docs](https://buddyplatform.com/docs).

### Installing the SDK

#### Install from Maven/Gradle
1) Ensure that you have [Android Studio](https://developer.android.com/sdk/installing/studio.html) installed.

2) Open your project in Android Studio. In your build.gradle file, under 'dependencies', add a line for the Buddy Android SDK dependency:

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        compile 'com.buddy:androidsdk:+'
    }

3) Go to File > Project Structure and select the Project tab. Change the `Default Library Repository` from `jcenter()` to `mavenCentral`.

This will install the latest release of the Buddy Android SDK.
**Note:** If you wish to limit yourself to a narrower set of releases, you can do so like this (e.g. the latest release in the 3.0.0 series):

    compile 'com.buddy:androidsdk:3.0.0+'

At this point you will be able to import from com.buddy.sdk to access the Buddy Classes
(e.g. `import com.buddy.sdk.BuddyClient`)

#### Building Source

Buddy hosts our SDK source on GitHub. To access it you need to have [Git](http://git-scm.com/download) installed. If you'd like to contribute SDK modifications or additions to Buddy, you'll want to [fork the repository](https://help.github.com/articles/fork-a-repo) so you can issue [pull requests](https://help.github.com/articles/be-social#pull-requests). See the "Contributing Back" section below for details.

From a cmd window or terminal, type:

    git clone https://github.com/BuddyPlatform/Buddy-Android-SDK.git

This will copy the latest source of the SDK into a directory called *Buddy-Android-SDK*.

#### Install Locally

##### Prerequisites

To build the SDK you need to:

1.  Install [Android Studio](http://developer.android.com/sdk/index.html). You may be asked to install a compatible version of the Java SE Development Kit, if you don't already have it installed.
2.  Open the Android SDK Manager and make sure you have installed Android SDK Build-tools 23.0.1 and Android 4.4.2 (API 19).

##### Build and Install

1.  Open a terminal window (Mac) or a cmd window (Windows).
2.  From the root of the Buddy-Android-SDK repository, run `./gradlew build` (Mac/Linux) or `gradlew.bat build` (Windows) to build the SDK
3.  Look in the `library/build/libs` directory to find the built JARs.
4.  Add the buddy-sdk-_version_.jar file (e.g. buddy-sdk-3.0.4.jar) as a dependency for your Android application by modifying the `dependencies { ... }` block of your application's build.gradle file. The `compile files` line should contain the relative path to the Buddy SDK .jar file. Be sure to add the `android-async-http` and `gson` dependencies:

    dependencies {
        compile fileTree(dir: 'libs', include: ['*.jar'])
        // Add the following dependencies:
        compile 'com.loopj.android:android-async-http:1.4.8'
        compile 'com.google.code.gson:gson:2.3'
        compile files('../../../library/build/libs/buddy-sdk-3.0.4.jar')
    }

### Using the Android SDK

Visit the [Buddy Dashboard](https://buddyplatform.com) to obtain your application ID and key.

#### Initialize the SDK

This should either be done in the `onCreate` method of your Application class (if you have one), or in the `onCreate` method of your launch Activity. You should pass in an Application or Activity Context if it's available:

    import com.buddy.sdk.Buddy;
    ...
    Context myContext = getApplicationContext(); // If there is no context, set myContext to null
    Buddy.init(myContext, "appId", "appKey");
    
#### User Flow

There are helper functions for creating, logging in, and logging out users, and a callback to manage login state:

##### Create User

    Buddy.createUser("someUser", "somePassword", null, null, null, null, null, null, new BuddyCallback<User>(User.class) {
      @Override
      public void completed(BuddyResult<User> result) {
        if (result.getIsSuccess()) {
          Log.w(APP_LOG, "User created: " + result.getResult().userName);
        }
      }
    });

##### User Login

    Buddy.loginUser("someUser", "somePassword", new BuddyCallback<User>(User.class) {...});

##### User Logout
    
    Buddy.logoutUser();

##### User Login\Logout Callback

We recommend implementing a `UserAuthenticationRequiredCallback` callback. It will get called whenever you make a Buddy call that needs an authorized user, and a user isn't logged in. That way, you won't have to manage user login state. Here's an example:

    Buddy.setUserAuthenticationRequiredCallback(new UserAuthenticationRequiredCallback() {
        @Override
        public void authenticate() {
            // The below is an example of how to call a 'login' Activity
            Intent loginIntent = new Intent(MyBuddyApplication.this, Login.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
        }
    });
        
#### REST Interface
    
Each SDK provides general wrappers that make REST calls to Buddy. For call responses, you can either create a wrapper Java class such as those found in `com.buddy.sdk.models`, or you can simply pass a type of `JsonObject` to return a standard [Gson](https://github.com/google/gson) JsonObject.

##### POST

In this example we will create a checkin. Take a look at the [Create Checkin documentation](https://buddyplatform.com/docs/Checkins#CreateCheckin), then:

    Location location = new Location("");
    location.setLatitude(47.61d);
    location.setLongitude(-122.33d);
    
    Map<String,Object> parameters = new HashMap<String,Object>();
    parameters.put("comment", "My first checkin");
    parameters.put("description", "This is where I was doing that thing.");
    parameters.put("location", location);
    
    Buddy.<Checkin>post("/checkins", parameters, new BuddyCallback<Checkin>(Checkin.class) {
        @Override
        public void completed(BuddyResult<Checkin> result) {
            if (result.getIsSuccess()) {
                Checkin checkin = result.getResult();
                // get the ID of the created checkin.
                String id = checkin.id;
            }
        }
    });

##### GET

This example searches for the checkin we created in the POST example. See [Search Checkins](https://buddyplatform.com/docs/Checkins#SearchCheckins) for a full list of parameters.

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("locationRange", "47.61, -122.33, 1500");
    
    Buddy.<JsonObject>get("/checkins", parameters, new BuddyCallback<JsonObject>(JsonObject.class) {
        @Override
        public void completed(BuddyResult<JsonObject> result) {
            if (result.getIsSuccess()) {
                JsonObject obj = result.getResult();
                // Get the pageResults response as a JsonArray
                JsonArray _res = obj.get("pageResults").getAsJsonArray();
                Log.w(APP_LOG, _res.toString());
            }
        }
    });

##### PUT/PATCH/DELETE

Each remaining supported REST verb is available through the Buddy SDK using the same pattern as the POST and GET examples.

#### File Management

Buddy offers support for binary files. The Android SDK works with files through our REST interface similarly to other API calls.

##### Upload A File

The Buddy Android SDK handles all necessary file management for you. The key class is `com.buddy.sdk.BuddyFile`, which is a wrapper around an Android `File` or `InputStream`, along with a MIME content type. Here we demonstrate uploading a picture. All binary files use the same pattern, with a different API path and different parameters. Here's an example of uploading a picture:

    BuddyFile buddyFile = new BuddyFile(new File(...), "image/jpg");
    Map<String,Object> parameters = new HashMap<String,Object>();
    parameters.put("caption", "From Android");
    parameters.put("data", buddyFile);
    
    String pictureId;
    Buddy.<Picture>post("/pictures", parameters, new BuddyCallback<Picture>(Picture.class){
        @Override
        public void completed(BuddyResult<Picture> result) {
            if (result.getIsSuccess()) {
                pictureId = result.getResult().id;
                Log.w(APP_LOG, "It worked!");
            }
        }
    });

##### Download A File

To download a file send a GET request with BuddyFile as the operation type. This sample downloads the picture we uploaded in the "Upload a File" example:

    Buddy.get(String.format("/pictures/%s/file", pictureId), null, new BuddyCallback<BuddyFile>(BuddyFile.class) {
        @Override
        public void completed(BuddyResult<BuddyFile> file) {
            // Do something with your picture!
            Log.w(APP_LOG, file.getResult().getContentType());
            // The picture's binary data is in the stream property
            InputStream stream = file.getResult().getStream();
        }
    });

#### Advanced Usage

##### Automatically report location for each Buddy call

If you set the current location, each time a Buddy call is made, that location will be passed in the call. Most calls that send data to Buddy have a location parameter; if a call is made that doesn't take location, the parameter will be ignored.

    Location loc = new Location("My Location");
    loc.setLatitude(47);
    loc.setLongitude(-122);
    Buddy.setLastLocation(loc);

##### Multiple concurrent users

If you need to have multiple clients, for example if you need to interact with multiple users concurrently from your app, you can capture clients created from `Buddy.init` and use those clients individually:

    BuddyClientOptions opt1 = new BuddyClientOptions();
    opt1.instanceName = "client1";
    
    BuddyClient client1 = Buddy.init(app1, key1, opt1);
    
    BuddyClientOptions opt2 = new BuddyClientOptions();
    opt1.instanceName = "client2";
    
    BuddyClient client2 = Buddy.init(app1, key1, opt2);
    
    client1.loginUser("user1", "pw1", null);
    client2.loginUser("user2", "pw2", null);
    
The `Buddy` static class has the same signature as the `BuddyClient` class, and is shorthand for calling the most recently created client via a `Buddy.init()` call.

##### Handling connectivity

You can set the `ConnectivityLevelChangedCallback` callback if you would like to be notified if your device loses and regains ability to communicate to the Buddy servers for whatever reason. Here's an example that notifies the user:

    Buddy.setConnectivityLevelChangedCallback(new ConnectivityLevelChangedCallback() {
        @Override
        public void connectivityLevelChanged(ConnectivityLevel level) {
            String message = (level == ConnectivityLevel.None) ? "Connectivity lost..." : "Reconnected!";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }
    });

### Sample Apps

#### BuddyChat

BuddyChat is an example of how to use Buddy's push notification and messaging support to create a simple chat application.

### Contributing Back: Pull Requests

We'd love to have your help making the Buddy SDK as good as it can be!

To submit a change to the Buddy SDK please do the following:

1) Create your own fork of the Buddy SDK

2) Make the change to your fork

3) Before creating your pull request, please sync your repository to the current state of the parent repository: ```git pull origin master```

4) Commit your changes, then [submit a pull request](https://help.github.com/articles/using-pull-requests) for just that commit