package com.example.rocketapp.view;

import androidx.fragment.app.FragmentActivity;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Experiment experiment;
    private ArrayList<? extends Trial> trialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        experiment = ExperimentManager.getExperiment(getIntent().getSerializableExtra("id"));
        TrialManager.listen(experiment, this::onUpdate);
    }

    private void onUpdate(Experiment experiment) {
        trialList = experiment.getTrials();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        if (trialList == null) return;
        mMap = googleMap;

        
        for(int i = 0; i<trialList.size(); i++){
            try{
            LatLng mark = new LatLng(trialList.get(i).getLatitude(), trialList.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(mark).title(trialList.get(i).getValueString()));
        }
            catch (Exception e){
                System.out.println(e);
            }
        }
    }
}