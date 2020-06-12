package ngonim.xyz.yourweather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeneralFunctions extends AppCompatActivity {







    public static JSONObject getJsonObject(JSONArray arr, int position) {
        try {
            JSONObject obj_temp = arr.getJSONObject(position);

            return obj_temp;

        } catch (JSONException e) {
//            e.printStackTrace();

            return null;
        }

    }





}
