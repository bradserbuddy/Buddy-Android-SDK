package com.buddy.sdk;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BuddyDateDeserializer implements JsonDeserializer<Date>
{
    private Class clazz;

    private final static Pattern pattern = Pattern.compile("/Date\\((-?\\d+)\\)/");


    public static Date deserialize(String str) {
        Matcher m = pattern.matcher(str);

        if (m.find()) {

            Long unixDate = Long.decode(m.group(1));

            return new Date(unixDate);
        }
        return null;
    }



    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException
    {
        String str = json.getAsString();

        return deserialize(str);


    }
}
