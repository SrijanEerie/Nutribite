package com.example.nutribite;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenFoodFactsApiService {
    // This defines the command for the public food search API
    @GET("cgi/search.pl?search_simple=1&action=process&json=1")
    Call<SearchResponse> searchPublicFood(@Query("search_terms") String query);
}