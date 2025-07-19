package com.onurtas.fleettracker.ViewModel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Helper.GeoUtils;
import com.onurtas.fleettracker.Model.Trip;
import com.onurtas.fleettracker.Repository.TripsRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TripsViewModel extends AndroidViewModel {

    private final TripsRepository repository;
    private final ExecutorService executor;

    private final MutableLiveData<Trip> tripLiveData = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Trip>> tripsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    private final ArrayList<Trip> originalTrips = new ArrayList<>();
    private Calendar currentFilterStartDate = null;
    private Calendar currentFilterEndDate = null;

    public TripsViewModel(@NonNull Application application) {
        super(application);

        executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        GeoUtils geoUtils = new GeoUtils(executor, mainHandler);
        repository = new TripsRepository(application.getApplicationContext(), geoUtils);
    }

    public LiveData<Trip> getTripLiveData() {
        return tripLiveData;
    }

    public LiveData<ArrayList<Trip>> getTripsLiveData() {
        return tripsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void fetchTrips() {
        isLoading.setValue(true);
        repository.fetchTrips(new TripsRepository.TripsCallback() {
            @Override
            public void onTripsLoaded(ArrayList<Trip> trips) {
                originalTrips.clear();
                originalTrips.addAll(trips);
                // Apply current filter if it exists, otherwise show all
                if (currentFilterStartDate != null && currentFilterEndDate != null) {
                    filterTripsByDate(currentFilterStartDate, currentFilterEndDate);
                } else {
                    tripsLiveData.postValue(new ArrayList<>(originalTrips));
                }
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.postValue(e.getMessage());
                isLoading.postValue(false);
            }
        });
    }

    public void filterTripsByDate(Calendar startDateCal, Calendar endDateCal) {
        currentFilterStartDate = startDateCal;
        currentFilterEndDate = endDateCal;

        if (originalTrips.isEmpty()) {
            tripsLiveData.postValue(new ArrayList<>());
            return;
        }

        isLoading.setValue(true);
        // Ensure the end of the endDateCal is set to the end of the day for inclusive filtering
        endDateCal.set(Calendar.HOUR_OF_DAY, 23);
        endDateCal.set(Calendar.MINUTE, 59);
        endDateCal.set(Calendar.SECOND, 59);
        endDateCal.set(Calendar.MILLISECOND, 999);

        long startTimeMillis = startDateCal.getTimeInMillis();
        long endTimeMillis = endDateCal.getTimeInMillis();

        ArrayList<Trip> filteredList = new ArrayList<>();
        for (Trip trip : originalTrips) {
            // trip.start_epoch_date is in seconds, convert to milliseconds
            long tripStartTimeMillis = trip.start_epoch_date * 1000L;
            if (tripStartTimeMillis >= startTimeMillis && tripStartTimeMillis <= endTimeMillis) {
                filteredList.add(trip);
            }
        }
        tripsLiveData.postValue(filteredList);
        isLoading.postValue(false);
    }

    public void clearDateFilter() {
        currentFilterStartDate = null;
        currentFilterEndDate = null;
        if (!originalTrips.isEmpty()) {
            tripsLiveData.postValue(new ArrayList<>(originalTrips));
        } else {
            // If originalTrips is empty, fetchTrips might be needed or post empty
            tripsLiveData.postValue(new ArrayList<>());
        }
    }


    public void fetchTripDetails() {
        isLoading.setValue(true);
        repository.fetchTripDetails(new TripsRepository.TripDetailsCallback() {
            @Override
            public void onTripLoaded(Trip trip) {
                tripLiveData.postValue(trip);
                isLoading.postValue(false);
            }

            @Override
            public void onError(Exception e) {
                errorLiveData.postValue(e.getMessage());
                isLoading.postValue(false);
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
