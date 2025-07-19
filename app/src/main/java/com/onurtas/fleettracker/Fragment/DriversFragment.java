package com.onurtas.fleettracker.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.onurtas.fleettracker.Activity.DriverScoreCardActivity;
import com.onurtas.fleettracker.R;
import com.onurtas.fleettracker.ViewModel.DriverViewModel;
import com.onurtas.fleettracker.databinding.FragmentDriversBinding;

import java.util.ArrayList;

public class DriversFragment extends Fragment {

    private FragmentDriversBinding binding;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayedDriverNames = new ArrayList<>();
    private DriverViewModel driverViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDriversBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.DriversFragmentTitle));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        driverViewModel = new ViewModelProvider(this).get(DriverViewModel.class);

        adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, displayedDriverNames);
        binding.driversList.setAdapter(adapter);

        setupUIListeners();
        setupObservers();

        // Fetch drivers only if it's the first creation, ViewModel will retain data on config changes
        if (savedInstanceState == null) {
            driverViewModel.fetchDrivers();
        }

    }

    private void setupUIListeners() {
        binding.driversList.setOnItemClickListener((parent, view, position, id) -> {
            if (position < displayedDriverNames.size()) {
                String selectedDriverName = displayedDriverNames.get(position);
                // For now, DriverScoreCardActivity only needs the name.
                Intent intent = new Intent(getActivity(), DriverScoreCardActivity.class);
                intent.putExtra("DRIVER_NAME", selectedDriverName);
                startActivity(intent);
            }
        });

        binding.searchText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(binding.searchText);
                return true;
            }
            return false;
        });

        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                driverViewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.pullToRefresh.setOnRefreshListener(() -> {
            binding.searchText.setText(""); // Clear search text on refresh
            driverViewModel.fetchDrivers();
        });
    }

    private void setupObservers() {
        // Observe the filtered list of driver names from the ViewModel
        driverViewModel.getFilteredDriverNamesLiveData().observe(getViewLifecycleOwner(), filteredNames -> {
            displayedDriverNames.clear();
            if (filteredNames != null) {
                displayedDriverNames.addAll(filteredNames);
            }
            adapter.notifyDataSetChanged(); // Update the adapter with the new list
        });

        driverViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (!isLoading && binding.pullToRefresh.isRefreshing()) {
                    binding.pullToRefresh.setRefreshing(false);
                }
            }
        });

        driverViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                if (binding.loadingProgressBar.getVisibility() == View.VISIBLE) {
                    binding.loadingProgressBar.setVisibility(View.GONE);
                }
                if (binding.pullToRefresh.isRefreshing()) {
                    binding.pullToRefresh.setRefreshing(false);
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
