package com.directions.sample;

import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.directions.sample.adapters.ResultsAdapter;
import com.directions.sample.utils.FareCalculation;
import com.directions.sample.utils.Util;
import com.directions.sample.volley.RouteResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.directions.sample.components.SlidingUpPanelLayout;
import com.directions.sample.components.SlidingUpPanelLayout.PanelSlideListener;
import com.directions.sample.components.SlidingUpPanelLayout.PanelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RoutingListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG ="MainActivity" ;
    protected GoogleMap map;
    protected LatLng start;
    protected LatLng end;
    @InjectView(R.id.start)
    AutoCompleteTextView starting;
    @InjectView(R.id.destination)
    AutoCompleteTextView destination;
    @InjectView(R.id.fab)
    ImageView send;
    private String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private ProgressDialog progressDialog;
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.primary_dark_material_light};


    private static final LatLngBounds BOUNDS_JAMAICA= new LatLngBounds(new LatLng(-57.965341647205726, 144.9987719580531),
            new LatLng(72.77492067739843, -9.998857788741589));

    private SlidingUpPanelLayout mLayout;
    private ImageView mGrabArrow;

    private String fromAddress = "";
    private String toAddress = "";

    private Button fareButton;



    private EditText mParamedicCount, mParamedicHourCost, mKilometerCost, mAdditionalTime;
    private Switch switchIsRetrun;
    private String distance = "";
    private String duration = "";
    private String distanceText = "";
    private String durationText = "";

    private ScrollView scrollViewContent;

    private List<RouteResult> routeResultsList;
    private RecyclerView mRecyclerView;
    private ResultsAdapter adapter;


    /**
     * This activity loads a map and then displays the route and pushpins on it.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ButterKnife.inject(this);
        mGrabArrow = (ImageView) findViewById(R.id.grab_arrow);
//        scrollViewContent = (ScrollView) findViewById(R.id.scrollViewContent);
        mParamedicCount = (EditText) findViewById(R.id.edit1);
        mParamedicHourCost = (EditText) findViewById(R.id.edit2);
        mKilometerCost = (EditText) findViewById(R.id.edit3);
        mAdditionalTime = (EditText) findViewById(R.id.edit4);
        switchIsRetrun= (Switch) findViewById(R.id.switchIsReturn);



        initDefaultValues();


        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                mGrabArrow.setBackground(getDrawable(R.drawable.ic_expand_more_white_48dp));
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                mGrabArrow.setBackground(getDrawable(R.drawable.ic_expand_less_white_48dp));
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });


        // Initialize recycler view
        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        map = mapFragment.getMap();

        mAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_JAMAICA, null);


        /*
        * Updates the bounds being used by the auto complete adapter based on the position of the
        * map.
        * */
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                mAdapter.setBounds(bounds);
            }
        });


        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(52.0829344, 18.9264378));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(6);

        map.moveCamera(center);
        map.animateCamera(zoom);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5000, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);

                        map.moveCamera(center);
                        map.animateCamera(zoom);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(8);

                        map.moveCamera(center);
                        map.animateCamera(zoom);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });



        /*
        * Adds auto complete adapter to both auto complete
        * text views.
        * */
        starting.setAdapter(mAdapter);
        destination.setAdapter(mAdapter);


        /*
        * Sets the start and destination points based on the values selected
        * from the autocomplete text views.
        * */

        starting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);
                fromAddress = item.description.toString();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        start=place.getLatLng();
                    }
                });

            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(LOG_TAG, "Autocomplete item selected: " + item.description);
                toAddress = item.description.toString();

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);

                        end=place.getLatLng();
                    }
                });

            }
        });

        /*
        These text watchers set the start and end points to null because once there's
        * a change after a value has been selected from the dropdown
        * then the value has to reselected from dropdown to get
        * the correct location.
        * */
        starting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                if (start != null) {
                    start = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if(end!=null)
                {
                    end=null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initDefaultValues(){

        mParamedicCount.setText("1");
        mParamedicHourCost.setText("20");
        mKilometerCost.setText("2.5");
        mAdditionalTime.setText("0");

        switchIsRetrun.setChecked(false);
        switchIsRetrun.setTextOff("Nie");
        switchIsRetrun.setTextOn("Tak");


    }

    private void setData(Route route) {

        // Calculating the total amount from the base price

        float dur_in_hr = FareCalculation.calculatreDurationInHourFromSeconds(route.getDurationValue());
        float dist_in_km = FareCalculation.calculatorDistanceInKilometers(route.getDistanceValue());

        int paramedicsCount = Integer.valueOf(mParamedicCount.getText().toString());
        float paramedicHourCost = Float.valueOf(mParamedicHourCost.getText().toString());
        float kilometerCost = Float.valueOf(mKilometerCost.getText().toString());
        int additionalTime = Integer.valueOf(mAdditionalTime.getText().toString());

        String estimatedFare = FareCalculation.estimatedFare(dur_in_hr, dist_in_km, paramedicsCount, paramedicHourCost, kilometerCost, additionalTime, switchIsRetrun.isChecked());

        RouteResult routeResult = new RouteResult();
        routeResult.setDistanceText(route.getDistanceText());
        routeResult.setDurationText(route.getDurationText());
        routeResult.setDurationValue(String.valueOf(route.getDurationValue()));
        routeResult.setDurationValue(String.valueOf(route.getDurationValue()));
        routeResult.setStatus("OK");

        routeResult.setFareCost(estimatedFare);


        routeResultsList.add(routeResult);

//        tvDistance.setVisibility(View.VISIBLE);
//        tvDistance.setText("Szacowany dystans: " + route.getDistanceText());
//
//        tvTime.setVisibility(View.VISIBLE);
//        tvTime.setText("Szacowany czas jazdy: " + route.getDurationText());

//        ShowFare(estimatedFare);
//        scrollViewContent.fullScroll(View.FOCUS_DOWN);

    }

    private void ShowFare(String fare) {

        // visible the amount's text view and set the estimate fare to the text view.
//        tvTitle.setVisibility(View.VISIBLE);
//        estimated_fare.setVisibility(View.VISIBLE);
//        estimated_fare.setText(fare);


    }


    @OnClick(R.id.fab)
    public void sendRequest()
    {

        mParamedicCount.setError(null);
        mParamedicHourCost.setError(null);
        mKilometerCost.setError(null);
        mAdditionalTime.setError(null);

        boolean error = false;

        if (mParamedicCount.getText().toString().length() == 0) {
            mParamedicCount.setError("Pole nie może być puste");
            error = true;
        }

        if (mParamedicHourCost.getText().toString().length() == 0) {
            mParamedicHourCost.setError("Pole nie może być puste");
            error = true;
        }

        if (mKilometerCost.getText().toString().length() == 0) {
            mKilometerCost.setError("Pole nie może być puste");
            error = true;
        }

        if (mAdditionalTime.getText().toString().length() == 0) {
            mAdditionalTime.setError("Pole nie może być puste");
            error = true;
        }


        if(Util.Operations.isOnline(this) && !error)
        {
            route();
        }
        else
        {
            Toast.makeText(this, "Brak połączenia z internetem", Toast.LENGTH_SHORT).show();

        }
    }



    public void route()
    {
        if(start==null || end==null)
        {
            if(start==null)
            {
                if(starting.getText().length()>0)
                {
                    starting.setError("Wybierz lokacje z listy.");
                }
                else
                {
                    Toast.makeText(this,"Proszę wybrać punkt początkowy.",Toast.LENGTH_SHORT).show();
                }
            }
            if(end==null)
            {
                if(destination.getText().length()>0)
                {
                    destination.setError("Wybierz lokację z listy.");
                }
                else
                {
                    Toast.makeText(this,"Proszę wybrać cel.",Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            progressDialog = ProgressDialog.show(this, "Proszę czekać.",
                    "Pobieranie informacji o trasie.", true);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }
    }


    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        progressDialog.dismiss();
        Toast.makeText(this,"Coś poszło nie tak, spróbuj ponownie", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

        map.moveCamera(center);
        map.moveCamera(zoom);


        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.

        routeResultsList = new ArrayList<>();
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            setData(route.get(i));


            Toast.makeText(getApplicationContext(),"Trasa "+ (i+1) +": dystans - "+ route.get(i).getDistanceText()+": czas - "+ route.get(i).getDurationText(),Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "list" + routeResultsList.get(0).getFareCost());
        adapter = new ResultsAdapter(MainActivity.this, routeResultsList);
        mRecyclerView.setAdapter(adapter);


        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(end);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);



    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Wyliczanie trasy zostało anulowane.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG,connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
