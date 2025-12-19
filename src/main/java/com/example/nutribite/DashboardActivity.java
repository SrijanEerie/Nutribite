package com.example.nutribite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvFullName, tvEmail, tvGoal, tvBmi, tvAllergies, tvToday;
    private RecyclerView rvBreakfast, rvLunch, rvDinner;
    private FoodApiService apiService;
    private List<String> userAllergies = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Views
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvGoal = findViewById(R.id.tvGoal);
        tvBmi = findViewById(R.id.tvBmi);
        tvAllergies = findViewById(R.id.tvAllergies);
        tvToday = findViewById(R.id.tvToday);
        rvBreakfast = findViewById(R.id.rvBreakfast);
        rvLunch = findViewById(R.id.rvLunch);
        rvDinner = findViewById(R.id.rvDinner);

        rvBreakfast.setLayoutManager(new LinearLayoutManager(this));
        rvLunch.setLayoutManager(new LinearLayoutManager(this));
        rvDinner.setLayoutManager(new LinearLayoutManager(this));

        // Set Today's Date
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
        tvToday.setText(sdf.format(new Date()));

        // --- 1. GET AND DISPLAY NAME/EMAIL ---
        Intent intent = getIntent();
        userId = intent.getIntExtra(HealthProfileActivity.EXTRA_USER_ID, -1);
        String name = intent.getStringExtra("user_name");
        String email = intent.getStringExtra("user_email");

        tvFullName.setText((name != null && !name.isEmpty()) ? name : "N/A");
        tvEmail.setText((email != null && !email.isEmpty()) ? email : "N/A");

        // Setup Edit Profile Button
        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> {
            Intent profileIntent = new Intent(DashboardActivity.this, HealthProfileActivity.class);
            profileIntent.putExtra(HealthProfileActivity.EXTRA_USER_ID, userId);
            startActivity(profileIntent);
        });

        apiService = ApiClient.getClient().create(FoodApiService.class);

        if (userId == -1) {
            Toast.makeText(this, "Could not find user. Please log in again.", Toast.LENGTH_LONG).show();
            // Optionally, redirect to login
            // finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This will be called every time the user returns to the dashboard
        fetchUserProfileAndThenMenu();
    }

    private void fetchUserProfileAndThenMenu() {
        apiService.getProfile(String.valueOf(userId)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject profile = response.body();
                    JsonElement dataElement = profile.get("data");

                    if (dataElement != null && !dataElement.isJsonNull() && dataElement.isJsonObject()) {
                        JsonObject data = dataElement.getAsJsonObject();

                        tvGoal.setText(data.has("health_goal") ? data.get("health_goal").getAsString() : "Not set");

                        // --- 2. DISPLAY ALLERGENS ---
                        userAllergies.clear(); // Clear old allergies before adding new ones
                        if (data.has("allergies") && !data.get("allergies").isJsonNull()) {
                            String allergiesStr = data.get("allergies").getAsString();
                            if (allergiesStr != null && !allergiesStr.trim().isEmpty()) {
                                // Standardize to lowercase for reliable matching
                                userAllergies.addAll(Arrays.asList(allergiesStr.toLowerCase().split("\\s*,\\s*")));
                                tvAllergies.setText(allergiesStr);
                            } else {
                                tvAllergies.setText("None");
                            }
                        } else {
                            tvAllergies.setText("None");
                        }

                    } else {
                        // Handle case where user has no profile yet
                        tvAllergies.setText("No profile found. Edit profile to set allergies.");
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
                // --- ALWAYS FETCH MENU AFTER PROFILE ATTEMPT ---
                fetchWeeklyMenu();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Profile Load Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // --- ALWAYS FETCH MENU AFTER PROFILE ATTEMPT ---
                fetchWeeklyMenu();
            }
        });
    }

    private void fetchWeeklyMenu() {
        apiService.getMenu().enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject menuData = response.body();
                    JsonArray weeklyMenu = menuData.getAsJsonArray("weekly_menu");
                    if (weeklyMenu != null && weeklyMenu.size() > 0) {
                        JsonObject todayMenu = weeklyMenu.get(0).getAsJsonObject(); // Assuming today is the first day
                        populateMeal("Breakfast", todayMenu.getAsJsonArray("breakfast"), rvBreakfast);
                        populateMeal("Lunch", todayMenu.getAsJsonArray("lunch"), rvLunch);
                        populateMeal("Dinner", todayMenu.getAsJsonArray("dinner"), rvDinner);
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Menu Load Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateMeal(String mealType, JsonArray mealItems, RecyclerView recyclerView) {
        List<FoodItem> foodItems = new ArrayList<>();
        if (mealItems != null) {
            for (JsonElement itemElement : mealItems) {
                JsonObject itemObject = itemElement.getAsJsonObject();
                String name = itemObject.get("name").getAsString();
                String description = itemObject.get("description").getAsString();
                String tag = itemObject.has("tag") ? itemObject.get("tag").getAsString() : "";
                String itemAllergen = itemObject.has("allergen") ? itemObject.get("allergen").getAsString() : "";

                // --- 3. DYNAMICALLY HIGHLIGHT ALLERGENS ---
                boolean isUserAllergic = false;
                if (itemAllergen != null && !itemAllergen.trim().isEmpty()) {
                    // Check against the user's list of allergies (which is already lowercase)
                    for (String userAllergy : userAllergies) {
                        if (itemAllergen.toLowerCase().contains(userAllergy)) {
                            isUserAllergic = true;
                            break; // Found a match, no need to check further
                        }
                    }
                }
                
                foodItems.add(new FoodItem(name, description, tag, itemAllergen, isUserAllergic));
            }
        }
        FoodAdapter adapter = new FoodAdapter(foodItems);
        recyclerView.setAdapter(adapter);
    }
}