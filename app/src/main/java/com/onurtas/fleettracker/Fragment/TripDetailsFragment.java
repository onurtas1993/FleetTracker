package com.onurtas.fleettracker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;
import com.onurtas.fleettracker.Activity.DriverInfoActivity;
import com.onurtas.fleettracker.Activity.VehicleStatusActivity;
import com.onurtas.fleettracker.Helper.ColorUtils;
import com.onurtas.fleettracker.Helper.MapManager;
import com.onurtas.fleettracker.Helper.MapsCommon;
import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.ViewModel.TripsViewModel;
import com.onurtas.fleettracker.databinding.ContentTripDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripDetailsFragment extends Fragment implements OnMapReadyCallback {

    private ContentTripDetailsBinding binding;
    private TripsViewModel viewModel;
    private MapManager mapManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ContentTripDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.tripDetailsMap);
        mapFragment.getMapAsync(this);

        viewModel = new ViewModelProvider(this).get(TripsViewModel.class);

        setupUI();
        setupObservers();

        viewModel.fetchTripDetails();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String apiKey = getString(R.string.google_maps_key);
        MapsCommon mapsCommonInstance = new MapsCommon(apiKey);

        mapManager = new MapManager(googleMap, mapsCommonInstance);

        // Attempt to display trip details if data is already available
        Trip currentTrip = viewModel.getTripLiveData().getValue();
        if (currentTrip != null) {
            mapManager.displayTripDetails(currentTrip, requireActivity());
        }
    }

    private void setupUI() {
        MaterialButton btnDriver = binding.btnDriver;
        MaterialButton btnPlate = binding.btnPlate;

        btnDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DriverInfoActivity.class);
                startActivity(intent);
            }
        });

        btnPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VehicleStatusActivity.class);
                intent.putExtra("PLATE", binding.btnPlate.getText());
                startActivity(intent);
            }
        });
    }

    private void setupObservers() {
        viewModel.getTripLiveData().observe(getViewLifecycleOwner(), trip -> {
            if (trip != null) {
                updateUI(trip);
                if (mapManager != null) {
                    mapManager.displayTripDetails(trip, requireActivity());
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) { // Added check for empty error string
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI(Trip trip) {
        binding.tripDetailsInclude.txtFromLocation.setText(trip.friendly_start_street_name);
        binding.tripDetailsInclude.txtEndLocation.setText(trip.friendly_end_street_name);
        binding.tripDetailsInclude.txtDistance.setText(String.valueOf(trip.distance));
        binding.tripDetailsInclude.txtElapsedTime.setText(String.valueOf(trip.elapsed_time_in_minutes));
        binding.tripDetailsInclude.txtPlate.setText(trip.plate);
        binding.tripDetailsInclude.txtFuelConsumption.setText(String.valueOf(trip.fuel_consumption));

        binding.txtOverallScore.setText(String.valueOf(trip.overall_driving_score));
        binding.txtEconomyScore.setText(String.valueOf(trip.economy_score));
        binding.txtSafetyScore.setText(String.valueOf(trip.safety_score));

        binding.overallScore.setProgressWithAnimation(trip.overall_driving_score, 750L);
        binding.economyScore.setProgressWithAnimation(trip.economy_score, 750L);
        binding.safetyScore.setProgressWithAnimation(trip.safety_score, 750L);

        ColorUtils.assignScoreColorToProgressBar(binding.overallScore, trip.overall_driving_score);
        ColorUtils.assignScoreColorToProgressBar(binding.economyScore, trip.economy_score);
        ColorUtils.assignScoreColorToProgressBar(binding.safetyScore, trip.safety_score);


        binding.btnDriver.setText(trip.driver_name);
        binding.btnPlate.setText(trip.plate);

        int totalIdleMin = 0;
        if (trip.idles != null) {
            totalIdleMin = trip.idles.stream().mapToInt(idle -> idle.duration).sum();
        }
        String currentElapsedTime = String.valueOf(trip.elapsed_time_in_minutes);
        binding.tripDetailsInclude.txtElapsedTime.setText(currentElapsedTime);

        Date startDate = new Date(trip.start_epoch_date * 1000L);
        Date endDate = new Date(trip.end_epoch_date * 1000L);


        SimpleDateFormat localDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String startNiceTime = localDateFormat.format(startDate);
        String endNiceTime = localDateFormat.format(endDate);

        binding.tripDetailsInclude.txtStartDate.setText(startNiceTime);
        binding.tripDetailsInclude.txtEndTime.setText(endNiceTime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapManager != null) {
            mapManager.clearMarkers();
        }
        mapManager = null;
        binding = null;
    }
}