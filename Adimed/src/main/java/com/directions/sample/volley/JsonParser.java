package com.directions.sample.volley;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2016-01-15.
 */
public class JsonParser {

    private String distance, duration, distanceText, durationText, destinationAddres, originAddress, status;



    public static final String TAG = "JsonParser";

    public static RouteResult parseResponse(String response){

       RouteResult routeResult = new RouteResult();

        if (response != null) {

            try {
                JSONObject jObject = new JSONObject(response);
                Log.d(TAG, "response " + response);

                routeResult.setStatus(jObject.getString("status"));

                // checking the status
                if (jObject.getString("status").equals("OK")) {

                    routeResult.setDestinationAddresses(jObject.getString("destination_addresses"));
                    routeResult.setOriginAddresses(jObject.getString("origin_addresses"));

                    // Parsing the value from row array
                    JSONArray jaArray = jObject.getJSONArray("rows");


                    for (int i = 0; i < jaArray.length(); i++) {

                        JSONObject jobj = jaArray.getJSONObject(i);

                        JSONArray jaArray2 = jobj.getJSONArray("elements");

                        for (int j = 0; i < jaArray2.length(); j++) {

                            JSONObject jobj1 = jaArray2.getJSONObject(j);

                            JSONObject jobj_distance = jobj1
                                    .getJSONObject("distance");

                            JSONObject jobj_duration = jobj1
                                    .getJSONObject("duration");

                            routeResult.setDistanceValue(jobj_distance.getString("value"));
                            routeResult.setDurationValue(jobj_duration.getString("value"));

                            routeResult.setDistanceText(jobj_distance.getString("text"));
                            routeResult.setDurationText(jobj_duration.getString("text"));

                        }

                    }

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }

        }

        return routeResult;
    }
}
