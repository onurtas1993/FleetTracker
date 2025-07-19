package com.onurtas.fleettracker.ViewModel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.onurtas.fleettracker.Fragment.VehiclesGridFragment;
import com.onurtas.fleettracker.Fragment.VehiclesListFragment;

import java.util.ArrayList;
import java.util.List;

public class VehiclesViewModel extends ViewModel {

    private final MutableLiveData<List<Class<? extends Fragment>>> _fragmentClasses = new MutableLiveData<>();
    public LiveData<List<Class<? extends Fragment>>> fragmentClasses = _fragmentClasses;

    public VehiclesViewModel() {
        loadFragmentClasses();
    }

    private void loadFragmentClasses() {
        List<Class<? extends Fragment>> fragmentClassList = new ArrayList<>();
        // Add your fragment classes in the order you want them to appear
        fragmentClassList.add(VehiclesListFragment.class);
        fragmentClassList.add(VehiclesGridFragment.class);
        // Add more fragment classes if needed
        _fragmentClasses.setValue(fragmentClassList);
    }
}