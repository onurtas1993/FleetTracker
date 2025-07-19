package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Model.Driver;
import com.onurtas.fleettracker.Repository.DriverRepository;

public class DriverScoreCardViewModel extends AndroidViewModel {
    private final DriverRepository driverRepository;
    private final MutableLiveData<Driver> driverScoreCardLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public DriverScoreCardViewModel(@NonNull Application application) {
        super(application);
        driverRepository = new DriverRepository(application);

    }

    public LiveData<Driver> getDriverScoresLiveData() {
        return driverScoreCardLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }


    public void fetchScores() {
        isLoading.setValue(true);

        driverRepository.fetchDriver(new DriverRepository.FetchDriverCallback() {
            @Override
            public void onDriverLoaded(Driver driver) {
                driverScoreCardLiveData.postValue(driver);
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                errorLiveData.postValue(error.getMessage());
                isLoading.postValue(false);
            }
        });

        driverScoreCardLiveData.observeForever(new androidx.lifecycle.Observer<Driver>() {
            @Override
            public void onChanged(Driver driver) {
                if (driver != null) {
                    isLoading.postValue(false);
                    driverScoreCardLiveData.removeObserver(this);
                }
            }
        });
        errorLiveData.observeForever(new androidx.lifecycle.Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error != null) {
                    isLoading.postValue(false);
                    errorLiveData.removeObserver(this);
                }
            }
        });
    }
}
