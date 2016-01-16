package com.directions.sample.utils;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by hswie on 2016-01-14.
 */
public class FareCalculation {

    private static final String TAG = "FareCalculation";

    public static float calculatreDurationInHourFromSeconds(String duration){

        float dur_in_hr;
        dur_in_hr = Math.abs(Integer.valueOf(duration) * 1 / (60 * 60));

        return dur_in_hr;
    }

    public static float calculatorDistanceInKilometers(String distance){

        float distanceKm = Float.valueOf(distance) / 1000;

        return distanceKm;
    }

    public static String estimatedFare(Float dur_in_hr, Float distance_in_kilometeres, int paramedicCount, float paramedicHourCost, float kilometerCost, int additionalTime, boolean isReturn){

        Log.d(TAG, "dur_in_hr " + dur_in_hr);
        Log.d(TAG, "distance_in_kilometeres " + distance_in_kilometeres);
        Log.d(TAG, "paramedicCount " + paramedicCount);
        Log.d(TAG, "paramedicHourCost " + paramedicHourCost);
        Log.d(TAG, "kilometerCost " + kilometerCost);
        Log.d(TAG, "additionalTime " + additionalTime);

        float totalTime = dur_in_hr + additionalTime;
        float totalParamedicsCoast = ((dur_in_hr + additionalTime) * paramedicHourCost) * paramedicCount;
        float totatCarKilometersCost = distance_in_kilometeres * kilometerCost;

        float total = totalParamedicsCoast + totatCarKilometersCost;

        if(isReturn)
            total *= 2;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);


        return  df.format(total) + " zł";
    }


}
