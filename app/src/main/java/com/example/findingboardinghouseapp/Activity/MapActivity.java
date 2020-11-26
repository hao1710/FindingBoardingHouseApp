package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.R;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.BubbleLayout;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap mapboxMap;

    private static final String ICON_PROPERTY = "ICON_PROPERTY";

    private static final String GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID";
    private static final String ICON_LAYER_ID = "ICON_LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String ICON_PIN_ID = "ICON_PIN_ID";

    private String ROUTE_LAYER_ID = "ROUTE_LAYER_ID";
    private static final String ROUTE_SOURCE_ID = "ROUTE_SOURCE_ID";

    private static final String ICON_SOURCE_ID = "icon-source-id";

    private GeoJsonSource source;
    private FeatureCollection featureCollection;
    public FeatureCollection featureCollection_One;

    private static MapboxDirections client;
    private DirectionsRoute currentRoute;
    private Point origin = Point.fromLngLat(105.60451156571315, 9.76126166509999);
    private Point destination;
    public String name;
    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_map);


        mapView = findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);
//        loadGeoJsonData();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri(Style.SATELLITE_STREETS),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude)).zoom(14).build();
                        new LoadGeoJsonDataTask(MapActivity.this).execute(String.valueOf(latitude), String.valueOf(longitude), name);


                        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mapboxMap.addOnMapClickListener(MapActivity.this);
                    }
                });


    }

    /**
     * AsyncTask to load data from the assets folder.
     */
    private static class LoadGeoJsonDataTask extends AsyncTask<String, Void, FeatureCollection> {

        private final WeakReference<MapActivity> activityRef;

        LoadGeoJsonDataTask(MapActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        protected FeatureCollection doInBackground(String... params) {
            MapActivity activity = activityRef.get();

            if (activity == null) {
                return null;
            }
            Log.i("Tell me why", "Params " + params[0]);

            Feature singleFeatureTwo = Feature.fromGeometry(
                    Point.fromLngLat(Double.valueOf(params[1]),
                            Double.valueOf(params[0])));
            singleFeatureTwo.addStringProperty("name", params[2]);
            singleFeatureTwo.addStringProperty(ICON_PROPERTY, ICON_PIN_ID);
            Feature singleFeatureThree = Feature.fromGeometry(
                    Point.fromLngLat(105.590584,
                            9.756021));

            singleFeatureThree.addStringProperty(ICON_PROPERTY, ICON_PIN_ID);
            singleFeatureThree.addStringProperty("name", "Nhà trọ Phương An");

            Feature singleFeatureFour = Feature.fromGeometry(
                    Point.fromLngLat(105.609115,
                            9.763684));

            singleFeatureFour.addStringProperty(ICON_PROPERTY, ICON_PIN_ID);
            singleFeatureFour.addStringProperty("name", "Nhà trọ Minh Đăng");

            Feature singleFeatureFive = Feature.fromGeometry(
                    Point.fromLngLat(105.590874,
                            9.744405));

            singleFeatureFive.addStringProperty(ICON_PROPERTY, ICON_PIN_ID);
            singleFeatureFive.addStringProperty("name", "Nhà trọ A");

            List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
            symbolLayerIconFeatureList.add(singleFeatureTwo);
//            symbolLayerIconFeatureList.add(singleFeatureThree);
//            symbolLayerIconFeatureList.add(singleFeatureFour);
//            symbolLayerIconFeatureList.add(singleFeatureFive);
            return FeatureCollection.fromFeatures(symbolLayerIconFeatureList);
        }

        @Override
        protected void onPostExecute(FeatureCollection featureCollection) {
            Log.i("Tell", "onpost");
            super.onPostExecute(featureCollection);
            MapActivity activity = activityRef.get();
            if (featureCollection == null || activity == null) {
                return;
            }

// This example runs on the premise that each GeoJSON Feature has a "selected" property,
// with a boolean value. If your data's Features don't have this boolean property,
// add it to the FeatureCollection 's features with the following code:


            for (Feature singleFeature : featureCollection.features()) {
                Log.i("Tell", "for");
                singleFeature.addBooleanProperty(PROPERTY_SELECTED, false);
            }

            activity.setUpData(featureCollection);
            new GenerateViewIconTask(activity).execute(featureCollection);
            Log.i("Tell", "afte new");
        }

    }


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    /**
     * Sets up all of the sources and layers needed for this example
     *
     * @param collection the FeatureCollection to set equal to the globally-declared FeatureCollection
     */
    public void setUpData(final FeatureCollection collection) {
        featureCollection = collection;
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                setupSource(style);
                setUpImage(style);
                initLayers(style);
                setUpMarkerLayer(style);
                setUpInfoWindowLayer(style);
            });
        }
    }

    /**
     * Adds the GeoJSON source to the map
     */
    private void setupSource(@NonNull Style loadedStyle) {
        source = new GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection);
        loadedStyle.addSource(source);
        loadedStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

    }

    /**
     * Adds the marker image to the map for use as a SymbolLayer icon
     */
    private void setUpImage(@NonNull Style loadedStyle) {
        loadedStyle.addImage(ICON_PIN_ID, BitmapFactory.decodeResource(
                this.getResources(), R.drawable.red_marker));
    }

    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource() {
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    /**
     * Setup a layer with maki icons, eg. west coast city.
     */
    private void setUpMarkerLayer(@NonNull Style loadedStyle) {
        loadedStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        iconImage(ICON_PIN_ID),
                        iconAllowOverlap(true),
                        iconOffset(new Float[]{0f, 0f}),
                        iconSize(0.3f)
                ));
    }

    /**
     * Add the route and icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#006eff"))
        );
        loadedMapStyle.addLayer(routeLayer);

//        loadedMapStyle.addImage(ICON_PIN_ID, BitmapFactory.decodeResource(
//                this.getResources(), R.drawable.house_icon));
//
////         Add the red marker icon SymbolLayer to the map
//        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_PIN_ID).withProperties(
//                iconImage(ICON_PIN_ID),
//                iconIgnorePlacement(true),
//                iconAllowOverlap(true),
//                iconOffset(new Float[]{0f, -9f})));
    }


    /**
     * Setup a layer with Android SDK call-outs
     * <p>
     * name of the feature is used as key for the iconImage
     * </p>
     */
    private void setUpInfoWindowLayer(@NonNull Style loadedStyle) {
        loadedStyle.addLayer(new SymbolLayer(CALLOUT_LAYER_ID, GEOJSON_SOURCE_ID)
                .withProperties(
                        /* show image with id title based on the value of the name feature property */
                        iconImage("{name}"),

                        /* set anchor of icon to bottom-left */
                        iconAnchor(ICON_ANCHOR_BOTTOM),

                        /* all info window and marker image to appear at the same time*/
                        iconAllowOverlap(true),

                        /* offset the info window to be above the marker */
                        iconOffset(new Float[]{-2f, -28f})
                )
                /* add a filter to show only when selected feature property is true */
                .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));
    }

    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, ICON_LAYER_ID);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty(PROPERTY_NAME);


            List<Feature> featureList = featureCollection.features();
            if (featureList != null) {
                for (int i = 0; i < featureList.size(); i++) {
                    System.out.println("feature get " + i);
                    if (featureList.get(i).getStringProperty(PROPERTY_NAME).equals(name)) {
                        System.out.println("equals " + name);
                        Point destination = (Point) featureList.get(i).geometry();

                        if (featureSelectStatus(i)) {

                            setFeatureSelectState(featureList.get(i), false);


                        } else {
                            setSelected(i);

                        }
                        System.out.println(origin);
                        System.out.println(destination);
                        getRoute(mapboxMap, origin, destination);

                        System.out.println(screenPoint.x + " aaaa");
                        System.out.println("get source " + source);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private void setSelected(int index) {
        if (featureCollection.features() != null) {
            Feature feature = featureCollection.features().get(index);
            setFeatureSelectState(feature, true);
            refreshSource();
        }
    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void setFeatureSelectState(Feature feature, boolean selectedState) {
        if (feature.properties() != null) {
            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
            refreshSource();
        }
    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private boolean featureSelectStatus(int index) {
        if (featureCollection == null) {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, Bitmap> imageMap) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);

            });
        }
    }


    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * </p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     * </p>
     */
    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>> {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<MapActivity> activityRef;
        private final boolean refreshSource;

        GenerateViewIconTask(MapActivity activity, boolean refreshSource) {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
        }

        GenerateViewIconTask(MapActivity activity) {
            this(activity, false);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params) {
            MapActivity activity = activityRef.get();
            System.out.println("1 + 2");
            if (activity != null) {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity);

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features()) {

                    BubbleLayout bubbleLayout = (BubbleLayout)
                            inflater.inflate(R.layout.symbol_layer_info_window_layout_callout, null);

                    String name = feature.getStringProperty(PROPERTY_NAME);
                    TextView titleTextView = bubbleLayout.findViewById(R.id.info_window_title);
                    titleTextView.setText(name);


//                    String style = feature.getStringProperty(PROPERTY_CAPITAL);
//                    TextView descriptionTextView = bubbleLayout.findViewById(R.id.info_window_description);
//                    descriptionTextView.setText(
//                            String.format(activity.getString(R.string.capital), style));

                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    bubbleLayout.measure(measureSpec, measureSpec);

                    float measuredWidth = bubbleLayout.getMeasuredWidth();

                    bubbleLayout.setArrowPosition(measuredWidth / 2 - 5);

                    Bitmap bitmap = SymbolGenerator.generate(bubbleLayout);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, bubbleLayout);

                }

                return imagesMap;
            } else {

                return null;

            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap) {
            super.onPostExecute(bitmapHashMap);
            MapActivity activity = activityRef.get();
            if (activity != null && bitmapHashMap != null) {
                activity.setImageGenResults(bitmapHashMap);
                System.out.println("zzzzzz");
                if (refreshSource) {
                    activity.refreshSource();
                }
            }
            Toast.makeText(activity, R.string.tap_on_marker_instruction, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private static class SymbolGenerator {

        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        static Bitmap generate(@NonNull View view) {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     *
     * @param mapboxMap   the Mapbox map object that the route will be drawn on
     * @param origin      the starting point of the route
     * @param destination the desired finish point of the route
     */
    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);

                // Make a toast which displays the route's distance
                Toast.makeText(MapActivity.this, String.format(
                        getString(R.string.directions_activity_toast_message),
                        currentRoute.distance()), Toast.LENGTH_SHORT).show();
                System.out.println(currentRoute.distance() + " is m.");
                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            //style.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

                            Feature f1 = Feature.fromGeometry(origin);
                            Feature f2 = Feature.fromGeometry(destination);
                            List<Feature> f = new ArrayList<>();
                            f.add(f1);
                            f.add(f2);
                            FeatureCollection featureCollection = FeatureCollection.fromFeatures(f);
                            GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, featureCollection);
                            //style.addSource(iconGeoJsonSource);
                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);
                            System.out.println(source + " aaaaaa");
                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));

                            }

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(MapActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
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
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}