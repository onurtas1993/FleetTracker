package com.onurtas.fleettracker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.onurtas.fleettracker.Activity.VehicleStatusActivity;
import com.onurtas.fleettracker.Helper.CarListAdaptor;
import com.onurtas.fleettracker.Model.Car;
import com.onurtas.fleettracker.ViewModel.VehiclesListViewModel;
import com.onurtas.fleettracker.databinding.FragmentVehiclesGridBinding;

import java.util.ArrayList;
import java.util.List;

public class VehiclesGridFragment extends Fragment {

    private final List<Car> currentCarsList = new ArrayList<>();
    private FragmentVehiclesGridBinding binding;
    private VehiclesListViewModel viewModel;
    private CarListAdaptor adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVehiclesGridBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(VehiclesListViewModel.class);

        adapter = new CarListAdaptor(requireActivity(), (ArrayList<Car>) currentCarsList);
        binding.vehiclesList.setAdapter(adapter);

        setupUIListeners();
        observeViewModel();

        viewModel.fetchCars();
    }

    private void setupUIListeners() {
        binding.vehiclesList.setOnItemClickListener((parent, itemView, position, id) -> {
            Car selectedCar = currentCarsList.get(position);
            Intent intent = new Intent(getActivity(), VehicleStatusActivity.class);
            intent.putExtra("PLATE", selectedCar.plate);
            startActivity(intent);
        });

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && binding.searchText.getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(binding.searchText.getWindowToken(), 0);
                }
                String query = binding.searchText.getText().toString();
                viewModel.filterCars(query);
                return true;
            }
            return false;
        });

        binding.pullToRefresh.setOnRefreshListener(() -> {
            viewModel.fetchCars();
        });
    }

    private void observeViewModel() {
        viewModel.getFilteredCars().observe(getViewLifecycleOwner(), cars -> {
            currentCarsList.clear();
            currentCarsList.addAll(cars);
            adapter.notifyDataSetChanged();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.pullToRefresh.setRefreshing(isLoading);
            if (isLoading && currentCarsList.isEmpty()) {
                binding.loadingProgressBar.setVisibility(View.VISIBLE);
            } else if (!isLoading) { // Hide camouflage when not loading
                binding.loadingProgressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}