package com.afundacionfp.street_fight.interfaces;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ResponseHandlerArray {
    void onOkResponse(JSONArray okResponseJson);
    void onErrorResponse(JSONObject errorResponseJson);
}
