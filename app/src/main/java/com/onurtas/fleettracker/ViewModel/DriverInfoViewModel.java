package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Model.Driver;
import com.onurtas.fleettracker.Repository.DriverRepository;

public class DriverInfoViewModel extends AndroidViewModel {


    private final DriverRepository driverRepository;

    private final MutableLiveData<Driver> driverDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);


    public DriverInfoViewModel(@NonNull Application application) {
        super(application);
        driverRepository = new DriverRepository(application);
    }

    public LiveData<Driver> getDriverDetails() {
        return driverDetailsLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }


    public void fetchDriver() {
        isLoadingLiveData.setValue(true);
        // Fetch driver details
        driverRepository.fetchDriver(new DriverRepository.FetchDriverCallback() {
            @Override
            public void onDriverLoaded(Driver driver) {
                driverDetailsLiveData.postValue(driver);
                isLoadingLiveData.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                errorLiveData.postValue(error.toString());
                isLoadingLiveData.postValue(false);
            }
        });
    }


}