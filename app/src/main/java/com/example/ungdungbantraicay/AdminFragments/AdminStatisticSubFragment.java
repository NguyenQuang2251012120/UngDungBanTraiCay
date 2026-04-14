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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AdminStatisticSubFragment extends Fragment {
    private Spinner spnStatType;
    private RecyclerView rvStatistics;
    private StatisticDAO statisticDAO;

    private BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_statistic_sub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        barChart = view.findViewById(R.id.barChart);
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadStats(int type) {
        Cursor cursor;

        switch (type) {
            case 0:
                cursor = statisticDAO.getRevenueByUser();
                break;
            case 1:
                cursor = statisticDAO.getRevenueByDay();
                break;
            case 2:
                cursor = statisticDAO.getRevenueByMonth();
                break;
            case 3:
                cursor = statisticDAO.getRevenueByCategory();
                break;
            default:
                cursor = statisticDAO.getBestSellingFruits(10);
                break;
        }

        barChart.setVisibility(View.VISIBLE);

        // 👇 debug data
        if (cursor == null || cursor.getCount() == 0) {
            barChart.clear();
            return;
        }

        showBarChart(cursor);
    }

    private void showBarChart(Cursor cursor) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String label = cursor.getString(0);

                float value;
                try {
                    value = cursor.getFloat(1);
                } catch (Exception e) {
                    value = Float.parseFloat(cursor.getString(1));
                }

                entries.add(new BarEntry(index, value));
                labels.add(label);

                index++;
            } while (cursor.moveToNext());
        }

        // nếu không có data thì khỏi vẽ
        if (entries.size() == 0) {
            barChart.clear();
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Thống kê");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        // trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }
}