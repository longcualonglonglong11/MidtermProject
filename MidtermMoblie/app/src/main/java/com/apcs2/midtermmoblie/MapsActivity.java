package com.apcs2.midtermmoblie;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.text.Transliterator;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.libraries.places.api.Places;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    FusedLocationProviderClient mFusedLocationProviderClient;
    private RequestQueue mQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private GoogleMap mMap;
    private Marker mMarker;
    private TextToSpeech mTextToSpeech;
    private boolean misTextToSpeech = true;
    private int REQUEST_PERMISSION_ACCESS_FINE_LOCATION_AND_INTERNET_CODE = 1235;
    private String TAG_FAIL = "locationFail";
    private ArrayList<Landmark> _landmarks = new ArrayList<>();
    LinearLayout containerLayout;
    LinearLayout requestForm;
    LinearLayout deltailView;
    TextView detailTitle;
    TextView detailDescription;
    TextView detailPhone;
    TextView detailLocation;
    EditText eTitle;
    EditText eDescription;
    EditText eLocation;
    EditText ePhone;
    Spinner sEmergency;
    CheckBox cbCurrentLocation;
    TextView tEmergency;

    TextView detailEmergency;
    ArrayList<EditText> editFormText;
    DatabaseReference databaseReference;
    private ArrayList<Marker> mMarkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Places.initialize(getApplicationContext(), "AIzaSyCkDwHIftWmPCocgvKwXL6A9gMJObJHMog");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        initComponent();

    }

    // API require money so it not work
    private void createAutosuggestionSerachAdress() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363), new LatLng(-33.85754, 151.229596)));
        autocompleteFragment.setCountries("IN");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("sug", "Place: " + place.getName() + ", " + place.getId());
            }




            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i("cur", "An error occurred: " + status);
            }
        });
    }

    public void initComponent() {
        // API require money so it not work
        createAutosuggestionSerachAdress();

        //l

        mMarkers = new ArrayList<>();
        containerLayout = findViewById(R.id.container);
        requestForm = findViewById(R.id.request_from);
        deltailView = findViewById(R.id.detail_view);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailPhone = findViewById(R.id.detail_phone);
        detailLocation = findViewById(R.id.detail_location);
        eTitle = findViewById(R.id.eTitle);

        eDescription = findViewById(R.id.eDescription);
        eLocation = findViewById(R.id.eLocation);
        ePhone = findViewById(R.id.ePhone);
        sEmergency = findViewById(R.id.s_emergency);
        tEmergency = findViewById(R.id.t_emergency);
        cbCurrentLocation = findViewById(R.id.current_location);
        detailEmergency = findViewById(R.id.detail_emergency);
        ArrayAdapter<CharSequence> sEmergencyAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.emergency_level, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        sEmergencyAdapter.setDropDownViewResource(R.layout.emergency_level_spinner);
// Apply the adapter to the spinner
        sEmergency.setAdapter(sEmergencyAdapter);
        sEmergency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                tEmergency.setText("Emergency Level: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //l
        createEditFormTextList();


        databaseReference = FirebaseDatabase.getInstance().getReference("Location");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                misTextToSpeech = true;
            }
        });
    }

    private void createEditFormTextList() {
        editFormText = new ArrayList<>();
        editFormText.add(eTitle);
        editFormText.add(eDescription);
        editFormText.add(ePhone);
        editFormText.add(eLocation);

        createActionSendOnEditFormTexts();
    }

    private void createActionSendOnEditFormTexts() {
        for (int i = 0; i < editFormText.size(); i++) {
            final int index = i;
            editFormText.get(i).setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_NEXT) {
                        if (index == editFormText.size() - 1 || (index == editFormText.size() - 2 && cbCurrentLocation.isChecked())) {
//                            String title = String.valueOf(eTitle.getText());
//                            String description = String.valueOf(eDescription.getText());
//                            String location = String.valueOf(eLocation.getText());
//                            String phone = String.valueOf(ePhone.getText());
                            sEmergency.performClick();
//
                            // Check if no view has focus:
                            return hideKeyBoard();
                        }
                        editFormText.get(index + 1).requestFocus();
                        return true;
                    }
                    return false;
                }


            });
        }
    }

    private boolean hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        return true;

    }

    public String createDirectionUri(LatLng startPosition, LatLng desPositon) {
        String start = String.valueOf(startPosition.longitude) + ',' + String.valueOf(startPosition.latitude);
        String des = String.valueOf(desPositon.longitude) + ',' + String.valueOf(desPositon.latitude);
        return getString(R.string.MAPBOX_URL) + start + ';' + des + getString(R.string.ACCESS_TOKEN);
    }

    private void requestDirection(String url, final int postion) {
        mQueue = Volley.newRequestQueue(this);
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject route = response
                                    .getJSONArray(getString(R.string.route))
                                    .getJSONObject(0);
                            ArrayList<LatLng> listPointRoute = decodePoly(route
                                    .getString
                                            (getString
                                                    (R.string.geometry)));
                            drawPolyline(listPointRoute,
                                    getString(R.string.colorPolyLine),
                                    10, postion);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v(TAG_FAIL, "On respone");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG_FAIL, "err");
            }
        });
        checkInternetPermission();
    }

    public void addRequestToQueue() {
        mQueue.add(mJsonObjectRequest);
    }

    public void checkInternetPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            addRequestToQueue();
        } else {
            requestLocation(Manifest.permission.INTERNET, REQUEST_PERMISSION_ACCESS_FINE_LOCATION_AND_INTERNET_CODE);
            addRequestToQueue();
        }
    }

    public void loadData() {
        //l
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                //getDeviceLocation();
                LatLng currentPos = getDeviceLocation();
                moveCamera(currentPos);
            }
        });
        button.callOnClick();
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, new LatLngBounds(new LatLng(0,0),new LatLng(10,10)), null);
//        eLocation.setAdapter(mPlaceAutocompleteAdapter);
        //      getDeviceLocation();

