package com.afundacionfp.street_fight.interfaces;

import org.json.JSONObject;

public interface ResponseHandlerObject {
    void onOkResponse(JSONObject okResponseJson);
    void onErrorResponse(JSONObject errorResponseJson);
}
