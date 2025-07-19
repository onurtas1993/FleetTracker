package com.onurtas.fleettracker.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.onurtas.fleettracker.Helper.ApiConstants;
import com.onurtas.fleettracker.Helper.NetworkBridge;
import com.onurtas.fleettracker.Model.Car; // Import the Car class
import com.onurtas.fleettracker.Model.Error;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VehicleStatusViewModel extends AndroidViewModel {

    // Use Car.Status as the type for LiveData
    private final MutableLiveData<Car.Status> _vehicleStatus = new MutableLiveData<>();
    private final MutableLiveData<List<Error>> _errorCodes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final RequestQueue requestQueue;

    // Use Car.Status as the type for LiveData
    public LiveData<Car.Status> vehicleStatus = _vehicleStatus;
    public LiveData<List<Error>> errorCodes = _errorCodes;
    public LiveData<Boolean> isLoading = _isLoading;
    public LiveData<String> errorMessage = _errorMessage;

    public VehicleStatusViewModel(Application application) {
        super(application);
        requestQueue = NetworkBridge.getInstance().getRequestQueue();
    }

    public void fetchVehicleStatus(String plate) {
        _isLoading.setValue(true);
        _errorMessage.setValue(null);

        String requestURL = ApiConstants.CAR_STATUS_URL;

        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, requestURL, null,
                response -> {
                    try {
                        // Create an instance of Car.Status
                        Car.Status statusData = new Car.Status(
                                response.optInt("total_miles"),
                                response.optInt("total_trips"),
                                response.optInt("mpg"),
                                response.optString("status"),
                                response.optInt("battery_voltage"),
                                response.optInt("exhaust_emmision"),
                                response.optInt("engine_coolant_temp"),
                                response.optInt("transportation"),
                                response.optString("socket")
                        );
                        _vehicleStatus.postValue(statusData);

                        ArrayList<Error> errors = new ArrayList<>();
                        JSONArray rawErrorCodes = response.getJSONArray("engine_faults");
                        for (int i = 0; i < rawErrorCodes.length(); i++) {
                            JSONObject data = rawErrorCodes.getJSONObject(i);
                            String errorCode = data.names().getString(0);
                            errors.add(new Error(errorCode, data.getString(errorCode)));
                        }
                        _errorCodes.postValue(errors);

                        _isLoading.postValue(false);

                    } catch (JSONException e) {
                        _errorMessage.postValue("Error parsing data: " + e.getMessage());
                        _isLoading.postValue(false);
                    }
                },
                error -> {
                    _errorMessage.postValue("Ops.. Something went wrong: " + error.getMessage());
                    _isLoading.postValue(false);
                });
        requestQueue.add(getRequest);
    }
}
