package appcontest.playabq;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    // park/comm variables:
    // data = raw map read from Json
    // list = list of maps, 1 per park/ctr
    // aliases maps Json field names to their aliases
    private Map parkData;
    private List<Map> parkList;
    private Map<String,String> parkAliases;
    private Map commData;
    private List<Map> commList;
    private Map<String,String> commAliases;

    // polygonMap has park polygons as keys, their names as values
    // for use in click listener
    private Map<Polygon, String> polygonMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        parkData = JsonParser.getParkData(this);
        parkList = JsonParser.getParkList(parkData);
        parkAliases = JsonParser.getAliases(parkData);

        commData = JsonParser.getCommData(this);
        commList = JsonParser.getCommList(commData);
        commAliases = JsonParser.getAliases(commData);


        /*for (Map park : parkList)  {
            System.out.println(park.get("PARKNAME"));
            Map coords = (Map) park.get("geometry");
            for (Object key : coords.keySet()) {
                List coordlist = (List) coords.get(key);
                for (Object item : coordlist) {
                    System.out.println(item.toString()); // returns array of points for poly
                }
            }
        }*/

        /**
        try {
            TestJackson.testJackson(getAssets().open(TestJackson.TEST_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        **/
    }


    /*
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
        getPolys();
        markCenters();
        /*
        for (Polygon poly : polygonMap.keySet()) {
            System.out.println(polygonMap.get(poly));
            System.out.println(poly.getPoints());
        }*/
        //mMap.setOnMapLoadedCallback(this);
    }

    //this method isn't working
    @Override
    public void onMapLoaded() {
        /*LatLngBounds abqBounds = new LatLngBounds(
                new LatLng(34.946766, -106.471163), new LatLng(35.218054, -106.881796));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(abqBounds, 0));*/
    }
    

    // puts polygons on GoogleMap, also adds them to polygon-parkname map
    private void getPolys() {
        HashMap<Polygon,String> polygonMap = new HashMap<>();
        for (Map park : parkList)  {
            String name = (String) park.get("PARKNAME");
            Map coords = (Map) park.get("geometry");
            // get arraylist of arraylists representing contiguous areas
            for (Object key : coords.keySet()) {
                List polys = (List) coords.get(key);
                for (Object poly : polys) {
                    //poly is arraylist of 2-element arraylists
                    // representing closed polygon
                    ArrayList coordList = (ArrayList) poly;
                    List <LatLng> vtxList = new ArrayList<LatLng>();
                    for (Object coord : coordList) {
                        ArrayList<Double> doubleList = (ArrayList) coord;
                        double lat = doubleList.get(1);
                        double lon = doubleList.get(0);
                        LatLng latlon = new LatLng(lat, lon);
                        vtxList.add(latlon);
                    }
                    PolygonOptions polyOpt = new PolygonOptions()
                            .addAll(vtxList)
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE);
                    Polygon newPoly = mMap.addPolygon(polyOpt);
                    polygonMap.put(newPoly, name);
                }
               }
            }
        }

        private void markCenters() {
            for (Map ctr : commList) {
                Map geometry = (Map) ctr.get("geometry");
                double lat = (double) geometry.get("y");
                double lon = (double) geometry.get("x");
                String name = (String) ctr.get("CENTERNAME");
                mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(name));
            }
        }

    }


