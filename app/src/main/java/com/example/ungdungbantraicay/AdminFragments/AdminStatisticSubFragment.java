package com.example.ungdungbantraicay.AdminFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ungdungbantraicay.AdminAdapter.StatisticAdapter;
import com.example.ungdungbantraicay.DAO.StatisticDAO;
import com.example.ungdungbantraicay.R;

public class AdminStatisticSubFragment extends Fragment {
    private Spinner spnStatType;
    private RecyclerView rvStatistics;
    private StatisticDAO statisticDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_statistic_sub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spnStatType = view.findViewById(R.id.spnStatType);
        rvStatistics = view.findViewById(R.id.rvStatistics);
        statisticDAO = new StatisticDAO(getContext());

        String[] types = {"Theo khách hàng", "Theo ngày", "Theo tháng", "Theo danh mục", "Sản phẩm bán chạy"};
        spnStatType.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, types));

        spnStatType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                loadStats(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadStats(int type) {
        Cursor cursor;
        switch (type) {
            case 0: cursor = statisticDAO.getRevenueByUser(); break;
            case 1: cursor = statisticDAO.getRevenueByDay(); break;
            case 2: cursor = statisticDAO.getRevenueByMonth(); break;
            case 3: cursor = statisticDAO.getRevenueByCategory(); break;
            default: cursor = statisticDAO.getBestSellingFruits(10); break;
        }
        rvStatistics.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStatistics.setAdapter(new StatisticAdapter(cursor, type));
    }
}