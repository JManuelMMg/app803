package com.example.appjuan803;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.splash_logo);
        ImageView ring = findViewById(R.id.splash_ring);
        TextView subtitle = findViewById(R.id.splash_subtitle);

        // Obtener duración configurable desde recursos (ms)
        int duration = getResources().getInteger(R.integer.splash_duration_ms);

        boolean skip = shouldSkipAnimation();

        if (skip) {
            // Ir inmediatamente a la siguiente pantalla
            goNext();
            return;
        }

        try {
            // Escala + fade + rotación del logo
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.6f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.6f, 1f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
            ObjectAnimator rotate = ObjectAnimator.ofFloat(logo, "rotation", -8f, 8f, -4f, 4f, 0f);

            // Subtítulo entra desde abajo
            ObjectAnimator subTrans = ObjectAnimator.ofFloat(subtitle, "translationY", 30f, 0f);
            ObjectAnimator subAlpha = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f);

            // Anillo rotatorio continuo (value animator drives rotation)
            ValueAnimator ringAnim = ValueAnimator.ofFloat(0f, 360f);
            ringAnim.setDuration(duration * 2L);
            ringAnim.setRepeatCount(ValueAnimator.INFINITE);
            ringAnim.addUpdateListener(animation -> {
                float v = (float) animation.getAnimatedValue();
                ring.setRotation(v);
            });

            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY, alpha, rotate, subTrans, subAlpha);
            set.setDuration(duration);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // detener rotación del anillo y continuar
                    ringAnim.cancel();
                    goNext();
                }
            });

            // start ring rotation first
            ringAnim.start();

            // Añadir pulso suave al anillo para dar vida (scale + alpha)
            ObjectAnimator ringScaleX = ObjectAnimator.ofFloat(ring, "scaleX", 1f, 1.06f, 1f);
            ObjectAnimator ringScaleY = ObjectAnimator.ofFloat(ring, "scaleY", 1f, 1.06f, 1f);
            ObjectAnimator ringAlpha = ObjectAnimator.ofFloat(ring, "alpha", 0.12f, 0.22f, 0.12f);
            AnimatorSet ringPulse = new AnimatorSet();
            ringPulse.playTogether(ringScaleX, ringScaleY, ringAlpha);
            ringPulse.setDuration(duration);
            ringPulse.setStartDelay(0);
            ringPulse.start();

            // start main set (logo + subtitle)
            set.start();
        } catch (Exception e) {
            Log.w(TAG, "Animación falló, usando fallback estático", e);
            // Fallback: ir directo
            goNext();
        }
    }

    private boolean shouldSkipAnimation() {
        // 1) Preferencia del usuario para omitir splash
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (prefs.getBoolean("skip_splash", false)) return true;

        // 2) Respetar configuración del sistema (reducir/inhabilitar animaciones)
        try {
            float scale = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
            if (scale == 0f) return true;
        } catch (Exception ignored) {
            // si no se puede leer, continuar
        }

        // 3) Si el servicio de accesibilidad sugiere reducir animaciones
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am != null && am.isEnabled()) {
            // No forzamos skipping solo por accesibilidad activada, pero podríamos consultarlo
        }

        return false;
    }

    private void goNext() {
        // Ajuste: siempre llevar al Login primero. El login validará y abrirá menu_inicio.
        Intent i = new Intent(SplashActivity.this, login.class);

        startActivity(i);
        // Transición suave
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
