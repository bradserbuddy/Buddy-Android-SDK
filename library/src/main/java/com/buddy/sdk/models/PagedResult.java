package com.buddy.sdk.models;
import com.buddy.sdk.JsonEnvelopeDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;


/**
 * Created by nick on 8/25/14.
 */
public class PagedResult {


    public String nextToken;
    public String previousToken;
    public String currentToken;
    public List<JsonElement> pageResults;

    public <T> List<T> ConvertPageResults(Type typeOfT) {

        List<T> result = new ArrayList<T>();

        if(typeOfT==null) {
            return result; // Or throw ?
        }
        Gson gson = JsonEnvelopeDeserializer.makeGsonDeserializer();

        for(JsonElement jObj : pageResults){
            T currentObj = gson.fromJson(jObj,typeOfT);
            result.add(currentObj);
        }
        return result;
    }

}
