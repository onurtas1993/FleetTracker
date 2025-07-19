package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Model.Driver;
import com.onurtas.fleettracker.Repository.DriverRepository;

import java.util.ArrayList;
import java.util.List;

public class DriverViewModel extends AndroidViewModel {

    private final DriverRepository driverRepository;
    private final MutableLiveData<List<Driver>> allDriversLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> filteredDriverNamesLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private String currentQuery = ""; // Stores the current search query

    public DriverViewModel(@NonNull Application application) {
        super(application);
        driverRepository = new DriverRepository(application);
    }

    public LiveData<List<String>> getFilteredDriverNamesLiveData() {
        return filteredDriverNamesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    private void applyFilter() {
        // This version will be used by setSearchQuery
        List<Driver> currentDrivers = allDriversLiveData.getValue();
        if (currentDrivers == null) {
            currentDrivers = new ArrayList<>();
        }
        applyFilter(currentDrivers);
    }

    // Applies the current filter to the list of all drivers
    private void applyFilter(List<Driver> currentDrivers) {
        List<String> driverNamesToShow = new ArrayList<>();

        if (currentQuery.isEmpty()) {
            for (Driver driver : currentDrivers) {
                if (driver.driverName != null) {
                    driverNamesToShow.add(driver.driverName);
                }
            }
        } else {
            for (Driver driver : currentDrivers) {
                if (driver.driverName != null && driver.driverName.toLowerCase().contains(currentQuery)) {
                    driverNamesToShow.add(driver.driverName);
                }
            }
        }
        filteredDriverNamesLiveData.postValue(driverNamesToShow);
    }

    // Called by the Fragment when the search text changes
    public void setSearchQuery(String query) {
        currentQuery = query.toLowerCase().trim();
        applyFilter();
    }

    public void fetchDrivers() {
        isLoading.setValue(true);
        // Clear previous error messages when a new fetch is initiated
        errorLiveData.setValue(null);

        driverRepository.fetchDrivers(new DriverRepository.FetchDriversCallback() {
            @Override
            public void onDriversLoaded(List<Driver> drivers) {
                allDriversLiveData.postValue(drivers);
                applyFilter(drivers); // Apply filter to the newly fetched drivers
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                errorLiveData.postValue(error.toString());
                isLoading.postValue(false);
            }
        });
    }
}
