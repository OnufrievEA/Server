package com.shkryaba.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestAPI {
    @GET("users")
    Call<List<UserModel>> loadUsers();

    @GET("users/{user}")
    Call<UserModel> loadUsers(@Path("user") String user);

    @GET("users/{user}/repos")
    Call<List<RepositoryModel>> loadRepositoriesList(@Path("user") String user);
}
