package com.example.nutribite;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        FoodApiService apiService = ApiClient.getClient().create(FoodApiService.class);

        JsonObject loginData = new JsonObject();
        loginData.addProperty("email", email);
        loginData.addProperty("password", password);
        loginData.addProperty("role", "user");

        Call<JsonObject> call = apiService.loginUser(loginData);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    if (responseBody.get("status").getAsString().equals("success")) {
                        JsonObject user = responseBody.getAsJsonObject("user");
                        int userId = user.get("id").getAsInt();
                        String name = user.get("name").getAsString();
                        String email = user.get("email").getAsString();

                        Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.putExtra(HealthProfileActivity.EXTRA_USER_ID, userId);
                        intent.putExtra("user_name", name);
                        intent.putExtra("user_email", email);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = responseBody.has("message") ? responseBody.get("message").getAsString() : "Invalid credentials";
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