//        Intent intent = getIntent();
//        mLandmark = new Landmark(intent.getStringExtra("name"),
//                intent.getStringExtra("des"),
//                intent.getIntExtra("logoID", 0),
//                intent.getDoubleExtra("lat", 0),
//                intent.getDoubleExtra("long", 0));
    }

    public ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void sendSMS(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the cameram

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                if (misTextToSpeech) {
//                    mTextToSpeech.speak(mLandmark.getDescription(),
//                            TextToSpeech.QUEUE_FLUSH, null);
//                    Toast.makeText(getApplicationContext(),
//                            mLandmark.getDescription(),
//                            Toast.LENGTH_SHORT
//                    ).show();
//                }
//                return false;
                containerLayout = findViewById(R.id.container);
                deltailView = findViewById(R.id.detail_view);

                deltailView.setVisibility(View.VISIBLE);
                containerLayout.setGravity(Gravity.BOTTOM);
                detailTitle.setText("Title: " + marker.getTitle());

                LatLng tmpLatLng = marker.getPosition();
                detailLocation.setText("Location: " + String.valueOf(tmpLatLng.latitude) + "N, " + String.valueOf(tmpLatLng.longitude) + "E");
                String spitSign = "~";
                String[] splitedStr = marker.getSnippet().split(spitSign);
                detailDescription.setText("Description: " + splitedStr[0]);
                detailPhone.setText("Phone: " + splitedStr[1]);
                detailEmergency.setText(splitedStr[2]);
                Log.d("DCCC", marker.getTitle());

                return true;
            }
        });
        loadData();
        // nay cua thay k xai
        //  displayMarkers();
    }

