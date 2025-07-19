package com.onurtas.fleettracker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.onurtas.fleettracker.Helper.ErrorsListAdaptor;
import com.onurtas.fleettracker.Model.Car;
import com.onurtas.fleettracker.Model.Error;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.ViewModel.VehicleStatusViewModel;
import com.onurtas.fleettracker.databinding.FragmentVehicleStatusBinding;

import java.util.ArrayList;

public class VehicleStatusFragment extends Fragment {

    private static final String ARG_PLATE = "PLATE";
    private FragmentVehicleStatusBinding binding;
    private VehicleStatusViewModel vehicleStatusViewModel;
    private ErrorsListAdaptor adapter;
    private ArrayList<Error> errors;
    private String vehiclePlate;

    public static VehicleStatusFragment newInstance(String plate) {
        VehicleStatusFragment fragment = new VehicleStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLATE, plate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehiclePlate = getArguments().getString(ARG_PLATE);
        }
        errors = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVehicleStatusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vehicleStatusViewModel = new ViewModelProvider(this).get(VehicleStatusViewModel.class);

        adapter = new ErrorsListAdaptor(requireActivity(), errors);
        binding.errorsList.setAdapter(adapter);

        observeViewModel();

        if (vehiclePlate != null && !vehiclePlate.isEmpty()) {
            binding.loadingProgressBar.setVisibility(View.VISIBLE);
            vehicleStatusViewModel.fetchVehicleStatus(vehiclePlate);
        } else {
            Toast.makeText(requireContext(), "Error: Vehicle plate not available in Fragment.", Toast.LENGTH_LONG).show();
        }
    }

    private void observeViewModel() {
        vehicleStatusViewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (!isLoading) {
                binding.progLay.setVisibility(View.VISIBLE);
            } else {
                binding.progLay.setVisibility(View.GONE);
            }
        });

        vehicleStatusViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                binding.loadingProgressBar.setVisibility(View.GONE);
            }
        });

        vehicleStatusViewModel.vehicleStatus.observe(getViewLifecycleOwner(), statusData -> {
            if (statusData != null) { // Add null check for safety
                updateUiWithStatusData(statusData);
            }
        });

        vehicleStatusViewModel.errorCodes.observe(getViewLifecycleOwner(), errorCodesList -> {
            if (errorCodesList != null) {
                errors.clear();
                errors.addAll(errorCodesList);
                adapter.notifyDataSetChanged();

                if (errorCodesList.isEmpty()) {
                    binding.faultsIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.check, null));
                } else {
                    binding.faultsIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.warning, null));
                }
            }
        });
    }

    // Update method to accept Car.Status
    private void updateUiWithStatusData(Car.Status statusData) {
        binding.milesTxt.setText(String.valueOf(statusData.totalMiles));
        binding.tripsTxt.setText(String.valueOf(statusData.totalTrips));
        binding.mpgTxt.setText(String.valueOf(statusData.mpg));
        binding.engineTxt.setText(statusData.status);

        int batteryResult = statusData.batteryVoltage;
        binding.batteryStatusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), batteryResult == 0 ? R.drawable.accumulator_red : R.drawable.accumulator_green, null));

        int exhaustEmission = statusData.exhaustEmission;
        binding.exhaustStatusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), exhaustEmission == 0 ? R.drawable.warning : R.drawable.ok, null));

        int coolantResult = statusData.engineCoolantTemp;
        binding.engineStatusIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), coolantResult == 0 ? R.drawable.thermometer_red : R.drawable.thermometer_green, null));

        int transportationResult = statusData.transportation;
        binding.transportationIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), transportationResult == 0 ? R.drawable.transporter_red : R.drawable.transporter_green, null));

        String socketResult = statusData.socketStatus;
        binding.socketIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), "connected".equalsIgnoreCase(socketResult) ? R.drawable.socket_green : R.drawable.socket_red, null));

        if (exhaustEmission == 0 || coolantResult == 0) {
            binding.statusIconImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.error, null));
        } else if (batteryResult == 0) {
            binding.statusIconImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.warning, null));
        } else {
            binding.statusIconImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.check, null));
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
