package com.buddy.sdk.models;

import com.buddy.sdk.JsonEnvelopeDeserializer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PagedResult {


    public String nextToken;
    public String previousToken;
    public String currentToken;
    public List<JsonObject> pageResults;

    public <T extends ModelBase> List<T> convertPageResults(Class<T> clazz) {
        List<T> result = new ArrayList<T>();

        Gson gson = JsonEnvelopeDeserializer.makeGsonDeserializer();

        Type ty =  TypeToken.get(clazz).getType();

        for(JsonObject jObj : pageResults){
            T currentObj = gson.fromJson(jObj,ty);
            currentObj.setJsonObject(jObj);
            result.add(currentObj);
        }
        return result;
    }

}