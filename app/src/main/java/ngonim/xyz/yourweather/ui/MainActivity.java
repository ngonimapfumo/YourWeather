package ngonim.xyz.yourweather.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ngonim.xyz.yourweather.R;
import ngonim.xyz.yourweather.models.Current;
import ngonim.xyz.yourweather.models.Forecast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String APIKEY = "0b7621a8ed42749a4d70136dab97e9f9";
    private Forecast mForecast;
    private TextView mTime;
    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mPrecipitation;
    private TextView mSummary;
    private ImageView mIconView;
    private TextView mLocationText;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LocationManager mLocationManager;

    private boolean doubleBackToExitPressedOnce = false;
    private ProgressBar mProgressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mTime = findViewById(R.id.timeLabel);
        mTemperature = findViewById(R.id.temperatureLabel);
        mHumidity = findViewById(R.id.humidityValue);
        mPrecipitation = findViewById(R.id.precipValue);
        mIconView = findViewById(R.id.iconImageView);
        mSummary = findViewById(R.id.summaryText);
        mLocationText = findViewById(R.id.locationText);
        mProgressBar = findViewById(R.id.progBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        getForecast();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast mForecast1 = new Forecast();
        mForecast1.setCurrent(getCurrentDetails(jsonData));
        return mForecast1;

    }

    private void showAlert(String title, String message, String positiveMessageButtonText, String negativeMessageButtonText) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveMessageButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(myIntent, 1);
                    }
                })
                .setNegativeButton(negativeMessageButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
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
                showAlert("Enable Location", "\"Your Locations Settings is set to 'Off'.\\nPlease Enable Location to \" +\n" +
                        "                        \"use this app\"", "Location Settings", "OK");
            }
        }
    }


    private void getForecast() {
        mProgressBar.setVisibility(View.VISIBLE);

        mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                //todo: edge case to be fixed
                if (location == null) {
                    return;
                }

                final String forecast = "https://api.darksky.net/forecast/" + APIKEY +
                        "/" + location.getLatitude() + "," + location.getLongitude();
                Log.d("Coordinates", location.getLatitude() + location.getLongitude() + "");
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
                            Log.d(TAG, e.getMessage());
                        }

                        @Override
                        public void onResponse
                                (Call call, Response response) {

                            try {
                                final String jsonData = response.body().string();
                                Log.v(TAG, jsonData);

                                if (response.isSuccessful()) {

                                    mForecast = parseForecastDetails(jsonData);
                                    runOnUiThread(new Runnable() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void run() {
                                            mProgressBar.setVisibility(View.GONE);
                                            updateDisplay();
                                        }
                                    });
                                } else {
                                    showAlert("ALERT", "oops, something went wrong",
                                            "OK", "");
                                }

                            } catch (IOException | JSONException e) {
                                Log.e(TAG, "Exception caught", e);
                            }
                        }
                    });

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                showAlert("Fail", "App failed to start, please reload",
                        "OKAY", "");
            }
        });


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
    private void updateDisplay() {
        Current current = mForecast.getCurrent();
        mTemperature.setText(current.getTemperatureInCelcius() + "");
        mPrecipitation.setText(current.getPrecipitation() + "%");
        mHumidity.setText(current.getHumidity() + "%");
        mSummary.setText(current.getSummary() + "");
        Drawable drawable = getResources().getDrawable(current.getIconId(), null);
        mIconView.setImageDrawable(drawable);
        mTime.setText(current.getFormattedTime());
        mLocationText.setText(current.getTimeZone().replace("/", ", "));
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

    private Context actContext() {
        return MainActivity.this;
    }

}

