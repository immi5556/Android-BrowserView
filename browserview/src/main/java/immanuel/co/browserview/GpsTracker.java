package immanuel.co.browserview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.fonts.FontVariationAxis;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Immanuel on 4/21/2018.
 */

public class GpsTracker implements LocationListener {
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    BrowserActivity mActivity;
    Context mContext;
    public static String locationCallback;
    public  static String  freqcallback;

    public GpsTracker(Context context, BrowserActivity activity) {
        this.mContext = context;
        this.mActivity = activity;
        getLocation();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public String getLocation() {
        String retMsg = "'{ stt: %1$s, prm: %2$s, loc: %3$s }'";
        String sttMsg = "{ gpsEnabled:\"%1$s\", networkEnabled :\"%2$s\" }";
        String prmMsg = "{ gps: \"%1$s\", network:\"%2$s\" }";
        String locMsg = "{ lat: \"%1$f\", lng:\"%2$f\" }";
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //sttMsg = "'{ gpsEnabled:" + isGPSEnabled + ", networkEnabled :" + isNetworkEnabled + " } '";
            sttMsg = String.format(sttMsg, isGPSEnabled, isNetworkEnabled);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                //Open setting to enable GPS
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (!checkLocationPermission()) {
                        prmMsg = String.format(prmMsg, false, false);

                    }
                     else {
                        prmMsg = String.format(prmMsg, false, true);
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        if (!checkLocationPermission()) {
                            prmMsg = String.format(prmMsg, false, false);
                        }
                        else {
                            prmMsg = String.format(prmMsg, true, false);
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        locMsg = String.format(locMsg, latitude, longitude);
        //return location;
        retMsg = String.format(retMsg, sttMsg, prmMsg, locMsg);
        //mActivity.UpdateLocation(retMsg);
        return  retMsg;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        String locMsg = "'{ lat: \"%1$f\", lng:\"%2$f\" }'";
        locMsg = String.format(locMsg, latitude, longitude);
        mActivity.UpdateLocation(locMsg, true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
