package com.buddy.sdk;

import android.location.Location;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


class BuddyLocationDeserializer implements JsonDeserializer<Location>
{
    private Class clazz;


    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException
    {


        JsonObject jsonObj = json.getAsJsonObject();

        if (jsonObj != null && jsonObj.has("lat") && jsonObj.has("lng")) {

            Location l = new Location("Buddy");
            l.setLatitude(jsonObj.get("lat").getAsDouble());
            l.setLongitude(jsonObj.get("lng").getAsDouble());
            return l;
        }

        throw new JsonParseException("Invalid location: " + json.toString());

    }
}

