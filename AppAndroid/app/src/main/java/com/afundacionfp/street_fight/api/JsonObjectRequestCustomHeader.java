package com.afundacionfp.street_fight.api;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectRequestCustomHeader extends JsonObjectRequest {
    private String session_cookie;

    public JsonObjectRequestCustomHeader(int method, String url, String session_cookie, @Nullable JSONObject jsonRequest, Response.Listener<JSONObject> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        this.session_cookie = session_cookie;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> overriddenHeaders = new HashMap<>();
        for (Map.Entry<String, String> entry : super.getHeaders().entrySet()) {
            overriddenHeaders.put(entry.getKey(), entry.getValue());
        }
        if (session_cookie != null) {
            overriddenHeaders.put("sessioncookie", session_cookie);
        }
        return overriddenHeaders;
    }
}
