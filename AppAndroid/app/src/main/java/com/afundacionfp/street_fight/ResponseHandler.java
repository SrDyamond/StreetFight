package com.afundacionfp.street_fight;

import org.json.JSONObject;

public interface ResponseHandler {
    void onOkResponse(JSONObject okResponseJson);
    void onErrorResponse(JSONObject errorResponseJson);
}
