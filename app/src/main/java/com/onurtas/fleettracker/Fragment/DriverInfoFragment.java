package com.onurtas.fleettracker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.onurtas.fleettracker.Helper.ColorUtils;
import com.onurtas.fleettracker.Model.Driver;
import com.onurtas.fleettracker.ViewModel.DriverInfoViewModel;
import com.onurtas.fleettracker.databinding.ContentDriverInfoBinding;

public class DriverInfoFragment extends Fragment {

    private DriverInfoViewModel viewModel;
    private ContentDriverInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ContentDriverInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DriverInfoViewModel.class);
        observeViewModel();

        if (savedInstanceState == null) {
            viewModel.fetchDriver();
        }
    }


    private void observeViewModel() {
        binding.loadingProgressBar.setVisibility(View.VISIBLE);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getDriverDetails().observe(getViewLifecycleOwner(), driver -> {
            if (driver != null) {
                updateDriverInfoUI(driver);
            }
        });

    }

    private void updateDriverInfoUI(Driver driver) {
        binding.txtDriverName.setText(driver.driverName);
        binding.txtPlateNumber.setText(driver.plate);
        binding.txtInfoScore.setText(String.valueOf(driver.score));
        binding.txtDate.setText(driver.getLastUpdatedFormatted());
        binding.txtSpeedRate.setText(String.valueOf(driver.speedRate));
        binding.txtMobileUsage.setText(String.valueOf(driver.mobileUsage));
        binding.txtBrakeRate.setText(String.valueOf(driver.brakeRate));
        binding.txtRevRate.setText(String.valueOf(driver.revRate));

        binding.overallScore.setProgressWithAnimation(driver.score, 1000L);
        binding.pbSpeedRate.setProgressWithAnimation(driver.speedRate, 1000L);
        binding.pbMobileUsage.setProgressWithAnimation(driver.mobileUsage, 1000L);
        binding.pbBrakeRate.setProgressWithAnimation(driver.brakeRate, 1000L);
        binding.pbRevRate.setProgressWithAnimation(driver.revRate, 1000L);

        ColorUtils.assignScoreColorToProgressBar(binding.overallScore, driver.score);
        ColorUtils.assignScoreColorToProgressBar(binding.pbSpeedRate, driver.speedRate);
        ColorUtils.assignScoreColorToProgressBar(binding.pbMobileUsage, driver.mobileUsage);
        ColorUtils.assignScoreColorToProgressBar(binding.pbBrakeRate, driver.brakeRate);
        ColorUtils.assignScoreColorToProgressBar(binding.pbRevRate, driver.revRate);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}