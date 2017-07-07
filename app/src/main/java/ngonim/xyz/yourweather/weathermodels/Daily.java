package ngonim.xyz.yourweather.weathermodels;

/**
 * Created by ngoni on 7/7/17.
 */

public class Daily extends Weather {

    public Daily() {
    }

    private double mMaxTemperature;
    private String mSummary;
    private long mTime;
    private String mIcon;
    private String mTimezone;

    public double getMaxTemperature() {
        return mMaxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    @Override
    public String getSummary() {
        return mSummary;
    }

    @Override
    public void setSummary(String summary) {
        mSummary = summary;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    @Override
    public void setTime(long time) {
        mTime = time;
    }

    @Override
    public String getIcon() {
        return mIcon;
    }

    @Override
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
