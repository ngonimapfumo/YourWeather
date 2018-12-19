package ngonim.xyz.yourweather.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ngonim.xyz.yourweather.R;
import ngonim.xyz.yourweather.weathermodels.Current;
import ngonim.xyz.yourweather.weathermodels.Forecast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APIKEY = "0b7621a8ed42749a4d70136dab97e9f9";
    //hardcoded harare coordinates
    private double latitude = -17.824858;
    private double longitude = 31.053028;
    /*private String forecast =
            "https://api.darksky.net/forecast/0b7621a8ed42749a4d70136dab97e9f9/-17.824858,31.053028";*/
    private String forecast = "https://api.darksky.net/forecast/" + APIKEY +
            "/" + latitude + "," + longitude;
    private Forecast mForecast;
    private TextView mTime;
    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mPrecipitation;
    private TextView mSummary;
    private ImageView mIconView;
    private TextView mLocationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTime = (TextView)findViewById(R.id.timeLabel);
        mTemperature = (TextView) findViewById(R.id.temperatureLabel);
        mHumidity = (TextView) findViewById(R.id.humidityValue);
        mPrecipitation = (TextView) findViewById(R.id.precipValue);
        mIconView = (ImageView) findViewById(R.id.iconImageView);
        mSummary = (TextView) findViewById(R.id.summaryText);
        mLocationText = (TextView) findViewById(R.id.locationText);
        getForecast();
        if(!isGPSEnabled()){
            showAlert();
        }


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if (item.getItemId() == R.id.menu_refresh) {
                getForecast();
                Toast.makeText(this, R.string.WeatherUpdatedText,
                        Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);*/
       int position = item.getItemId();
       switch (position){
           case R.id.menu_refresh:
               getForecast();
               Toast.makeText(this, R.string.WeatherUpdatedText,
                       Toast.LENGTH_SHORT).show();
               break;


           case R.id.invite_menu:
               Intent n = new Intent(this, SettingsActivity.class);
               startActivity(n);
               break;
       }
       return super.onOptionsItemSelected(item);
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast mForecast = new Forecast();
        mForecast.setCurrent(getCurrentDetails(jsonData));
        return mForecast;

    }



    public boolean isGPSEnabled(){
        LocationManager locationManager =(LocationManager)
                this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void showAlert(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dialog.show();
    }

    private void getForecast() {

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecast)
                    .build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure
                        (Call call, IOException e) {
                    Log.d(TAG,e.getMessage());
                }

                @Override
                public void onResponse
                        (Call call, Response response)
                        throws IOException {

                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            errorAlert();
                        }

                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception caught", e);
                    }
                }
            });

        } else {
            Toast.makeText(this, "Network not available",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperature.setText(current.getTemperatureInCelcius() + "");
        mPrecipitation.setText(current.getPrecipitation() + "%");
        mHumidity.setText(current.getHumidity() + "");
        mSummary.setText(current.getSummary() + "");
        Drawable drawable = getResources().getDrawable(current.getIconId(), null);
        mIconView.setImageDrawable(drawable);
        mTime.setText(current.getFormattedTime());
        mLocationText.setText(current.getTimeZone());
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

    private void errorAlert() {
        AlertDialogFragment a = new AlertDialogFragment();
        a.show(getFragmentManager(), "error_dialog");
    }
}
//private locationListener

