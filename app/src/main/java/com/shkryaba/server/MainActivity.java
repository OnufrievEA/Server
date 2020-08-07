package com.shkryaba.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView mInfoTextView;
    private ProgressBar progressBar;
    private EditText editText;

    private RestAPI restAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        mInfoTextView = findViewById(R.id.tvLoad);
        progressBar = findViewById(R.id.progressBar);

        Button btnLoad = findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener((v) -> onClickLoad());

        Button btnLoadSingleUser = findViewById(R.id.btnLoadSingleUser);
        btnLoadSingleUser.setOnClickListener((v) -> onClickLoadSingleUser());

        Button btnLoadRepositoriesList = findViewById(R.id.btnLoadRepositoriesList);
        btnLoadRepositoriesList.setOnClickListener((v) -> onClickLoadRepositoriesList());
    }


    public void onClickLoad() {
        mInfoTextView.setText("");
        Retrofit retrofit;

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            restAPI = retrofit.create(RestAPI.class);
        } catch (Exception e) {
            mInfoTextView.setText("Exception: " + e.getMessage());
            return;
        }
        getUsersData();
    }

    private void onClickLoadSingleUser() {
        mInfoTextView.setText("");
        Retrofit retrofit;

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            restAPI = retrofit.create(RestAPI.class);
        } catch (Exception e) {
            mInfoTextView.setText("Exception: " + e.getMessage());
            return;
        }
        String userName = editText.getText().toString();
        getSingleUserData(userName);
    }

    private void onClickLoadRepositoriesList() {
        mInfoTextView.setText("");
        Retrofit retrofit;

        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.github.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            restAPI = retrofit.create(RestAPI.class);
        } catch (Exception e) {
            mInfoTextView.setText("Exception: " + e.getMessage());
            return;
        }
        String userName = editText.getText().toString();
        getRepositoryList(userName);
    }


    private void getUsersData() {
        Call<List<UserModel>> callUsers = restAPI.loadUsers();

        if (isInternetAvailable()) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadOneUrl(callUsers);
            } catch (IOException e) {
                Log.e("server", "failed", e);
            }
        }
    }

    private void getSingleUserData(String userName) {
        Call<UserModel> callSingleUser = restAPI.loadUsers(userName);

        if (isInternetAvailable()) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadSingleUser(callSingleUser);
            } catch (IOException e) {
                Log.e("server", "failed", e);
            }
        }
    }

    private void getRepositoryList(String userName) {
        Call<List<RepositoryModel>> callRepositoryList = restAPI.loadRepositoriesList(userName);

        if (isInternetAvailable()) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                downloadRepositoryList(callRepositoryList);
            } catch (IOException e) {
                Log.e("server", "failed", e);
            }
        }
    }


    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Подключите интернет", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void downloadOneUrl(Call<List<UserModel>> call) throws IOException {
        call.enqueue(new Callback<List<UserModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserModel>> call,
                                   @NonNull Response<List<UserModel>> response) {
                if (response.isSuccessful()) {
                    UserModel userModel;
                    for (int i = 0; i < response.body().size(); i++) {
                        userModel = response.body().get(i);
                        mInfoTextView.append("\nLogin " + userModel.getLogin() +
                                "\nId " + userModel.getId() +
                                "\nURL " + userModel.getAvatarUrl() +
                                "\n-------------");
                    }
                } else
                    mInfoTextView.setText("onResponse: " + response.code());

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<UserModel>> call, @NonNull Throwable t) {
                mInfoTextView.setText("onFailure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void downloadSingleUser(Call<UserModel> call) throws IOException {
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call,
                                   @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel userModel;
                    userModel = response.body();
                    mInfoTextView.append("\nLogin " + userModel.getLogin() +
                            "\nId " + userModel.getId() +
                            "\nURL " + userModel.getAvatarUrl() +
                            "\n-------------");
                } else
                    mInfoTextView.setText("onResponse: " + response.code());

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                mInfoTextView.setText("onFailure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void downloadRepositoryList(Call<List<RepositoryModel>> callRepositoryList) throws IOException {
        callRepositoryList.enqueue(new Callback<List<RepositoryModel>>() {
            @Override
            public void onResponse(Call<List<RepositoryModel>> call, Response<List<RepositoryModel>> response) {
                if (response.isSuccessful()) {
                    RepositoryModel repositoryModel;
                    for (int i = 0; i < response.body().size(); i++) {
                        repositoryModel = response.body().get(i);
                        mInfoTextView.append("\nName " + repositoryModel.getName() +
                                "\nURL " + repositoryModel.getHtml_url() +
                                "\n-------------");
                    }
                } else
                    mInfoTextView.setText("onResponse: " + response.code());

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<RepositoryModel>> call, Throwable t) {
                mInfoTextView.setText("onFailure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
