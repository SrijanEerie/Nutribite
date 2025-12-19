package com.example.nutribite;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FoodApiService {

    // 🔹 Your server’s base URL should be defined in ApiClient.java
    // Example: http://10.0.2.2/nutribite_api/

    @POST("signup.php")
    Call<JsonObject> signupUser(@Body JsonObject userData);

    @POST("login.php")
    Call<JsonObject> loginUser(@Body JsonObject credentials);

    @GET("get_menu.php")
    Call<JsonObject> getMenu();

    @GET("get_profile.php")
    Call<JsonObject> getProfile(@Query("user_id") int userId);

    @POST("save_profile.php")
    Call<JsonObject> saveProfile(@Body JsonObject body);

    // Optional — for external public food search API
    @GET("cgi/search.pl?search_simple=1&json=1")
    Call<SearchResponse> searchPublicFood(@Query("search_terms") String query);
}
