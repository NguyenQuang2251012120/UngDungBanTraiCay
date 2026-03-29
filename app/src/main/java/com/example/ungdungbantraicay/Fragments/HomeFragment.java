package com.example.ungdungbantraicay.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ungdungbantraicay.Adapter.FruitAdapter;
import com.example.ungdungbantraicay.DAO.FruitDAO;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerFruit;
    FruitAdapter adapter;
    ArrayList<Fruit> list;
    FruitDAO fruitDAO;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerFruit = view.findViewById(R.id.recyclerFruit);

        fruitDAO = new FruitDAO(getContext());
        list = fruitDAO.getAllFruit();

        adapter = new FruitAdapter(getContext(), list);

        recyclerFruit.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerFruit.setAdapter(adapter);

        return view;
    }
}