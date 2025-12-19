package com.example.nutribite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class LoginDialogFragment extends DialogFragment {

    public interface Callbacks {
        void onLoginAttempt(String email, String password, String role);
        void onShowSignUp();
    }

    private Callbacks callbacks;
    private String role;

    public static LoginDialogFragment newInstance(String role) {
        LoginDialogFragment fragment = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putString("ROLE", role);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString("ROLE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        TextView tvSignUp = view.findViewById(R.id.tvSignUp);
        TextView tvTitle = view.findViewById(R.id.tvTitle);

        // Defensive check for role to prevent crash
        if (role == null) {
            role = "user"; // Default to "user" if role is not provided
        }

        tvTitle.setText("Login as " + Character.toUpperCase(role.charAt(0)) + role.substring(1));

        if ("admin".equalsIgnoreCase(role)) {
            tvSignUp.setVisibility(View.GONE);
        } else {
            tvSignUp.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        tvSignUp.setOnClickListener(v -> {
            if (callbacks != null) {
                callbacks.onShowSignUp();
            }
            dismiss();
        });
        view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (callbacks != null) {
                callbacks.onLoginAttempt(email, password, role);
            }
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) {
            callbacks = (Callbacks) context;
        }
    }
}
