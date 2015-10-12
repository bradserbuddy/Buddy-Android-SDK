package com.buddy.sdk.models;

import android.location.Location;

public class Checkin extends ModelBase {

    public class CheckinLocation extends Location {

        public CheckinLocation() {
            super("Buddy");
        }
        public String name;
        public String id;
    }

    public String comment;
    public String description;
}
