package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.findingboardinghouseapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class PickLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    FloatingActionButton fabMenu;

    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button selectLocationButton;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;

    public static final int RESULT_CODE_FROM_PICK_LOCATION = 30;
    public static final int REQUEST_CODE_FROM_PICK_LOCATION = 31;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_pick_location);

        // Initialize the mapboxMap view
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fabMenu = findViewById(R.id.fab_menu);
        fabMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), fabMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_map, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_streets:
                        PickLocationActivity.this.mapboxMap = mapboxMap;
                        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
//                enableLocationPlugin(style);

                            // Toast instructing user to tap on the mapboxMap
                            Toast.makeText(
                                    PickLocationActivity.this, "Di chuyển marker đến vị trí nhà trọ", Toast.LENGTH_SHORT).show();

                            // When user is still picking a location, we hover a marker above the mapboxMap in the center.
                            // This is done by using an image view with the default marker found in the SDK. You can
                            // swap out for your own marker image, just make sure it matches up with the dropped marker.
                            hoveringMarker = new ImageView(PickLocationActivity.this);
                            hoveringMarker.setImageResource(R.drawable.ic_red_marker);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                            hoveringMarker.setLayoutParams(params);
                            mapView.addView(hoveringMarker);

                            // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                            initDroppedMarker(style);

                            // Button for user to drop marker or to pick marker back up.
                            selectLocationButton = findViewById(R.id.select_location_button);
                            // Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                            selectLocationButton.setText("Chọn vị trí nhà trọ");

                            selectLocationButton.setOnClickListener(view -> {

                                if (hoveringMarker.getVisibility() == View.VISIBLE) {

                                    // Use the map target's coordinates to make a reverse geocoding search
                                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                                    // Hide the hovering red hovering ImageView marker
                                    hoveringMarker.setVisibility(View.INVISIBLE);

                                    // Transform the appearance of the button to become the cancel button
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                                    selectLocationButton.setText("Chọn lại");

                                    // Show the SymbolLayer icon to represent the selected map location
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {

                                        GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");

                                        if (source != null) {
                                            Log.i("Test", String.valueOf(mapTargetLatLng.getLatitude()) + " - " + mapTargetLatLng.getLongitude());
                                            source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                        }
                                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                        if (droppedMarkerLayer != null) {

                                            droppedMarkerLayer.setProperties(visibility(VISIBLE));
                                        }
                                    }

                                    // Use the map camera target's coordinates to make a reverse geocoding search
                                    reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                                } else {
                                    // Switch the button appearance back to select a location.
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                                    selectLocationButton.setText("Chọn vị trí nhà trọ");

                                    // Show the red hovering ImageView marker
                                    hoveringMarker.setVisibility(View.VISIBLE);

                                    // Hide the selected location SymbolLayer
                                    droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                    if (droppedMarkerLayer != null) {
                                        droppedMarkerLayer.setProperties(visibility(NONE));
                                    }
                                }
                            });
                        });
                        return true;
                    case R.id.menu_satellite_streets:
                        PickLocationActivity.this.mapboxMap = mapboxMap;
                        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
//                enableLocationPlugin(style);

                            // Toast instructing user to tap on the mapboxMap
                            Toast.makeText(
                                    PickLocationActivity.this, "Di chuyển marker đến vị trí nhà trọ", Toast.LENGTH_SHORT).show();

                            // When user is still picking a location, we hover a marker above the mapboxMap in the center.
                            // This is done by using an image view with the default marker found in the SDK. You can
                            // swap out for your own marker image, just make sure it matches up with the dropped marker.
                            hoveringMarker = new ImageView(PickLocationActivity.this);
                            hoveringMarker.setImageResource(R.drawable.ic_red_marker);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                            hoveringMarker.setLayoutParams(params);
                            mapView.addView(hoveringMarker);

                            // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
                            initDroppedMarker(style);

                            // Button for user to drop marker or to pick marker back up.
                            selectLocationButton = findViewById(R.id.select_location_button);
                            // Transform the appearance of the button to become the cancel button
                            selectLocationButton.setBackgroundColor(
                                    ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                            selectLocationButton.setText("Chọn vị trí nhà trọ");

                            selectLocationButton.setOnClickListener(view -> {

                                if (hoveringMarker.getVisibility() == View.VISIBLE) {

                                    // Use the map target's coordinates to make a reverse geocoding search
                                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                                    // Hide the hovering red hovering ImageView marker
                                    hoveringMarker.setVisibility(View.INVISIBLE);

                                    // Transform the appearance of the button to become the cancel button
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                                    selectLocationButton.setText("Chọn lại");

                                    // Show the SymbolLayer icon to represent the selected map location
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {

                                        GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");

                                        if (source != null) {
                                            Log.i("Test", String.valueOf(mapTargetLatLng.getLatitude()) + " - " + mapTargetLatLng.getLongitude());
                                            source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                        }
                                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                        if (droppedMarkerLayer != null) {

                                            droppedMarkerLayer.setProperties(visibility(VISIBLE));
                                        }
                                    }

                                    // Use the map camera target's coordinates to make a reverse geocoding search
                                    reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                                } else {
                                    // Switch the button appearance back to select a location.
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                                    selectLocationButton.setText("Chọn vị trí nhà trọ");

                                    // Show the red hovering ImageView marker
                                    hoveringMarker.setVisibility(View.VISIBLE);

                                    // Hide the selected location SymbolLayer
                                    droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                                    if (droppedMarkerLayer != null) {
                                        droppedMarkerLayer.setProperties(visibility(NONE));
                                    }
                                }
                            });
                        });
                        return true;
