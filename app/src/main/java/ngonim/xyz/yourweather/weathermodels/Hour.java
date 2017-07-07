package ngonim.xyz.yourweather.weathermodels;

/**
 * Created by ngoni on 7/7/17.
 */

public class Hour {

    private double mTemperature;
    private String mSummary;
    private long mTime;
    private String mIcon;
    private String mTimezone;

    public Hour(){}

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String timezone) {
        mTimezone = timezone;
    }
}
