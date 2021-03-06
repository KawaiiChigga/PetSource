package com.petsource;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.petsource.R;
import com.petsource.model.Info;
import com.petsource.model.User;
import com.petsource.network.API;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private EditText txtCity;
    private TextView txtBirthdate;
    private EditText txtJob;
    private EditText txtStreet;
    private Button btnOK;

    private Button btnUpAccountGo;
    private TextView lblUpdateTitle;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_acc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FRADMCN.TTF");

        btnUpAccountGo = (Button) findViewById(R.id.btnUpAccountGo);
        btnUpAccountGo.setTypeface(typeface);

        lblUpdateTitle = (TextView) findViewById(R.id.lblUpdateTitle);
        lblUpdateTitle.setTypeface(typeface);

        txtCity = (EditText) findViewById(R.id.txtUpAccountCity);
        txtBirthdate = (TextView) findViewById(R.id.txtUpAccountBirthdate);
        txtJob = (EditText) findViewById(R.id.txtUpAccountJob);
        txtStreet = (EditText) findViewById(R.id.txtUpAccountStreet);

        mFirebaseAuth = FirebaseAuth.getInstance();

        txtBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        UpdateAccActivity.this,
                        2000,
                        0,
                        1
                );
                dpd.vibrate(false);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        btnOK = (Button) findViewById(R.id.btnUpAccountGo);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<List<Info>> getUser = API.Factory.getInstance().checkAccount(mFirebaseAuth.getCurrentUser().getUid());
                getUser.enqueue(new Callback<List<Info>>() {
                    @Override
                    public void onResponse(Call<List<Info>> call, Response<List<Info>> response) {
                        Call<Info> u = API.Factory.getInstance().updateAccount(
                                response.body().get(0).getId(),
                                txtStreet.getText().toString(),
                                txtCity.getText().toString(),
                                txtBirthdate.getText().toString(),
                                txtJob.getText().toString(),
                                1,
                                0
                        );
                        u.enqueue(new Callback<Info>() {
                            @Override
                            public void onResponse(Call<Info> call, Response<Info> response) {
                                finish();
                            }

                            @Override
                            public void onFailure(Call<Info> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Info>> call, Throwable t) {
                        Toast.makeText(UpdateAccActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        txtBirthdate.setText(date);
    }
}
