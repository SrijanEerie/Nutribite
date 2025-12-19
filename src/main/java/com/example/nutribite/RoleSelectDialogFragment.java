package com.example.nutribite;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class RoleSelectDialogFragment extends DialogFragment {

    public interface Callbacks { void onRoleSelected(String role); }
    private Callbacks callbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_role_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btnClose).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btnAdmin).setOnClickListener(v -> {
            if (callbacks != null) callbacks.onRoleSelected("admin");
            dismiss();
        });
        view.findViewById(R.id.btnUser).setOnClickListener(v -> {
            if (callbacks != null) callbacks.onRoleSelected("user");
            dismiss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Callbacks) callbacks = (Callbacks) context;
    }
}