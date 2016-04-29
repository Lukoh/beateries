package com.goforer.beatery.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.goforer.beatery.R;
import com.goforer.beatery.utillity.ActivityCaller;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ViewEateryMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final float MAP_ZOOM = 18;

    private GoogleMap mMap;

    private double mLatitude;
    private double mLongitude;

    private String mEateryName;
    private String mEateryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_eatery_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLatitude = getIntent().getDoubleExtra(ActivityCaller.EXTRA_LATITUDE, -1);
        mLongitude = getIntent().getDoubleExtra(ActivityCaller.EXTRA_LONGITUDE, -1);
        mEateryName = getIntent().getStringExtra(ActivityCaller.EXTRA_EATERY_NAME);
        mEateryMenu = getIntent().getStringExtra(ActivityCaller.EXTRA_EATERY_MENU);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add a marker and move the camera
        LatLng eateryLocation = new LatLng(mLatitude, mLongitude);
        mMap.addMarker(new MarkerOptions().position(eateryLocation)
                .title(mEateryName)
                .snippet(getString(R.string.best_menu_phase) + mEateryMenu)
                .infoWindowAnchor(0.5f, 0.5f));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eateryLocation, MAP_ZOOM));
    }
}
