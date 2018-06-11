package kamil.packahe;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import kamil.packahe.User.ConstantsUserAPI;
import kamil.packahe.User.RequestHandler;
import kamil.packahe.User.SharedPrefManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class MapsActivity extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener, PlaceSelectionListener {

    public static final int REQUEST_LOCATION_CODE = 99;
    static final LatLng PB_INFA = new LatLng(53.118311, 23.149712);
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private static final String TAG = "PlaceSelectionListener";
    private static View view;
    private final LatLng[] currentPlace = new LatLng[1];
    private com.arlib.floatingsearchview.FloatingSearchView floatingSearchView;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    private LatLng lastClickedPos;
    private MarkerOptions greenMarkerOptions;
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList markerPoints = new ArrayList();
    private int PROXIMITY_RADIUS = 2500;
    private Button szkol, restaa, szpital;
    private ImageButton flagMarkts;
    private ImageButton historyListIB;
    private ListView listView;
    private LatLng mClickPos;
    private ProgressDialog progressDialog;
    private Sensor mySensor;
    private SensorManager sensorManager;
    private Marker currentLocationMarker;
    private String ifDestZero = "1 km";
    private Marker FirstRestaurant;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private boolean isMapMarked = false;
    private Map<String, Marker> markersMap = new HashMap<>();
    private Map<String, Marker> markersMapHistory = new LinkedHashMap<>();
    private List<Polyline> markersMapHistoryPolylines = new LinkedList<>();
    private List<HistoryPlace> historyList = new ArrayList<>();
    private ArrayAdapter<HistoryPlace> adapter;
    private Timer myTimer;
    private boolean isHistory = false;
    private Runnable Timer_Tick = () -> getHistory();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.activity_maps, container, false);
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            restaa = view.findViewById(R.id.restauracje);
            szkol = view.findViewById(R.id.szkoly);
            szpital = view.findViewById(R.id.szpitale);
            flagMarkts = view.findViewById(R.id.marketIB);
            historyListIB = view.findViewById(R.id.listIB);

            historyListIB.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                LayoutInflater inflater2 = getLayoutInflater();
                View convertView = inflater2.inflate(R.layout.dialog_layout, null);
                alertDialog.setView(convertView);
                alertDialog.setTitle("Historia");
                final AlertDialog dialog = alertDialog.create();
                listView = convertView.findViewById(R.id.List);
                historyList.clear();
                String pesel = SharedPrefManager.getInstance(getContext()).getUsername();
                String url = ConstantsUserAPI.URL_GET_HISTORY + "?pesel=" + pesel;
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");
                progressDialog.show();

                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                    try {
                        int counter = 0;
                        int currentValue = counter;
                        String fromLat, fromLang, toLat, toLang, fromPlace, toPlace, totalKm;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject post = response.getJSONObject(i);
                            fromLat = post.getString("zLat");
                            fromLang = post.getString("zLang");
                            toLat = post.getString("doLat");
                            toLang = post.getString("doLang");
                            fromPlace = post.getString("zMiejsca");
                            toPlace = post.getString("doMiejsca");
                            totalKm = post.getString("odleglosc");
                            HistoryPlace historyPlace = new HistoryPlace(fromLat, toLat, fromLang, toLang, fromPlace, toPlace, totalKm);
                            historyList.add(historyPlace);
                        }
                        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, historyList);
                        listView.setAdapter(adapter);
                        progressDialog.dismiss();
                    } catch (JSONException je) {
                        progressDialog.dismiss();
                        je.printStackTrace();
                    }
                }, error -> {
                    progressDialog.dismiss();
                    Log.e("ERROR", " " + error.toString());

                });
                progressDialog.dismiss();
                RequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                    Object item = listView.getItemAtPosition(i);
                    if (item != null) {
                        makeWayFromHistory((HistoryPlace) item);
                        dialog.dismiss();
                    }
                });
                dialog.show();

            });
