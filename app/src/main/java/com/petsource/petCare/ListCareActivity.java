package com.petsource.petCare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.petsource.R;
import com.petsource.adapter.ListCareAdapter;

import com.petsource.model.Pet;
import com.petsource.model.Shop;
import com.petsource.network.API;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCareActivity extends AppCompatActivity {
    public static Pet ChosePet;
    public static String idStaff;
    private List<Shop> data;
    private RecyclerView petRV;
    private TextView lblPetCareTitle;
    private TextView isEmpty;

    public ListCareAdapter adapter;
    public SwipeRefreshLayout swipeRefresh;
    public static Activity listCareActivity;

    public static String cusDateStart;
    public static String cusDateEnd;

    public ListCareActivity() {
        listCareActivity = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_care);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FRADMCN.TTF");

        lblPetCareTitle = (TextView) findViewById(R.id.lblCareListTitle);
        lblPetCareTitle.setTypeface(typeface);

        petRV = (RecyclerView) findViewById(R.id.rvListCare);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.refreshpetlist);

        isEmpty = (TextView) findViewById(R.id.lblEmpty);
        isEmpty.setVisibility(View.GONE);
        data = new ArrayList<>();
        prepareData();

        adapter = new ListCareAdapter(data);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        petRV.setHasFixedSize(true);
        petRV.setLayoutManager(manager);
        petRV.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
            }
        });
        swipeRefresh.setRefreshing(true);

    }

    public void prepareData() {
        Call<List<Shop>> p = API.Factory.getInstance().getCare(1);
        p.enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, Response<List<Shop>> response) {
                for (Shop s : response.body()) {
                    data.clear();
                    for (Shop i : response.body()) {
                        data.add(i);
                    }
                    if (data.isEmpty()) {
                        isEmpty.setVisibility(View.VISIBLE);
                    } else {
                        isEmpty.setVisibility(View.GONE);
                    }
                    adapter = new ListCareAdapter(data);
                    LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
                    petRV.setHasFixedSize(true);
                    petRV.setLayoutManager(manager);
                    petRV.setAdapter(adapter);
                }

            }
            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(ListCareActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
