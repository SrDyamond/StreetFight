package com.afundacionfp.street_fight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, LocationListener {
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private Location loc;
    private LocationManager locManager;
    private GeoPoint locPoint;
    private IMapController mapController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //  Use icon on another class
        if (Coordinates.resources == null) {
            Coordinates.resources = getResources();
        }

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // Context ctx = getApplicationContext();
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        setContentView(R.layout.activity_main);

        //  Localization
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        //  Creacion de mapa ,controlador y overlay
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(20.0);    // Initial zoom
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
        map.setMinZoomLevel(15.0);  // Max zoom out

        // HAY QUE INICIAR EL EMULADOR Y SETEAR LA LOCALIZACIÓN ANTES DE EJECUTAR LA APP
        if (loc != null) { // ESTO ES PARA QUE LA APP NO CASQUE
            locPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());

            mapController.setCenter(locPoint);

            //  Add coordinates
            Coordinates.coors(map);

            /*
            //  Prueva de icono en mapa
            GeoPoint startPoint2 = new GeoPoint(43.36209, -8.41248);
            Marker startMarker2 = new Marker(map);
            startMarker2.setPosition(startPoint2);
            startMarker2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            startMarker2.setIcon(getResources().getDrawable(R.drawable.ic_torchlight_help_icon, null));
            startMarker2.setTitle("Start point");
            map.getOverlays().add(startMarker2);
            //  Prueba de poligono rojo
            List<GeoPoint> geoPoints = new ArrayList<>();
            geoPoints.add(new GeoPoint(43.36367, -8.404));
            geoPoints.add(new GeoPoint(43.35967, -8.40209));
            geoPoints.add(new GeoPoint(43.35984, -8.39767));
            geoPoints.add(new GeoPoint(43.36316, -8.39668));
            geoPoints.add(new GeoPoint(43.36393, -8.39876));
            //add your points here
            Polygon polygon = new Polygon();    //see note below
            polygon.getFillPaint().setColor(Color.RED); //set fill color
            geoPoints.add(geoPoints.get(0));    //forces the loop to close(connect last point to first point)
            polygon.setPoints(geoPoints);
            polygon.setTitle("A sample polygon");

            map.getOverlayManager().add(polygon);
            */

            requestPermissionsIfNecessary(new String[]{
                    // if you need to show the current location, uncomment the line below
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    // WRITE_EXTERNAL_STORAGE is required in order to show the map
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        }
    }

    //  Runs wen the location changes
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locPoint = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        mapController.animateTo(locPoint);
    }

    //  Must stay to work
    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(new String[0]),
                REQUEST_PERMISSIONS_REQUEST_CODE
            );
        }
    }
}