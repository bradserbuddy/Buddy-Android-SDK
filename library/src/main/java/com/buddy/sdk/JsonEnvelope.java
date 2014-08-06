package com.buddy.sdk;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

class JsonEnvelope<T> extends JsonEnvelopeBase {
	
	public T result;

    public JsonEnvelope() {

    }

    public JsonEnvelope(JsonObject json, T result) {

        JsonElement errorElement = json.get("error");
        JsonElement errorNumberElement = json.get("errorNumber");
        JsonElement messageElement = json.get("message");
        JsonElement requestIdElement = json.get("request_id");
        JsonElement statusElement = json.get("status");

        if (errorElement != null) error = errorElement.getAsString();
        if (errorNumberElement != null) errorCode = errorNumberElement.getAsInt();
        if (messageElement != null) message = messageElement.getAsString();
        if (statusElement != null) status = statusElement.getAsInt();
        if (requestIdElement != null) request_id = requestIdElement.getAsString();


        this.result = result;
    }

    public JsonEnvelope(JSONObject json, T result) {
        error = json.optString("error");
        errorCode = json.optInt("errorNumber");
        message = json.optString("message");
        status = json.optInt("status");
        request_id = json.optString("request_id");
        this.result = result;
    }

    public <T2> JsonEnvelope<T2> convert(T2 newValue) {
        JsonEnvelope<T2> newEnvelope = new JsonEnvelope<T2>();
        newEnvelope.request_id = request_id;
        newEnvelope.status = status;
        newEnvelope.error = error;
        newEnvelope.message = message;
        newEnvelope.errorCode = errorCode;
        newEnvelope.result = newValue;
        return newEnvelope;
    }
}

