package com.directions.sample.volley;

import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 2016-01-15.
 */
public class JsonParser {

    private String distance, duration, distanceText, durationText, destinationAddres, originAddress, status;



    public static final String TAG = "JsonParser";

    public static DistanceResponse parseResponse(String response){

       DistanceResponse distanceResponse = new DistanceResponse();

        if (response != null) {

            try {
                JSONObject jObject = new JSONObject(response);
                Log.d(TAG, "response " + response);

                distanceResponse.setStatus(jObject.getString("status"));

                // checking the status
                if (jObject.getString("status").equals("OK")) {

                    distanceResponse.setDestinationAddresses(jObject.getString("destination_addresses"));
                    distanceResponse.setOriginAddresses(jObject.getString("origin_addresses"));

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

                            distanceResponse.setDistanceValue(jobj_distance.getString("value"));
                            distanceResponse.setDurationValue(jobj_duration.getString("value"));

                            distanceResponse.setDistanceText(jobj_distance.getString("text"));
                            distanceResponse.setDurationText(jobj_duration.getString("text"));

                        }

                    }

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }

        }

        return distanceResponse;
    }
}