/*
            historyListIB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view,
                                           int position, long id) {
                    Object item = adapterView.getItemAtPosition(position);
                    if (item != null) {
                        makeWayFromHistory((HistoryPlace) item);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
*/
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TimerMethod();
                }

            }, 0, 10000);

            flagMarkts.setOnClickListener(e -> getData());
            szpital.setOnClickListener(e -> szpitale());
            szkol.setOnClickListener(e -> szkoly());
            restaa.setOnClickListener(e -> restauracje());

            try {
                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager().findFragmentById(R.id.place_fragment);
                autocompleteFragment.setOnPlaceSelectedListener(this);
                autocompleteFragment.setHint("Wyszukaj miejsce");
                autocompleteFragment.setBoundsBias(BOUNDS_MOUNTAIN_VIEW);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mySensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
            setHasOptionsMenu(true);
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } catch (InflateException e) {
            e.printStackTrace();
        }
        return view;
    }

    private void TimerMethod() {
        getActivity().runOnUiThread(Timer_Tick);
    }

    @Override
    public void onPause() {
        myTimer.cancel();
        super.onPause();
    }

    public void saveHistory(String fromLat, String fromLang, String toLat, String toLang, String fromPlace, String toPlace, String totalKm) {
        Date datee = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(datee);
        Log.d("Data: ", date + " ");
        String pesel = SharedPrefManager.getInstance(getContext()).getUsername();
        String url = ConstantsUserAPI.URL_POST_HISTORY + "?pesel=" + pesel + "&zMiejsca=" + fromPlace + "&doMiejsca=" + toPlace + "&zLat=" + fromLat + "&zLang=" + fromLang +
                "&odleglosc=" + totalKm + "&doLat=" + toLat + "&doLang=" + toLang + "&data=" + date;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");
        progressDialog.show();
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            getHistory();
            progressDialog.dismiss();
        }, error -> {
            progressDialog.dismiss();
            Log.e("ERROR", " " + error.toString());

        });
        progressDialog.dismiss();
        RequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void getHistory() {
        historyList.clear();
        String pesel = SharedPrefManager.getInstance(getContext()).getUsername();
        String url = ConstantsUserAPI.URL_GET_HISTORY + "?pesel=" + pesel;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");
        progressDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                int counter = 0;
                int currentValue = counter;
                String fromLat, fromLang, toLat, toLang, fromPlace, toPlace, totalKm;
                for (int i = 0; i < response.length(); i++) {
                    JSONObject post = response.getJSONObject(i);
                    fromLat = post.getString("zLat");
                    fromLang = post.getString("zLang");
                    toLat = post.getString("doLat");
                    toLang = post.getString("doLang");
                    fromPlace = post.getString("zMiejsca");
                    toPlace = post.getString("doMiejsca");
                    totalKm = post.getString("odleglosc");
                    HistoryPlace historyPlace = new HistoryPlace(fromLat, toLat, fromLang, toLang, fromPlace, toPlace, totalKm);
                    historyList.add(historyPlace);
                }
                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, historyList);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            } catch (JSONException je) {
                progressDialog.dismiss();
                je.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.e("ERROR", " " + error.toString());

        });
        progressDialog.dismiss();
        RequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.MENU_Normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (itemId == R.id.MENU_Satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (itemId == R.id.MENU_Terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPlaceSelected(Place place) {
        isHistory = true;
        LatLng toPlace = place.getLatLng();
        Log.e(TAG, "HistoryPlace Selected: " + place.getName() + " - " + place.getLatLng());
        markerPoints.clear();
        mMap.clear();

        markerPoints.add(currentPlace[0]);
        MarkerOptions options = new MarkerOptions();
        options.position(currentPlace[0]);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markersMapHistory.put("0", mMap.addMarker(options));


        options = new MarkerOptions();
        options.position(toPlace);
        markerPoints.add(toPlace);

        LatLng origin = (LatLng) markerPoints.get(0);
        LatLng dest = (LatLng) markerPoints.get(1);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        String distance = getDistance(origin.latitude, origin.longitude, dest.latitude, dest.longitude);
        if (distance == null) {
            distance = ifDestZero;
        }
        String fromPlace = getCompleteAddressString(origin.latitude, origin.longitude);
        String toPlacee = getCompleteAddressString(dest.latitude, dest.longitude);
        options.title(distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markersMapHistory.put("1", mMap.addMarker(options));
        //    markersMapHistory.get("1").showInfoWindow();

        saveHistory(String.valueOf(origin.latitude), String.valueOf(origin.longitude), String.valueOf(dest.latitude), String.valueOf(dest.longitude), fromPlace, toPlacee, distance);
        markerPoints.clear();
        isHistory = false;
    }

    public void makeWayFromHistory(HistoryPlace historyPlace) {
        isHistory = true;
        LatLng fromPlace = new LatLng(Double.valueOf(historyPlace.getFromLat()), Double.valueOf(historyPlace.getFromLang()));
        LatLng toPlace = new LatLng(Double.valueOf(historyPlace.getToLat()), Double.valueOf(historyPlace.getToLang()));
        markerPoints.clear();
        mMap.clear();

        markerPoints.add(fromPlace);
        MarkerOptions options = new MarkerOptions();
        options.position(fromPlace);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markersMapHistory.put("0", mMap.addMarker(options));

        options = new MarkerOptions();
        options.position(toPlace);
        markerPoints.add(toPlace);

        LatLng origin = (LatLng) markerPoints.get(0);
        LatLng dest = (LatLng) markerPoints.get(1);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        String distance = getDistance(origin.latitude, origin.longitude, dest.latitude, dest.longitude);
        if (distance == null) {
            distance =ifDestZero;
        }
        options.title(distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markersMapHistory.put("1", mMap.addMarker(options));
        markersMapHistory.get("1").showInfoWindow();
        markerPoints.clear();
        isHistory = false;
    }

    @Override
    public void onError(Status status) {
        Log.e(TAG, "onError: Status = " + status.toString());
        Toast.makeText(getContext(), "HistoryPlace selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Log.i("qwe", "HistoryPlace Selected: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Log.e("qwe", "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLongClickListener(onNewLongClick());
        mGeoDataClient = Places.getGeoDataClient(getContext(), null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext(), null);


        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(task -> {
            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
            currentPlace[0] = placeResult.getResult().get(0).getPlace().getLatLng();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPlace[0]));
            likelyPlaces.release();
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.addMarker(new MarkerOptions().position(currentPlace[0]).title("Aktualna lokalizacja"));
        });
        mMap.setOnMapClickListener(latLng -> mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));


        mMap.setOnMapClickListener((LatLng latLng) -> {
            int size = markersMapHistory.size();
            for (int i = 0; i < size; i++) {
                String value = String.valueOf(i);
                markersMapHistory.remove(value).remove();
            }
            int sizePoly = markersMapHistoryPolylines.size();
            Log.d("Size: ", sizePoly + " ");
            for (int i = 0; i < sizePoly; i++) {
                Log.d("Removing: ", i + " ");
                markersMapHistoryPolylines.get(i).remove();
            }
            if (markerPoints.size() > 1) {
                markerPoints.clear();
                mMap.clear();
            }

            markerPoints.add(latLng);
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options);
                lastClickedPos = (LatLng) markerPoints.get(0);
            }

            if (markerPoints.size() >= 2) {
                LatLng origin = (LatLng) markerPoints.get(0);
                LatLng dest = (LatLng) markerPoints.get(1);
                String url = getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                String distance = getDistance(origin.latitude, origin.longitude, dest.latitude, dest.longitude);
                if (distance == null) {
                    distance = ifDestZero;
                }
                String fromPlace = getCompleteAddressString(origin.latitude, origin.longitude);
                String toPlace = getCompleteAddressString(dest.latitude, dest.longitude);
                mMap.addMarker(options.title(distance).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))).showInfoWindow();
                saveHistory(String.valueOf(origin.latitude), String.valueOf(origin.longitude), String.valueOf(dest.latitude), String.valueOf(dest.longitude), fromPlace, toPlace, distance);
            }

        });
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                strAdd = addresses.get(0).getAddressLine(0);
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private GoogleMap.OnMapLongClickListener onNewLongClick() {
        return latLng -> {
            mClickPos = latLng;
            final EditText edittext = new EditText(getContext());
            new AlertDialog.Builder(getContext())
                    .setTitle("Czy chcesz dodać widok lub niebezpieczenstwo w danym miejscu")
                    .setPositiveButton("Tak", (dialog, whichButton) -> {
                        Intent intent = new Intent(new Intent(getContext(), CreateWidok.class));
                        intent.putExtra("LAT", String.valueOf(mClickPos.latitude));
                        intent.putExtra("LNG", String.valueOf(mClickPos.longitude));
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("Nie", null)
                    .show();
            Log.d("dluzsze klikniecie", "qwe");
        };
    }

    public void getData() {
        isMapMarked = !isMapMarked;
        if (isMapMarked) {
            markersMap = new HashMap<>();
            final String url = ConstantsUserAPI.URL_GET_DATA;
            Log.d("URL: ", url + "");
            List<Widok> list = new ArrayList<>();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Prosze poczekaj, trwa przetwarzanie danych");

            progressDialog.show();
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                try {
                    int counter = 0;
                    int currentValue = counter;
                    String lat = "5", lng = "5", komentarz, typ;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject post = response.getJSONObject(i);
                        typ = post.getString("typ");
                        komentarz = post.getString("komentarz");
                        lat = post.getString("lat");
                        lng = post.getString("lng");
                        Widok f = new Widok(typ, komentarz, lat, lng);
                        list.add(f);
                    }
                    for (Widok w : list) {
                        LatLng latLng = new LatLng(Double.valueOf(w.getLon()), Double.valueOf(w.getLat()));
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(latLng);
                        markerOption.title(w.getTyp() + "\n" + w.getKomenatrz());
                        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

                        if (w.getKomenatrz().equals("Niebezpieczenstwo")) {
                            markersMap.put(String.valueOf(currentValue), mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(w.getKomenatrz())
                                    .snippet(w.getTyp()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))));
                        } else {
                            markersMap.put(String.valueOf(currentValue), mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(w.getKomenatrz())
                                    .snippet(w.getTyp()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))));
                        }
                        currentValue = ++counter;

                    }
                    progressDialog.dismiss();
                } catch (JSONException je) {
                    progressDialog.dismiss();
                    je.printStackTrace();
                }
            }, error -> {
                progressDialog.dismiss();
                Log.e("ERROR", " " + error.toString());

            });
            progressDialog.dismiss();
            RequestHandler.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
        } else {
            int size = markersMap.size();
            for (int i = 0; i < size; i++) {
                String value = String.valueOf(i);
                markersMap.remove(value).remove();
            }
        }
    }

    public String getDistance(final double lat1, final double lon1, final double lat2, final double lon2) {
        final String[] parsedDistance = new String[1];
        final String[] response = new String[1];
        Thread thread = new Thread(() -> {
            try {

                URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=metric&mode=driving");
                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response[0] = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                JSONObject jsonObject = new JSONObject(response[0]);
                JSONArray array = jsonObject.getJSONArray("routes");
                JSONObject routes = array.getJSONObject(0);
                JSONArray legs = routes.getJSONArray("legs");
                JSONObject steps = legs.getJSONObject(0);
                JSONObject distance = steps.getJSONObject("distance");
                parsedDistance[0] = distance.getString("text");

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance[0];
    }


    protected synchronized void buildGoogleApiClient() {

        client = new GoogleApiClient.Builder(getContext()).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }


    @Override
    public void onLocationChanged(Location location) {

        lastLocation = location;

        if (currentLocationMarker != null) {

            currentLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getContext(), "Permision !", Toast.LENGTH_LONG).show();

        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("Current location");
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        currentLocationMarker = mMap.addMarker(markerOption);

        location.getLatitude();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {

            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    public void szkoly() {
        String Restaurant = "school";
        Log.d("onClick", "Button is Clicked");
        Toast.makeText(getContext(), "Wyszukiwanie szkol w promieniu 2.5 km ...", Toast.LENGTH_LONG).show();
        mMap.clear();
        String url = getUrl(latitude, longitude, Restaurant);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
    }

    public void restauracje() {
        String Restaurant = "restaurant";
        Toast.makeText(getContext(), "Wyszukiwanie restauracji w promieniu 2.5 km ...", Toast.LENGTH_LONG).show();
        Log.d("onClick", "Button is Clicked");
        mMap.clear();
        String url = getUrl(latitude, longitude, Restaurant);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);

    }

    public void szpitale() {
        String Restaurant = "hospital";
        Log.d("onClick", "Button is Clicked");
        Toast.makeText(getContext(), "Wyszukiwanie szpitale w promieniu 2.5 km ...", Toast.LENGTH_LONG).show();
        mMap.clear();
        String url = getUrl(latitude, longitude, Restaurant);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
        getNearbyPlacesData.execute(DataTransfer);
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=bicycling";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlaceAutocompleteFragment f = (PlaceAutocompleteFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.place_fragment);
        if (f != null)
            getActivity().getFragmentManager().beginTransaction().remove(f).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else
                    Toast.makeText(getContext(), "Permision Denied!", Toast.LENGTH_LONG).show();
        }
        return;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            try {
                markersMapHistoryPolylines.add(mMap.addPolyline(lineOptions));
                Log.d("TRUE", "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}