//                        mapboxMap.setStyle(new Style.Builder().fromUri(Style.MAPBOX_STREETS),
//                                style -> {
//                                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                                            .target(new LatLng(latitude, longitude)).zoom(14).build();
//                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//                                });

//                        mapboxMap.setStyle(new Style.Builder().fromUri(Style.SATELLITE_STREETS),
//                                style -> {
//                                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                                            .target(new LatLng(latitude, longitude)).zoom(14).build();
//
//                                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                                });
                }
                return false;
            });
            popupMenu.show();
        });

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        PickLocationActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
//                enableLocationPlugin(style);

            // Toast instructing user to tap on the mapboxMap
            Toast.makeText(
                    PickLocationActivity.this, "Di chuyển marker đến vị trí nhà trọ", Toast.LENGTH_SHORT).show();

            // When user is still picking a location, we hover a marker above the mapboxMap in the center.
            // This is done by using an image view with the default marker found in the SDK. You can
            // swap out for your own marker image, just make sure it matches up with the dropped marker.
            hoveringMarker = new ImageView(PickLocationActivity.this);
            hoveringMarker.setImageResource(R.drawable.ic_red_marker);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            hoveringMarker.setLayoutParams(params);
            mapView.addView(hoveringMarker);

            // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
            initDroppedMarker(style);

            // Button for user to drop marker or to pick marker back up.
            selectLocationButton = findViewById(R.id.select_location_button);
            // Transform the appearance of the button to become the cancel button
            selectLocationButton.setBackgroundColor(
                    ContextCompat.getColor(PickLocationActivity.this, R.color.green));
            selectLocationButton.setText("Chọn vị trí nhà trọ");

            selectLocationButton.setOnClickListener(view -> {

                if (hoveringMarker.getVisibility() == View.VISIBLE) {

                    // Use the map target's coordinates to make a reverse geocoding search
                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                    // Hide the hovering red hovering ImageView marker
                    hoveringMarker.setVisibility(View.INVISIBLE);

                    // Transform the appearance of the button to become the cancel button
                    selectLocationButton.setBackgroundColor(
                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                    selectLocationButton.setText("Chọn lại");

                    // Show the SymbolLayer icon to represent the selected map location
                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {

                        GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");

                        if (source != null) {
                            Log.i("Test", String.valueOf(mapTargetLatLng.getLatitude()) + " - " + mapTargetLatLng.getLongitude());
                            source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                        }
                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                        if (droppedMarkerLayer != null) {

                            droppedMarkerLayer.setProperties(visibility(VISIBLE));
                        }
                    }

                    // Use the map camera target's coordinates to make a reverse geocoding search
                    reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                } else {
                    // Switch the button appearance back to select a location.
                    selectLocationButton.setBackgroundColor(
                            ContextCompat.getColor(PickLocationActivity.this, R.color.green));
                    selectLocationButton.setText("Chọn vị trí nhà trọ");

                    // Show the red hovering ImageView marker
                    hoveringMarker.setVisibility(View.VISIBLE);

                    // Hide the selected location SymbolLayer
                    droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                    if (droppedMarkerLayer != null) {
                        droppedMarkerLayer.setProperties(visibility(NONE));
                    }
                }
            });
        });
    }

    private void initDroppedMarker(@NonNull Style loadedMapStyle) {
        // Add the marker image to map
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_blue_marker, null);
        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
        loadedMapStyle.addImage("dropped-icon-image", mBitmap);
        loadedMapStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-icon-image"),
                visibility(NONE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("latitude", latitude);
        resultIntent.putExtra("longitude", longitude);
        setResult(RESULT_CODE_FROM_PICK_LOCATION, resultIntent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    @Override
//    public void onExplanationNeeded(List<String> permissionsToExplain) {
//        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void onPermissionResult(boolean granted) {
//        if (granted && mapboxMap != null) {
//            Style style = mapboxMap.getStyle();
//            if (style != null) {
//                enableLocationPlugin(style);
//            }
//        } else {
//            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }

    /**
     * This method is used to reverse geocode where the user has dropped the marker.
     *
     * @param point The location to use for the search
     */

    @SuppressLint("TimberArgCount")
    private void reverseGeocode(final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    if (response.body() != null) {
                        latitude = point.latitude();
                        longitude = point.longitude();
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());
                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
    }

//    @SuppressWarnings({"MissingPermission"})
//    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
//        // Check if permissions are enabled and if not request
//        if (PermissionsManager.areLocationPermissionsGranted(this)) {
//
//            // Get an instance of the component. Adding in LocationComponentOptions is also an optional
//            // parameter
//            LocationComponent locationComponent = mapboxMap.getLocationComponent();
//            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(
//                    this, loadedMapStyle).build());
//            locationComponent.setLocationComponentEnabled(true);
//
//            // Set the component's camera mode
//            locationComponent.setCameraMode(CameraMode.TRACKING);
//            locationComponent.setRenderMode(RenderMode.NORMAL);
//
//        } else {
//            permissionsManager = new PermissionsManager(this);
//            permissionsManager.requestLocationPermissions(this);
//        }
//    }
}

