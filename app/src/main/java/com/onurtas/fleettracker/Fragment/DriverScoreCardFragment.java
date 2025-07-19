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

import com.onurtas.fleettracker.Helper.ColorUtils;
import com.onurtas.fleettracker.Model.Driver;
import com.onurtas.fleettracker.ViewModel.DriverScoreCardViewModel;
import com.onurtas.fleettracker.databinding.ContentDriverScoreCardBinding;

public class DriverScoreCardFragment extends Fragment {

    private ContentDriverScoreCardBinding binding;
    private DriverScoreCardViewModel driverScoreCardViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = ContentDriverScoreCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        driverScoreCardViewModel = new ViewModelProvider(this).get(DriverScoreCardViewModel.class);
        setupObservers();

        if (savedInstanceState == null) {
            driverScoreCardViewModel.fetchScores();
        }
    }

    private void setupObservers() {
        driverScoreCardViewModel.getDriverScoresLiveData().observe(getViewLifecycleOwner(), driver -> {
            if (driver != null) {
                updateUI(driver);
            }
        });

        driverScoreCardViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        driverScoreCardViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                if (binding.loadingProgressBar.getVisibility() == View.VISIBLE) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateUI(Driver driver) {
        binding.txtOverallScore.setText(String.valueOf(driver.score));
        binding.txtTotalMileage.setText(String.valueOf(driver.totalMileage));
        binding.txtTotalTrips.setText(String.valueOf(driver.totalTrips));
        binding.txtLastUpdated.setText(driver.getLastUpdatedFormatted());
        binding.progressOverall.setProgressWithAnimation(driver.score, 750L);

        ColorUtils.assignScoreColorToProgressBar(binding.progressOverall, driver.score);
    }

}
