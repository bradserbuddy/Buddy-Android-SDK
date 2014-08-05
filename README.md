# Buddy Android SDK

These release notes are for the Buddy Platform Android SDK.

Please refer to [buddyplatform.com/docs](https://buddyplatform.com/docs) for more details on the Android SDK.

## Introduction

We realized most app developers end up writing the same code over and over again: user management, photo management, geolocation, checkins, metadata, and other basic features. Buddy enables developers to build cloud-connected apps without having to write, test, manage or scale server-side code and infrastructure.

Buddy's easy-to-use, scenario-focused APIs let you spend more time building your app and less time worrying about backend infrastructure.

This SDK is a thin wrapper over the Buddy REST API that takes care of the hard parts for you:

* Building and formatting requests
* Managing authentication
* Parsing responses
* Loading and saving credentials

The remainder of the Buddy API is accessible via standard REST API calls.

## Getting Started

To get started with the Buddy Platform SDK, please reference the _Getting Started_ series of documents at [buddyplatform.com/docs](https://buddyplatform.com/docs). You will need an App ID and Key before you can use the SDK. The _Getting Started_ documents will walk you through obtaining everything you need and show you where to find the SDK for your platform.

Application IDs and Keys are obtained at the Buddy Developer Dashboard at [buddyplatform.com](https://buddyplatform.com/login).

Full documentation for Buddy's services are available at [buddyplatform.com/docs](https://buddyplatform.com/docs).

## Installing the SDK

### Install from Maven/Gradle

In your build.gradle file under 'src', add a line for the Buddy Android SDK dependency

    dependencies {
        Compile fileTree(dir: 'libs', include: ['*.jar'])
        Compile 'com.buddy:androidsdk:+'
    }

This will install the latest release of the Buddy Android SDK.
**Note:** If you wish to limit yourself to a narrower set of releases, you can do so like this (e.g. the latest release in the 0.1 series):

    Compile 'com.buddy:androidsdk:0.1.+'

At this point you will be able to import from com.buddy.sdk to access the Buddy Classes
(e.g. import com.buddy.sdk.BuddyClient)

### Install Locally

#### Prerequisites

To build the SDK you need to:

1.  Install the [Android SDK Tools](http://developer.android.com/sdk/index.html)
2.  Set the ANDROID_HOME environment variable to the Android SDK install directory
3.  Install Android SDK build tools version 19.1.0, and SDK Platform for API 10 (Android 2.3.3) 

#### Build and Install

1.  Clone this repository to your local machine
2.  From the root of this repository, run `./gradlew build` (Mac/Linux) or `gradlew.bat build` (Windows) to build the SDK
3.  Look in the **library/build/libs** folder to find the JARs.
4.  Add The buddy-sdk-_version_.jar file as a dependency for your Android application.

## Using the Android SDK

Visit the [Buddy Dashboard](https://buddyplatform.com) to obtain your application ID and key.

### Initialize The SDK

    import com.buddy.sdk;
    // ...
    // Create the SDK client
    BuddyClient client = Buddy.init(myContext, "appId", "appKey");
    
If you need to have multiple clients, for example if you need to talk to multiple users from your app, you can capture the result from `Buddy.init` and call through those clients:

    BuddyClientOptions opt1 = new BuddyClientOptions();
    opt1.instanceName = "client1";
    
    BuddyClient client1 = Buddy.init(app1, key1, opt1);
    
    BuddyClientOptions opt2 = new BuddyClientOptions();
    opt1.instanceName = "client2";
    
    BuddyClient client2 = Buddy.init(app1, key1, opt2);
    
    client1.loginUser("user1", "pw1", null);
    client2.loginUser("user2", "pw2", null);
    
The `Buddy` static class is has the same signature as the `BuddyClient` class, and is shorthand for calling the most recently created client via a `Buddy.init()` call.

### Create/Login Users

There are helper functions for creating, logging in, and logging out users:

#### Create User

    Buddy.createUser("someUser", "somePassword", null, null, null, null, null, null, new BuddyCallback<User>(User.class) {
      @Override
      public void completed(BuddyResult<User> result) {
        if (result.getIsSuccess()) {
          TextView tv = (TextView)findViewById(R.id.textView1);
          tv.setText("Hello " + result.getResult().username);
        }
      }
    });

#### User Login

    Buddy.loginUser("someUser", "somePassword", new BuddyCallback<User>(User.class) {...});

#### User Logout
    
    // Logout is simple!
    Buddy.logoutUser();
	
### REST Interface
	  
Each SDK provides general wrappers that make REST calls to Buddy. For all the calls you can either create a wrapper java class such as those found in `com.buddy.sdk.models`, or you can simply pass a type of `JsonObject` to return a standard Gson JsonObject.

#### GET

This sample searches app-level metadata by `keyPrefix` for any keys that start with "dataPoint_". See [Search Metadata](https://buddyplatform.com/docs/Search%20Metadata) for a full list of parameters.

    // Search Metadata by key prefix
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("keyPrefix", "dataPoint_");
    Buddy.<JsonObject>get("/metadata/app", parameters, new BuddyCallback<JsonObject>(JsonObject.class) {
      @Override
      public void completed(BuddyResult<JsonObject> result) {
        if (result.getIsSuccess()) {
          JsonObject obj = result.getResult();
          // Get the first result from the pageResults response (this assumes we have at least one result from the query)
          String _d = obj.getMember("pagedResults")[0].getAsString();
        }
      }
    });

#### POST

In this example we will create a checkin. Take a look at the [Create Checkin documentation](https://buddyplatform.com/docs/Create%20Checkin/HTTP), then:

    // Create a checkin
    Location location = getTheDeviceLocation();
    Map<String,Object> parameters = new HashMap<String,Object>();
    parameters.put("comment", "My first checkin");
    parameters.put("description", "This is where I was doing that thing.");
    parameters.put("location", String.format("%f,%f", location.getLatitude(), location.getLongitude());
    Buddy.<JsonObject>post("/checkins", parameters, new BuddyCallback<JsonObject>(JsonObject.class) {
      @Override
      public void completed(BuddyResult<JsonObject> result) {
        if (result.getIsSuccess()) {
          JsonObject obj = result.getResult();
          // get the ID of the created checkin.
          String id = obj.getMember("id").getAsString();
        }
      }
    });

#### PUT/PATCH/DELETE

Each remaining REST verb is available through the Buddy SDK using the same pattern as the POST example.

### Working With Files

Buddy offers support for binary files. The Android SDK works with files through our REST interface similarly to other API calls.

#### Upload A File

The Buddy Android SDK handles all necessary file management for you. The key class is `com.buddy.sdk.BuddyFile`, which is a wrapper around an Android `File` or `InputStream`, along with a MIME content type. Here we demonstrate uploading a picture. For all binary files (e.g. blobs and videos), the pattern is the same, but with a different path and different parameters.

    BuddyFile file = new BuddyFile(new File("/some/image/foo.jpg"), "image/jpg");
    Map<String,Object> parameters = new HashMap<String,Object>();
    parameters.put("caption", "My first image");
    parameters.put("data", file);
    Buddy.<Picture>post("/pictures", parameters, new BuddyCallback<Picture>(Picture.class){
        @Override
        public void completed(BuddyResult<Picture> result) {
            if (result.getIsSuccess()) {...}
        }
    });

#### Download A File

Our download example uses pictures. To download a picture specify BuddyFile as the operation type:

    // Calling GET on "/pictures/{id}/file" returns a 302 response with a redirect URL that needs to be handled by your app
    Buddy.get(String.format("/pictures/%f/file", pictureId), null, new BuddyCallback<BuddyFile>(BuddyFile.class) {
      @Override
      public void completed(BuddyFile file) {
        // Do something with your picture!
      }
    });

**Note:** Responses for files deviate from the standard Buddy response templates. See the [Buddy Platform documentation](https://buddyplatform.com/docs) for more information.
	
### Creating Response Objects

Creating strongly typed response objects is simple.  If the REST operation that you intend to call returns a response that's not available in `com.buddy.sdk.models`, you can easily create one by creating a Java object with fields that match the JSON response fields for the operation.

1.  Go to the Buddy Console and try your operation
2.  When the operation completes, note the fields and their types in the response
3.  Create a Java class that derives from `com.buddy.sdk.models.ModelBase` with the appropriate properties.

For example, the response to **POST /checkins** looks like:

     {
       "status": 201,
       "result": {
         "comment": "h1",
         "userID": "bv.HrcbbDkMPgfn",
         "id": "cb.gBgbvKFkdhnp",
         "location": {
           "lat": 46.2,
           "lng": -120.1
          },
         "created": "2014-07-09T07:07:21.463Z",
         "lastModified": "2014-07-09T07:07:21.463Z"
     },
     "request_id": "53bcea29b32fad0c405372b6",
     "success": true
    }

The corresponding Java object for the unique field under `result` is:

    public class Checkin extends ModelBase {
        public String comment;
    }
    
**Note:** we do not need to specify the default common properties `id`, `userID`, `location`, `created`, or `lastModified`.

We can then call:

     Buddy.<BuddyResult<Checkin>>get(String.format("/checkins/%f", checkinId), null, new BuddyCallback<Checkin>(Checkin.class){...});

## Contributing Back: Pull Requests

We'd love to have your help making the Buddy SDK as good as it can be!

To submit a change to the Buddy SDK please do the following:

1. Create your own fork of the Buddy SDK
2. Make the change to your fork
3. Before creating your pull request, please sync your repository to the current state of the parent repository: `git pull origin master`
4. Commit your changes, then [submit a pull request](https://help.github.com/articles/using-pull-requests) for just that commit

## License

#### Copyright (C) 2014 Buddy Platform, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.

