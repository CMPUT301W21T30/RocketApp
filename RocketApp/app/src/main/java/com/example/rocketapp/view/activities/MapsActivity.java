package com.example.rocketapp.view.activities;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;

import com.example.rocketapp.R;
import com.example.rocketapp.controller.ExperimentManager;
import com.example.rocketapp.controller.TrialManager;
import com.example.rocketapp.model.experiments.Experiment;
import com.example.rocketapp.model.trials.Trial;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

//Citation - https://developers.google.com/maps/documentation/android-sdk/start
//Google
//Last updated 2021-04-07 UTC.
//License - Apache 2.0 License.

/**
 * Displays and adds markers to locations of all trials
 * If multiple trials have same location then clicking on marker will alternate between their info
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Experiment experiment;
    private ArrayList<? extends Trial> trialList;

    /**
     * Set up for map and gets ArrayList of trials
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra(Experiment.ID_KEY));
        TrialManager.listen(experiment, this::onUpdate);
    }

    
    /**
     * Obtain the SupportMapFragment and get notified when the map is ready to be used.
     * @param experiment - Experiment
     */
    private void onUpdate(Experiment experiment) {
        trialList = experiment.getTrials();

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (trialList == null) return;
        mMap = googleMap;

        
        for(int i = 0; i<trialList.size(); i++){
            try{
            LatLng mark = new LatLng(trialList.get(i).getLocation().getLatitude(), trialList.get(i).getLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(mark).title(trialList.get(i).getValueString()));
        }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}