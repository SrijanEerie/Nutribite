package com.example.nutribite;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HealthProfileActivity extends AppCompatActivity {

    public static final String EXTRA_USER_ID = "extra_user_id";

    private EditText etWeight, etHeight, etDislikes, etMedical, etWater, etSleep;
    private Spinner spinnerGender, spinnerDietary, spinnerActivity, spinnerGoal;
    private Button btnSaveProfile, btnSkipProfile;
    private CheckBox cbPeanuts, cbTreeNuts, cbDairy, cbEggs, cbWheat, cbSoy, cbFish, cbShellfish;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_profile);

        // Initialize Views
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        spinnerGender = findViewById(R.id.spinnerGender);
        etDislikes = findViewById(R.id.etDislikes);
        spinnerDietary = findViewById(R.id.spinnerDietary);
        spinnerActivity = findViewById(R.id.spinnerActivity);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        etWater = findViewById(R.id.etWater);
        etSleep = findViewById(R.id.etSleep);
        etMedical = findViewById(R.id.etMedical);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSkipProfile = findViewById(R.id.btnSkipProfile);

        // Initialize Checkboxes
        cbPeanuts = findViewById(R.id.cbPeanuts);
        cbTreeNuts = findViewById(R.id.cbTreeNuts);
        cbDairy = findViewById(R.id.cbDairy);
        cbEggs = findViewById(R.id.cbEggs);
        cbWheat = findViewById(R.id.cbWheat);
        cbSoy = findViewById(R.id.cbSoy);
        cbFish = findViewById(R.id.cbFish);
        cbShellfish = findViewById(R.id.cbShellfish);

        userId = getIntent().getIntExtra(EXTRA_USER_ID, 0);

        if (userId == 0) {
            Toast.makeText(this, "Invalid User ID. Cannot load profile.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        setupSpinners();
        loadUserProfile();

        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
        btnSkipProfile.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Gender
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Dietary Preference
        ArrayAdapter<CharSequence> dietaryAdapter = ArrayAdapter.createFromResource(this,
                R.array.dietary_preference_array, android.R.layout.simple_spinner_item);
        dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDietary.setAdapter(dietaryAdapter);

        // Activity Level
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_level_array, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivity.setAdapter(activityAdapter);

        // Health Goal
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.health_goal_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);
    }

    private void loadUserProfile() {
        FoodApiService apiService = ApiClient.getClient().create(FoodApiService.class);
        Call<JsonObject> call = apiService.getProfile(userId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    if (body.has("status") && body.get("status").getAsString().equals("success")) {
                        JsonObject profile = body.getAsJsonObject("profile");
                        if (profile != null) {
                            etWeight.setText(getJsonString(profile, "weight_kg"));
                            etHeight.setText(getJsonString(profile, "height_cm"));
                            setSpinnerSelection(spinnerGender, getJsonString(profile, "gender"));
                            updateAllergyCheckboxes(getJsonString(profile, "allergies"));
                            etDislikes.setText(getJsonString(profile, "dislikes"));
                            setSpinnerSelection(spinnerDietary, getJsonString(profile, "dietary_pref"));
                            setSpinnerSelection(spinnerActivity, getJsonString(profile, "activity_level"));
                            setSpinnerSelection(spinnerGoal, getJsonString(profile, "health_goal"));
                            etWater.setText(getJsonString(profile, "water_goal_liters"));
                            etSleep.setText(getJsonString(profile, "sleep_hours"));
                            etMedical.setText(getJsonString(profile, "medical_conditions"));
                        }
                    } else {
                        Toast.makeText(HealthProfileActivity.this, "Profile is empty. Please fill it out.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                     Toast.makeText(HealthProfileActivity.this, "Failed to load profile. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(HealthProfileActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserProfile() {
        JsonObject profileData = new JsonObject();
        profileData.addProperty("user_id", userId);
        profileData.addProperty("weight_kg", etWeight.getText().toString());
        profileData.addProperty("height_cm", etHeight.getText().toString());
        profileData.addProperty("gender", spinnerGender.getSelectedItem().toString());
        profileData.addProperty("allergies", getAllergiesString());
        profileData.addProperty("dislikes", etDislikes.getText().toString());
        profileData.addProperty("dietary_pref", spinnerDietary.getSelectedItem().toString());
        profileData.addProperty("activity_level", spinnerActivity.getSelectedItem().toString());
        profileData.addProperty("health_goal", spinnerGoal.getSelectedItem().toString());
        profileData.addProperty("water_goal_liters", etWater.getText().toString());
        profileData.addProperty("sleep_hours", etSleep.getText().toString());
        profileData.addProperty("medical_conditions", etMedical.getText().toString());

        FoodApiService apiService = ApiClient.getClient().create(FoodApiService.class);
        Call<JsonObject> call = apiService.saveProfile(profileData);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().get("status").getAsString().equals("success")) {
                        Toast.makeText(HealthProfileActivity.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to the dashboard
                    } else {
                        String message = response.body().has("message") ? response.body().get("message").getAsString() : "Unknown error";
                        Toast.makeText(HealthProfileActivity.this, "Failed to save profile: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(HealthProfileActivity.this, "Failed to save profile: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        Toast.makeText(HealthProfileActivity.this, "Failed to save profile. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(HealthProfileActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateAllergyCheckboxes(String allergies) {
        if (allergies == null || allergies.isEmpty()) return;
        List<String> allergyList = Arrays.asList(allergies.split("\\s*,\\s*"));
        
        cbPeanuts.setChecked(listContainsIgnoreCase(allergyList, "Peanuts"));
        cbTreeNuts.setChecked(listContainsIgnoreCase(allergyList, "Tree Nuts"));
        cbDairy.setChecked(listContainsIgnoreCase(allergyList, "Dairy"));
        cbEggs.setChecked(listContainsIgnoreCase(allergyList, "Eggs"));
        cbWheat.setChecked(listContainsIgnoreCase(allergyList, "Wheat"));
        cbSoy.setChecked(listContainsIgnoreCase(allergyList, "Soy"));
        cbFish.setChecked(listContainsIgnoreCase(allergyList, "Fish"));
        cbShellfish.setChecked(listContainsIgnoreCase(allergyList, "Shellfish"));
    }

    private String getAllergiesString() {
        StringJoiner joiner = new StringJoiner(", ");
        if (cbPeanuts.isChecked()) joiner.add("Peanuts");
        if (cbTreeNuts.isChecked()) joiner.add("Tree Nuts");
        if (cbDairy.isChecked()) joiner.add("Dairy");
        if (cbEggs.isChecked()) joiner.add("Eggs");
        if (cbWheat.isChecked()) joiner.add("Wheat");
        if (cbSoy.isChecked()) joiner.add("Soy");
        if (cbFish.isChecked()) joiner.add("Fish");
        if (cbShellfish.isChecked()) joiner.add("Shellfish");
        return joiner.toString();
    }

    private String getJsonString(JsonObject jsonObject, String memberName) {
        if (jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()) {
            return jsonObject.get(memberName).getAsString();
        }
        return "";
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value != null && !value.isEmpty()) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }
    
    private boolean listContainsIgnoreCase(List<String> list, String value) {
        for (String item : list) {
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
