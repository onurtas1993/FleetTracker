package com.onurtas.fleettracker.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.onurtas.fleettracker.Fragment.VehicleStatusFragment;
import com.onurtas.fleettracker.R;

public class VehicleStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_status);

        String vehiclePlate = getIntent().getStringExtra("PLATE");

        if (vehiclePlate != null && !vehiclePlate.isEmpty()) {

            VehicleStatusFragment fragment = VehicleStatusFragment.newInstance(vehiclePlate);

            // Add the Fragment to the 'fragment_container_view'
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container_view, fragment);
            fragmentTransaction.commit();

        } else {
            Toast.makeText(this, "Error: Vehicle plate not received.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}