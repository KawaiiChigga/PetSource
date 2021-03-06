package com.petsource.upHomeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.petsource.PetListActivity;
import com.petsource.R;
import com.petsource.SplashActivity;
import com.petsource.adapter.PetListAdapter;
import com.petsource.adapter.TransListAdapter;
import com.petsource.adapter.TransStaffListAdapter;
import com.petsource.model.Transaction;
import com.petsource.network.API;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Daniel on 11/9/2016.
 */

public class UpHistoryFragment extends Fragment {
    RecyclerView rvshop;
    RecyclerView rvhistory;
    List<Transaction> transdata;
    List<Transaction> transstaffdata;
    TransListAdapter adapter;
    TransStaffListAdapter adapterstaff;
    FirebaseAuth mFirebaseAuth;
    SwipeRefreshLayout swipeRefresh;
    TextView myTrans, myShop, isEmpty;

    public UpHistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uphistory, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvshop = (RecyclerView) getActivity().findViewById(R.id.rvstaffshop);
        rvhistory = (RecyclerView) getActivity().findViewById(R.id.rvstaffhistory);
        swipeRefresh = (SwipeRefreshLayout) getActivity().findViewById(R.id.refreshUpHistory);
        myTrans = (TextView) getActivity().findViewById(R.id.lblMyHistory);
        myShop = (TextView) getActivity().findViewById(R.id.lblMyShop);
        isEmpty = (TextView) getActivity().findViewById(R.id.lblEmpty);
        isEmpty.setVisibility(View.GONE);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareData();
            }
        });
        swipeRefresh.setRefreshing(true);
        mFirebaseAuth = FirebaseAuth.getInstance();

        transdata = new ArrayList<>();
        transstaffdata = new ArrayList<>();
        prepareData();

        adapter = new TransListAdapter(transdata);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvhistory.setHasFixedSize(true);
        rvhistory.setLayoutManager(manager);
        rvhistory.setAdapter(adapter);

        adapterstaff = new TransStaffListAdapter(transstaffdata);
        LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
        rvshop.setHasFixedSize(true);
        rvshop.setLayoutManager(manager2);
        rvshop.setAdapter(adapterstaff);


    }

    public void prepareData() {
        Call<List<Transaction>> gettransdb = API.Factory.getInstance().getTransUser(mFirebaseAuth.getCurrentUser().getUid());
        gettransdb.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                swipeRefresh.setRefreshing(false);
                transdata.clear();
                if (!response.body().isEmpty()) {
                    myTrans.setVisibility(View.VISIBLE);
                } else {
                    myTrans.setVisibility(View.GONE);
                }
                for (Transaction t : response.body()) {
                    transdata.add(t);
                }
                check();

                adapter = new TransListAdapter(transdata);
                LinearLayoutManager manager = new LinearLayoutManager(getContext());
                rvhistory.setHasFixedSize(true);
                rvhistory.setLayoutManager(manager);
                rvhistory.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Please check your network connection and internet permission", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

        Call<List<Transaction>> gettransstaffdb = API.Factory.getInstance().getTransShop(mFirebaseAuth.getCurrentUser().getUid());
        gettransstaffdb.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {
                swipeRefresh.setRefreshing(false);
                transstaffdata.clear();

                if (!response.body().isEmpty()) {
                    myShop.setVisibility(View.VISIBLE);
                } else {
                    myShop.setVisibility(View.GONE);
                }
                for (Transaction t : response.body()) {
                    transstaffdata.add(t);
                }
                check();

                adapterstaff = new TransStaffListAdapter(transstaffdata);
                LinearLayoutManager manager2 = new LinearLayoutManager(getContext());
                rvshop.setHasFixedSize(true);
                rvshop.setLayoutManager(manager2);
                rvshop.setAdapter(adapterstaff);
            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Please check your network connection and internet permission", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
    public void check() {
        if (transdata.isEmpty() && transstaffdata.isEmpty()) {
            isEmpty.setVisibility(View.VISIBLE);
        } else {
            isEmpty.setVisibility(View.GONE);
        }
    }
}