//    private void displayMarkers() {
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), mLandmark.getLogoID());
//        bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 4, bmp.getHeight() / 4, false);
//        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);
//        mMarker = mMap.addMarker(new MarkerOptions()
//                .position(mLandmark.getLatLng())
//                .icon(bitmapDescriptor)
//                .title(mLandmark.getName())
//                .snippet(mLandmark.getDescription()));
//        CameraPosition newCameraPosition = new CameraPosition.Builder()
//                .target(mLandmark.getLatLng()) // Sets the center of the map to Mountain View
//                .zoom(15)                      // Sets the zoom
//                .bearing(90)                   // Sets the orientation of the camera to east
//                .tilt(30)                      // Sets the tilt of the camera to 30 degrees
//                .build();
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
//    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            return true;
        } else {
            requestLocation(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION_AND_INTERNET_CODE);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1235:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    getDeviceLocation();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    LatLng getDeviceLocation() {

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return new LatLng(0,0);
        }
        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();

            // Getting longitude of the current location
            double longitude = location.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            LatLng myPosition = new LatLng(latitude, longitude);

            return latLng;
        }
        return new LatLng(0,0);
    }

    private void oldGetDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            Task<Location>[] locationResult = new Task[]{mFusedLocationProviderClient.getLastLocation()};
            locationResult[0].addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.setMyLocationEnabled(true);
                            LatLng curLocation = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());


                            curLat = curLocation.latitude;
                            curLong = curLocation.longitude;

                        } else {
                            Toast.makeText(getApplicationContext(), "PLEASE TURN ON YOUR LOCATION !!!", Toast.LENGTH_SHORT).show();
                            Log.v(TAG_FAIL, "Get LOCATION FAIL");

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "PLEASE TURN ON YOUR LOCATION !!!", Toast.LENGTH_SHORT).show();
                        Log.v(TAG_FAIL, "DEVICE NOT HAVE LOCATION");
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getDeviceLocationAgain() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task<Location>[] locationResult = new Task[]{mFusedLocationProviderClient.getLastLocation()};
            locationResult[0].addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            mMap.setMyLocationEnabled(true);
                            LatLng curLocation = new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());


                            curLat = curLocation.latitude;
                            curLong = curLocation.longitude;

                        } else {
                            Toast.makeText(getApplicationContext(), "PLEASE TURN ON YOUR LOCATION !!!", Toast.LENGTH_SHORT).show();
                            Log.v(TAG_FAIL, "Get LOCATION FAIL");

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "PLEASE TURN ON YOUR LOCATION !!!", Toast.LENGTH_SHORT).show();
                        Log.v(TAG_FAIL, "DEVICE NOT HAVE LOCATION");
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private double curLat;
    private double curLong;


    public void createForm(View view) {
        //     textView.setVisibility(View.VISIBLE);
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        containerLayout.setGravity(Gravity.CENTER);
        requestForm.setVisibility(View.VISIBLE);
        eTitle.setText("");
        //String title = String.valueOf(eTitle.getText());
        eDescription.setText("");
        String description = String.valueOf(eDescription.getText());
        eLocation.setText("");
        ePhone.setText("");
        //  String location = String.valueOf(eLocation.getText());
        // linearLayout.bringChildToFront(textView);

        // if true
//        if (checkPermission()) {
//            Button switchButton = findViewById(R.id.swithButton);
//            if (switchButton.getText().equals("Direct")) {
//                getDeviceLocation();
//                switchButton.setText("Remove path");
//            } else {
//                for (int i = 0; i < polylines.size(); i++)
//                    polylines.get(i).remove();
//                switchButton.setText("Direct");
//            }
//            return;
//        }
//        // if false
//        requestLocation(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_ACCESS_FINE_LOCATION_AND_INTERNET_CODE);
    }

    private void requestLocation(String accessFineLocation, int requestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{accessFineLocation},
                requestCode);
    }

    private ArrayList<Polyline> polylines = new ArrayList<>();

    public void drawPolyline(ArrayList<LatLng> listPointRoute, String color, int width, int position) {
        ArrayList<Polyline> tmpArr = new ArrayList<>();

        for (int i = 0; i < listPointRoute.size() - 1; i++) {
            LatLng src = listPointRoute.get(i),
                    des = listPointRoute.get(i + 1);
            Polyline singleLine = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(des.latitude, des.longitude)
                    )
                            .color(Color.parseColor(color))
                            .geodesic(true)
                            .width(width)
            );
            tmpArr.add(singleLine);
            //    Log.d("width", String.valueOf(singleLine.getWidth()));
        }
        _landmarks.get(position).setPolylines(tmpArr);

    }

    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Landmark drawMarker(String title, String description, String location, String phone) {


        //push vao trong array landmarks
        Landmark temp_lm = new Landmark(title, description, phone, new LatLng(0, 0), 1, new ArrayList<Polyline>());
        databaseReference.child("Location").push().setValue(temp_lm);
//        Toast.makeText(getApplicationContext(),
//                    title+description+phone,
//                    Toast.LENGTH_SHORT).show();


        Log.d("DCCCC", "No picture");
        BitmapDescriptor bitmapDescriptor = null;
        String level = (String) tEmergency.getText();
        if (level != "") {
            int color;
            if (level.contains("High")) {
                color = Color.RED;
                temp_lm.set_emergencyLevel(3);
            } else if (level.contains("Moderate")) {
                color = Color.YELLOW;
                temp_lm.set_emergencyLevel(2);
            } else {
                color = Color.BLUE;
                temp_lm.set_emergencyLevel(1);
            }
            //    Bitmap bmp = BitmapFactory.decodeResource(getResources(), warning);
            //    bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / 4, bmp.getHeight() / 4, false);
            bitmapDescriptor = vectorToBitmap(R.drawable.warning, color);
//            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bmp);

        }
        LatLng tmpLatLng = null;
        List<Address> addresses = null;
        if (cbCurrentLocation.isChecked() && checkPermission()) {
            tmpLatLng = getDeviceLocation();
        } else {
            Geocoder geocoder = new Geocoder(this);

            try {
                addresses = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {

                Log.d("DCCCC", "No location");
                e.printStackTrace();
            }
        }
        Address address = null;
        Log.e("sth", "iam here 1");

        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
            tmpLatLng = new LatLng(address.getLatitude(), address.getLongitude());
            Log.e("sth", "iam here 1");

//            Toast.makeText(getApplicationContext(),
//                    address.toString(),
//                    Toast.LENGTH_SHORT).show();

        }
//        Toast.makeText(getApplicationContext(),
//                    address.toString(),
//                   Toast.LENGTH_SHORT).show();
        temp_lm.setLatLng(tmpLatLng);
        if (tmpLatLng != null && !title.equals("") && !description.equals("") && !phone.equals("") && bitmapDescriptor != null) {
            String spitSign = "~";
            String containerStr = description + spitSign + phone + spitSign + level;
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(tmpLatLng)
                    .icon(bitmapDescriptor)
                    .title(title)
                    .snippet(containerStr));
            mMarkers.add(mMarker);
            _landmarks.add(temp_lm);
        }
        return temp_lm;
    }

    private void throwErrorWarning() {
        TextView error = findViewById(R.id.error_from);
        error.setVisibility(View.VISIBLE);
    }

    private void moveCamera(LatLng tmpLatLng) {
        CameraPosition newCameraPosition = new CameraPosition.Builder()
                .target(tmpLatLng) // Sets the center of the map to Mountain View
                .zoom(15)                      // Sets the zoom
                .bearing(90)                   // Sets the orientation of the camera to east
                .tilt(30)                      // Sets the tilt of the camera to 30 degrees
                .build();
        TextView error = findViewById(R.id.error_from);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        error.setVisibility(View.GONE);
        requestForm.setVisibility(View.GONE);
        containerLayout.setGravity(Gravity.BOTTOM);
    }

    public void saveMarker(View view) {

        //l
        String title = String.valueOf(eTitle.getText());
        String description = String.valueOf(eDescription.getText());
        String location = String.valueOf(eLocation.getText());
        String phone = String.valueOf(ePhone.getText());
        try {
            Landmark newLand = drawMarker(title, description, location, phone);
            moveCamera(newLand.getLatLng());
        } catch (Exception e) {
            e.printStackTrace();
            throwErrorWarning();
        }

        // sEmergency.setOnItemClickListener(this;
        //thiết lập sự kiện chọn phần tử cho Spinner


//        mMarker = mMap.addMarker(new MarkerOptions()
//                .position(tmpLatLng)
//                .title("bx q8")
//                .snippet("AAAAAAAAAAAAAAAAAAAAAAAAAAÂ"));
        //10.762913
        //106.6821717

//        _landmarks.add(temp_lm);
    }


    //l
    private void clearForm() {
        containerLayout.setGravity(Gravity.BOTTOM);
        requestForm.setVisibility(View.GONE);
        eTitle.setText("");
        eDescription.setText("");
        eLocation.setText("");
        ePhone.setText("");
    }

    public void close_detail_form(View view) {
        deltailView.setVisibility(View.GONE);
    }


    public void choose_current_location(View view) {
        if (cbCurrentLocation.isChecked()) {
            hideLocatonText(View.GONE);
        } else {
            hideLocatonText(View.VISIBLE);
        }
    }

    public void hideLocatonText(int state) {
        TextView tLocation = findViewById(R.id.t_location);
        eLocation.setVisibility(state);
        tLocation.setVisibility(state);
    }

    //l
    public void startDial(View view) {
        String phoneNumber = (String) detailPhone.getText().subSequence(7, detailPhone.getText().length());
        call(phoneNumber);
    }

    public void call(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + phoneNumber)));

    }

    public void startMessage(View view) {
        sendSMS((String) detailPhone.getText());
    }

    //l
    public String[] extractDetailForm() {
        String curLatLg = String.valueOf(detailLocation.getText());
        curLatLg = curLatLg.substring(10, curLatLg.length() - 1);
        String curTitle = (String) detailTitle.getText();
        curTitle = curTitle.substring(7, curTitle.length());
        return new String[]{curLatLg, curTitle};
    }

    //l
    public void completeRequest(View view) {
        String[] extractedStr = extractDetailForm();
        deleteMarker(extractedStr[0], extractedStr[1]);
        close_detail_form(new View(this));
    }

    //l
    public void deleteMarker(String location, String title) {

        int position = find(location, title);
        if (position != 1) {
            removeAllPolylineExceptAtPostion(-1);
            removeAMarker(position);
            //-1 = clear all
        }
    }

    //l
    private void removeAMarker(int position) {
        mMarkers.get(position).remove();
        mMarkers.remove(position);
        _landmarks.remove(position);
    }

    public void close_form(View view) {
        clearForm();
    }

    public int find(String location, String title) {
        for (int i = 0; i < _landmarks.size(); i++) {
            Landmark landmark = _landmarks.get(i);
            String tmpLatLg = String.valueOf(landmark.getLatLng().latitude) + "N, " + String.valueOf(landmark.getLatLng().longitude);
            if (landmark.getName().equals(title) && location.equals(tmpLatLg)) {
                return i;
            }

        }
        return -1;
    }

    public void directToCurrentPosition(View view) {
        if (checkPermission()) {
            LatLng curPosition = getDeviceLocation();
            removeAllPolylineExceptAtPostion(-1);

          //  LatLng tmpLng = new LatLng(curLat, curLong);
            String[] extractedStr = extractDetailForm();
            int position = find(extractedStr[0], extractedStr[1]);
            Landmark landmark = _landmarks.get(position);
            String url = createDirectionUri(curPosition, landmark.getLatLng());
            requestDirection(url, position);
            // check if get position success
            if (curPosition.latitude != 0 && curPosition.longitude != 0) {
                moveCamera(curPosition);
                close_detail_form(new View(this));
            }
        }
    }

    //l
    public void removeAllPolylineExceptAtPostion(int position) {
        for (int i = 0; i < _landmarks.size(); i++) {
            if (position != i) {
                ArrayList<Polyline> tmpPolylines = _landmarks.get(i).getPolylines();

                for (int j = 0; j < tmpPolylines.size(); j++) {
                    tmpPolylines.get(j).remove();
                }

            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("khoc","khoc");
    }
}