package com.example.tic_pv.Controlador;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ROLE = "user_role";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        // Inicializamos SharedPreferences
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Guardar rol del usuario
    public void saveUserRole(String role) {
        editor.putString(KEY_USER_ROLE, role);
        editor.apply(); // Aplicar los cambios
    }

    // Recuperar rol del usuario
    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "guest"); // Valor por defecto: "guest"
    }

    // Eliminar el rol de usuario, si es necesario
    public void clearUserRole() {
        editor.remove(KEY_USER_ROLE);
        editor.apply();
    }
}
