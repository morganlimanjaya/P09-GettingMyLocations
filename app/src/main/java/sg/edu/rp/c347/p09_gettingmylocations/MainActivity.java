package sg.edu.rp.c347.p09_gettingmylocations;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //create objects
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    String folderLocation;
    private GoogleMap map;

    TextView tvLat;
    TextView tvLong;
    TextView curLat, curLong;
    Button btnCheck, btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLat = (TextView) this.findViewById(R.id.textViewLat);
        tvLong = (TextView) this.findViewById(R.id.textViewLong);
        curLat = (TextView) this.findViewById(R.id.textViewCurLat);
        curLong = (TextView) this.findViewById(R.id.textViewCurLong);

        btnCheck = (Button) this.findViewById(R.id.btnCheck);
        btnStart = (Button) this.findViewById(R.id.btnStart);
        btnStop = (Button) this.findViewById(R.id.btnStop);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Test";

        //It shows that a folder has been created. This is done by the following code.
        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == true) {
                Log.d("File Read/Write", "Folder created");
            }
        }

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File targetFile = new File(folderLocation, "location_m.txt");

                if (targetFile.exists() == true) {
                    String data = "";
                    try {

                        //Reading file content is similar to reading CSV files.
                        // However, it is reading line by line, without the notion of columns.

                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to read!",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    //Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getBaseContext(), check_records_main_activity.class);
                    intent.putExtra("records", data);
                    startActivity(intent);

                }
            }


        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);

                Toast.makeText(getApplicationContext(), "Service is running", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);

                Toast.makeText(getApplicationContext(), "Service stopped", Toast.LENGTH_SHORT).show();
            }
        });

        //map

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                //get current location
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);

                    //map location set

                } else {
                    Log.e("GMap - Permission", "GPS access has not been granted");
                }

            }
        });


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest
                    .PRIORITY_BALANCED_POWER_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setSmallestDisplacement(100);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);


        } else {
            mLocation = null;
            Toast.makeText(MainActivity.this,
                    "Permission not granted to retrieve location info",
                    Toast.LENGTH_SHORT).show();
        }

        if (mLocation != null) {

            tvLat.setText("Lat : " + mLocation.getLatitude());
            tvLong.setText("Lng : " + mLocation.getLongitude());

            curLat.setText("Lat : " + mLocation.getLatitude());
            curLong.setText("Lng : " + mLocation.getLongitude());


        } else {
            Toast.makeText(this, "Location not Detected",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        //the detected location is given by the variable location in the signature

        File targetFile = new File(folderLocation, "location_m.txt");

        String loc = "Lat : " + location.getLatitude() + " Lng : " +
                location.getLongitude() + "";


        try {

            //The data is appended into the file because of the 2nd argument in the FileWriter object.
            // If it is set to false, it will always truncate the file, overwriting the file from the beginning.

            FileWriter writer = new FileWriter(targetFile, true);
            writer.write(loc + "\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to write!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}


