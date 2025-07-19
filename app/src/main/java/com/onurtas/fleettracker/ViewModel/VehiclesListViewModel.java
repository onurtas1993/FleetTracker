package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Model.Car;
import com.onurtas.fleettracker.Repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VehiclesListViewModel extends AndroidViewModel {

    private final VehicleRepository repository;

    private final MutableLiveData<List<Car>> filteredCarsData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageData = new MutableLiveData<>();

    public LiveData<List<Car>> getFilteredCars() { return filteredCarsData; }
    public LiveData<Boolean> getIsLoading() { return isLoadingData; }
    public LiveData<String> getErrorMessage() { return errorMessageData; }

    private List<Car> allCarsSourceList = new ArrayList<>();

    public VehiclesListViewModel(@NonNull Application application) {
        super(application);
        repository = new VehicleRepository();
    }

    public void fetchCars() {
        isLoadingData.setValue(true);
        errorMessageData.setValue(null);

        repository.fetchCars(new VehicleRepository.OnDataFetchedCallback() {
            @Override
            public void onSuccess(List<Car> fetchedCars) {
                allCarsSourceList = fetchedCars != null ? new ArrayList<>(fetchedCars) : new ArrayList<>();
                filteredCarsData.postValue(new ArrayList<>(allCarsSourceList));
                isLoadingData.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessageData.postValue(message);
                isLoadingData.postValue(false);
                allCarsSourceList.clear();
                filteredCarsData.postValue(new ArrayList<>());
            }
        });
    }

    public void filterCars(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredCarsData.setValue(new ArrayList<>(allCarsSourceList));
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault()).trim();
            List<Car> filteredList = allCarsSourceList.stream()
                    .filter(car ->
                            (car.plate != null && car.plate.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                                    (car.brand != null && car.brand.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                                    (car.model != null && car.model.toLowerCase(Locale.getDefault()).contains(lowerCaseQuery))
                    ).collect(Collectors.toList());
            filteredCarsData.setValue(filteredList);
        }
    }
}
