package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onurtas.fleettracker.Helper.NetworkBridge;
import com.onurtas.fleettracker.Model.DashboardResult;
import com.onurtas.fleettracker.Model.DashboardStats;
import com.onurtas.fleettracker.Model.VehicleMarker;
import com.onurtas.fleettracker.Repository.DashboardRepository;

import java.util.List;

public class DashboardViewModel extends AndroidViewModel {

    private final DashboardRepository repository;

    private final MutableLiveData<DashboardStats> _statsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<VehicleMarker>> _markerListLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> _errorLiveData = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        repository = new DashboardRepository(NetworkBridge.getInstance().getRequestQueue());
    }

    public LiveData<DashboardStats> getStatsLiveData() {
        return _statsLiveData;
    }

    public LiveData<List<VehicleMarker>> getMarkerListLiveData() {
        return _markerListLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return _errorLiveData;
    }

    public void fetchDashboardData() {

        DashboardRepository.DashboardDataCallback callback = new DashboardRepository.DashboardDataCallback() {
            @Override
            public void onSuccess(DashboardResult data) {
                _statsLiveData.postValue(data.getStats());
                _markerListLiveData.postValue(data.getMarkers());
            }

            @Override
            public void onError(String errorMessage) {
                _errorLiveData.postValue(errorMessage);
            }
        };
        repository.fetchDashboardData(callback);
    }
}
