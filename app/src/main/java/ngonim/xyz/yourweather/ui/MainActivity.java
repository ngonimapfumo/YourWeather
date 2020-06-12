package ngonim.xyz.yourweather.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import ngonim.xyz.yourweather.BuildConfig;
import ngonim.xyz.yourweather.R;
import ngonim.xyz.yourweather.models.Current;
import ngonim.xyz.yourweather.models.Forecast;
import ngonim.xyz.yourweather.utils.GeneralFunctions;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static ngonim.xyz.yourweather.utils.GeneralFunctions.getJsonObject;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Forecast mForecast;
    private TextView mTime;
    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mPrecipitation;
    private TextView mSummary;
    private ImageView mIconView;
    private TextView mLocationText;
    private Location mLocation;
    private LocationManager mLocationManager;
    private FirebaseRemoteConfig mFirebaseConfig;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressBar mProgressBar;
    private GeneralFunctions mGeneralFunctions;
    private double lat;
    private double lon;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseConfig = FirebaseRemoteConfig.getInstance();
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mTime = findViewById(R.id.timeLabel);
        mTemperature = findViewById(R.id.temperatureLabel);
        mHumidity = findViewById(R.id.humidityValue);
        mPrecipitation = findViewById(R.id.precipValue);
        mIconView = findViewById(R.id.iconImageView);
        mSummary = findViewById(R.id.summaryText);
        mLocationText = findViewById(R.id.locationText);
        mProgressBar = findViewById(R.id.progBar);
        mGeneralFunctions = new GeneralFunctions();

        checkPermission();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        getForecast();
        checkGPS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void checkGPS() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            GPSAlert("Enable Location",
                    "Your Locations Settings is set to off \n Please Enable Location to use this app",
                    "Go to settings");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_refresh) {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, getString(R.string.NO_DATA_CONN), Toast.LENGTH_SHORT).show();
            } else {
                getForecast();
            }
        } else if (itemId == R.id.add_city) {
            //todo: add activity for city picker
            showAlert("ALERT", "Currently under development", "OK", "");
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAlert(String alert, String currently_under_development, String ok, String s) {
        //todo: do things
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast mForecast1 = new Forecast();
        mForecast1.setCurrent(getCurrentDetails(jsonData));
        return mForecast1;

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isAvailabe = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailabe = true;
        }
        return isAvailabe;
    }

    private void GPSAlert(String title, String message, String positiveMessageButtonText) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton(positiveMessageButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, 1);
                    }
                });
        dialog.show();
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getForecast();
                checkGPS();

            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: " + lat + lon);
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    private void getForecast() {

        mProgressBar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?";
        String url = BASE_URL + "lat=" + lat + "&lon=" + lon
                + "&appid=" + BuildConfig.API_KEY + "&units=metric";

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure
                        (Call call, IOException e) {
                    Log.d(TAG, e.getMessage());
                }

                @Override
                public void onResponse
                        (Call call, final Response response) {

                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {
                            final JSONObject jsonObject = new JSONObject(jsonData);
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                @Override
                                public void run() {
                                    mProgressBar.setVisibility(View.GONE);
                                    try {
                                        updateDisplay(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else {
                            showAlert("ALERT", "oops, something went wrong",
                                    "OK", "");
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught", e);
                    } catch (JSONException e) {

                    }
                }
            });


        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.press_back_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateDisplay(JSONObject jsonObject) throws JSONException {
        DecimalFormat decimalFormat = new DecimalFormat("0");
        JSONArray weatherObj = jsonObject.getJSONArray("weather");
        JSONObject main = jsonObject.getJSONObject("main");
        for (int i = 0; i < weatherObj.length(); i++) {
            JSONObject obj_temp = getJsonObject(weatherObj, i);
            mSummary.setText("" + obj_temp.get("description"));
        }
        mTemperature.setText(decimalFormat.format(main.get("temp")));
        mLocationText.setText("" + jsonObject.get("name"));


    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject currently = forecast.getJSONObject("currently");
        Current current = new Current();
        current.setHumidity(currently.getDouble("humidity"));
        current.setTime(currently.getLong("time"));
        current.setIcon(currently.getString("icon"));
        current.setPrecipitation(currently.getDouble("precipProbability"));
        current.setSummary(currently.getString("summary"));
        current.setTemperature(currently.getDouble("temperature"));
        current.setTimeZone(timezone);
        return current;
    }


    private Context getActivity() {
        return MainActivity.this;
    }

}

