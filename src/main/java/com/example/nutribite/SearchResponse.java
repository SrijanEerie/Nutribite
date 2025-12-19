package com.example.nutribite;

import java.util.List;

// This class represents the whole JSON response from the API
public class SearchResponse {
    List<Product> products;
}

// This class represents a single product in the search results
class Product {
    String product_name;
    Nutriments nutriments;
    String image_front_url;
}

// This class represents the nutritional information
class Nutriments {
    // We can add more fields like fats, proteins etc. later
    double carbohydrates_100g;
    double energy_kcal_100g;
    double proteins_100g;
    double fat_100g;
}