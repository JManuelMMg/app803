package com.example.appjuan803;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.appjuan803.databinding.ActivityMiProyectoBinding;

public class MiProyecto extends AppCompatActivity {
    ActivityMiProyectoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMiProyectoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.ToolBar);
        
        activarMenu();
    }

    private void activarMenu() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmento_principal);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(binding.barraNavegacion,
                    navHostFragment.getNavController());
            
            // IMPORTANTE: Esto debe ir DESPUÉS de setupWithNavController
            // para que los iconos PNG muestren sus colores originales
            binding.barraNavegacion.setItemIconTintList(null);
        }
    }
}