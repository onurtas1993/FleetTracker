package com.onurtas.fleettracker.Fragment;

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
import com.onurtas.fleettracker.Helper.MapManager;
import com.onurtas.fleettracker.Helper.MapsCommon;
import com.onurtas.fleettracker.Model.VehicleMarker;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.ViewModel.DashboardViewModel;
import com.onurtas.fleettracker.databinding.FragmentDashboardBinding;

import java.util.List;

public class DashboardFragment extends Fragment implements OnMapReadyCallback {
    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private MapManager mapManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        // Fullscreen toggle
        binding.btnFullscreen.setOnClickListener(v -> {
            int visibility = binding.statisticsLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            binding.statisticsLayout.setVisibility(visibility);
        });

        setupObservers();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(binding.map.getId());
        mapFragment.getMapAsync(this);
    }

    private void setupObservers() {
        viewModel.getStatsLiveData().observe(getViewLifecycleOwner(), stats -> {
            binding.txtActiveVehicles.setText(String.valueOf(stats.activeVehicles));
            binding.txtParkedVehicles.setText(String.valueOf(stats.parkedVehicles));
            binding.txtCrashes.setText(String.valueOf(stats.crashes));
            binding.txtBreaches.setText(String.valueOf(stats.breaches));
            binding.txtUnderMaintenance.setText(String.valueOf(stats.underMaintenance));
            binding.txtFuelConsumptions.setText(String.valueOf(stats.fuelConsumptions));
        });

        viewModel.getMarkerListLiveData().observe(getViewLifecycleOwner(), vehicleMarkers -> {
            binding.loadingProgressBar.setVisibility(View.GONE); // Hide overlay when data is ready
            updateMarkers(vehicleMarkers);
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            binding.loadingProgressBar.setVisibility(View.GONE); // Hide overlay on error
        });
    }

    private void updateMarkers(List<VehicleMarker> vehicleMarkers) {
        if (mapManager == null) return;
        mapManager.updateMarkers(vehicleMarkers);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        String apiKey = getString(R.string.google_maps_key);
        MapsCommon mapsCommonInstance = new MapsCommon(apiKey);

        mapManager = new MapManager(googleMap, mapsCommonInstance);
        viewModel.fetchDashboardData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
