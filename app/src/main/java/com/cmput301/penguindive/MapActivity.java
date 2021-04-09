package com.cmput301.penguindive;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
/*
Title: Places SDK for Android: How to validate AutocompleteSupportFragment is not empty?
Author: Linray Wu
Date: 2021/03/30
Availability: https://stackoverflow.com/questions/59205440/places-sdk-for-android-how-to-validate-autocompletesupportfragment-is-not-empty
Title: Add the marker for autocomplete search
Author: Linray Wu
Date: 2021/03/31
Availability: https://stackoom.com/question/2Zl7c/%E5%9C%A8%E8%87%AA%E5%8A%A8%E5%AE%8C%E6%88%90%E6%90%9C%E7%B4%A2%E4%BD%8D%E7%BD%AE%E8%AE%BE%E7%BD%AE%E6%A0%87%E8%AE%B0
Title: How to change the position of My Location Button in Google Maps using android studio
Author: Linray Wu
Date: 2021/03/30
Availability: https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
Title: Adding multiple markers in Google Maps API v2 Android
Author: Linray Wu
Date: 2021/03/30
Availability:https://stackoverflow.com/questions/30569854/adding-multiple-markers-in-google-maps-api-v2-android
*/

/**
 * This class represents a map activity where the user can view
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference experimentCollectionReference = db.collection("Experiments");

    private GoogleMap mMap;
    private View mapView;
    private static final String TAG = MapActivity.class.getSimpleName();

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;

    // define the permission request
    static public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;


    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private LatLng locationLatLng;

    private String selectedLocation;
    private String expID;

    private Marker mLocationMarker;

    private MarkerOptions oLocation;

    AutocompleteSupportFragment autocompleteSelectLocation;

    Button mapConfirm;

    public ArrayList<MarkerOptions> options = new ArrayList<MarkerOptions>();
    MarkerOptions markerOptions;
    Marker marker;
    private LatLng latLng;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        autocompleteSelectLocation = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.select_location);
        mapConfirm = (Button) findViewById(R.id.map_confirm);

        Intent intent = getIntent();
        expID = intent.getStringExtra("EXPID");

        mapConfirm.setOnClickListener(view -> {
            double Lat = locationLatLng.latitude;
            double Lng = locationLatLng.longitude;
            GeoPoint geoPoint = new GeoPoint(Lat, Lng);
            experimentCollectionReference.document(expID).update("Locations", FieldValue.arrayUnion(geoPoint));
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finishAffinity();
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        // Construct a PlacesClient
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //active the autocomplete place selection for select location
        getAutocompleteSelect();

        loadMap();

    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    private void getAutocompleteSelect() {
        autocompleteSelectLocation.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSelectLocation.setHint("Location");
        autocompleteSelectLocation.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull final Place place) {
                if (place.getLatLng() != null) {
                    locationLatLng = place.getLatLng();
                    selectedLocation = place.getName();
                }
                oLocation = new MarkerOptions();
                oLocation.position(locationLatLng);
                oLocation.title(selectedLocation);
                oLocation.zIndex(1.0f);
                mMap.setOnMapLoadedCallback(() -> {
                    if (mLocationMarker != null) {
                        mLocationMarker.remove();
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 11));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15.0f));
                    mLocationMarker = mMap.addMarker(oLocation);
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i("PickUp", "An error occurred: " + status);

            }
        });
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
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

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // position on right bottom
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0,0,40,350);
        }

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(intent);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(intent);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void updateLocationUI() {
        // set myLocation and zoom
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                        assert mLastKnownLocation != null;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private String getAddress(double LAT, double LONG){
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try{
            //get address in list
            List<Address> addresses = geocoder.getFromLocation(LAT, LONG, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
            }
            else{
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void loadMap(){
        experimentCollectionReference.addSnapshotListener((value, error) -> {
            for(QueryDocumentSnapshot doc: Objects.requireNonNull(value)) {
                List<GeoPoint> locations = (List<GeoPoint>) doc.getData().get("Locations");
                Log.i("Locations", ""+locations);
                if (locations != null) {
                    for (int i = 0; i < locations.size(); i++) {
                        GeoPoint geoPoint = locations.get(i);
                        Log.i("geoPoints", "" + geoPoint);
                        double Lat = geoPoint.getLatitude();
                        double Lng = geoPoint.getLongitude();
                        latLng = new LatLng(Lat, Lng);
                        Log.i("LatLng", "" + latLng);
                        address = getAddress(Lat, Lng);
                        Log.i("address", address);
                        markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(address);
                        markerOptions.zIndex(1.0f);
                        Log.i("Marker options", "" + markerOptions);
                        options.add(markerOptions);
                    }
                    mMap.setOnMapLoadedCallback(() -> {
                        for (int i = 0; i<options.size(); i++) {
                            marker = mMap.addMarker(options.get(i));
                        }
                    });
                }
            }
        });
    }
}
