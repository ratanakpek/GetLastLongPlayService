package com.number.ratanakpek.googleplaystoredemo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private final String TAG = "PlayServiceDemo";
    //Reference to google play service
    private GoogleApiClient mGoogleApiClient;

    private TextView mLatVal;
    private TextView mLongVal;
    private TextView mAlt;
    private TextView mAccuracy;

    /**
     * Update the location field values in the Activity with the given
     * values in the supplied Location object
     */
    public void setLocationFields(Location loc) {
        Log.d(TAG, "Updating location fields");
        if (loc != null) {
            mLatVal.setText(String.format("%f", loc.getLatitude()));
            mLongVal.setText(String.format("%f", loc.getLongitude()));

            if (loc.hasAltitude()) {
                mAlt.setText(String.format("%f", loc.getAltitude()));
            }
            if (loc.hasAccuracy()) {
                mAccuracy.setText(String.format("%f", loc.getAccuracy()));
            }
        }
    }

    /**
     * Retrieves the last known location. Assumes that permissions are granted.
     */
    private Location getLocation() {
        // TODO: get and return the last known location
        try{
            Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return loc;
        }catch (SecurityException e){
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // get references to the user interface fields
        mLatVal = (TextView)findViewById(R.id.latValue);
        mLongVal = (TextView)findViewById(R.id.longValue);
        mAlt = (TextView)findViewById(R.id.altValue);
        mAccuracy = (TextView)findViewById(R.id.accValue);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed : "+ connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // If we're running on API 23 or above, we need to ask permission at runtime
        int permCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            Location locData = getLocation();
            setLocationFields(locData);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection was suspend : "+ i);

    }

    /**
     * Called when the user has been prompted at runtime to grant permissions
     */
    @Override
    public void onRequestPermissionsResult(int reqCode, String[] perms, int[] results){
        if (reqCode == 1) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                Location locData = getLocation();
                setLocationFields(locData);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
        int resultCode = gAPI.isGooglePlayServicesAvailable(this);
        if(resultCode!=ConnectionResult.SUCCESS){
            gAPI.getErrorDialog(this, resultCode, 1).show();
        }else{
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            Log.i(TAG, " onStp: Disconnect from Google play service");
            mGoogleApiClient.disconnect();
        }
    }
}
