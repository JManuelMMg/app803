package com.example.appjuan803;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    private VideoView videoView;
    private ImageView ring;
    private ImageView wave;
    private TextView subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.i(TAG, "Iniciando SplashActivity");
            setContentView(R.layout.activity_splash);

            videoView = findViewById(R.id.splash_logo);
            ring = findViewById(R.id.splash_ring);
            wave = findViewById(R.id.splash_wave);
            subtitle = findViewById(R.id.splash_subtitle);

            if (videoView == null || ring == null || wave == null || subtitle == null) {
                Log.e(TAG, "Error: Elementos del splash no encontrados");
                goNext();
                return;
            }

            int duration;
            try {
                duration = getResources().getInteger(R.integer.splash_duration_ms);
            } catch (Exception e) {
                duration = 4000; // fallback a 3s para más tiempo de animación
            }

            if (shouldSkipAnimation()) {
                Log.i(TAG, "Saltando animación del splash por configuración del sistema");
                goNext();
                return;
            }

            // Inicia todas las animaciones de forma coordinada
            startFullAnimationSequence(duration);
            playVideoFromRaw();

        } catch (Exception e) {
            Log.e(TAG, "Error al inicializar SplashActivity", e);
            goNext();
        }
    }

    private void playVideoFromRaw() {
        try {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.logo);
            videoView.setVideoURI(uri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        mp.setLooping(false);
                        mp.setVolume(0f, 0f);
                    } catch (Exception ignored) {}
                    videoView.start();
                }
            });

            videoView.setOnCompletionListener(mp -> {
                Log.i(TAG, "Video splash finalizado");
                goNext();
            });

            videoView.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "Error al reproducir video splash: " + what + ", extra=" + extra);
                goNext();
                return true;
            });

        } catch (Exception e) {
            Log.e(TAG, "Error iniciando VideoView", e);
            goNext();
        }
    }

    /**
     * Inicia una secuencia completa de animaciones coordinadas que incluye:
     * - Entrada suave del video
     * - Rotación fluida del anillo
     * - Ondas pulsantes coordinadas
     * - Animación dinámica del subtítulo
     */
    private void startFullAnimationSequence(int duration) {
        try {
            // ===== ANIMACIÓN DEL VIDEO (entrada suave + escala) =====
            animateVideoEntrance();

            // ===== ANIMACIÓN DEL ANILLO ROTATIVO =====
            animateRingRotation(duration);

            // ===== ANIMACIÓN DE ONDAS PULSANTES =====
            animatePulsingWaves(duration);

            // ===== ANIMACIÓN DEL SUBTÍTULO =====
            animateSubtitleWithEffects(duration);

        } catch (Exception e) {
            Log.w(TAG, "Error al iniciar secuencia de animaciones", e);
        }
    }

    /**
     * Anima la entrada del video con fade-in y escala suave
     */
    private void animateVideoEntrance() {
        try {
            // El video entra desde pequeño a su tamaño normal
            ObjectAnimator videoScale = ObjectAnimator.ofFloat(videoView, "scaleX", 0.7f, 1f);
            ObjectAnimator videoScaleY = ObjectAnimator.ofFloat(videoView, "scaleY", 0.7f, 1f);
            ObjectAnimator videoAlpha = ObjectAnimator.ofFloat(videoView, "alpha", 0f, 1f);

            videoScale.setDuration(800);
            videoScaleY.setDuration(800);
            videoAlpha.setDuration(800);

            videoScale.setInterpolator(new DecelerateInterpolator());
            videoScaleY.setInterpolator(new DecelerateInterpolator());

            AnimatorSet videoSet = new AnimatorSet();
            videoSet.playTogether(videoScale, videoScaleY, videoAlpha);
            videoSet.start();
        } catch (Exception e) {
            Log.w(TAG, "Error animando entrada del video", e);
        }
    }

    /**
     * Anima el anillo con rotación suave e infinita
     */
    private void animateRingRotation(int duration) {
        try {
            // Rotación continua más rápida y suave
            ValueAnimator ringRotation = ValueAnimator.ofFloat(0f, 360f);
            ringRotation.setDuration(duration);
            ringRotation.setRepeatCount(ValueAnimator.INFINITE);
            ringRotation.setInterpolator(new LinearInterpolator());
            ringRotation.addUpdateListener(animation -> {
                try {
                    ring.setRotation((float) animation.getAnimatedValue());
                } catch (Exception ignored) {}
            });
            ringRotation.start();

            // Efecto de escala pulsante suave en el anillo
            ObjectAnimator ringScaleX = ObjectAnimator.ofFloat(ring, "scaleX", 0.9f, 1.1f, 0.9f);
            ObjectAnimator ringScaleY = ObjectAnimator.ofFloat(ring, "scaleY", 0.9f, 1.1f, 0.9f);

            ringScaleX.setDuration(duration / 2);
            ringScaleY.setDuration(duration / 2);
            ringScaleX.setRepeatCount(ValueAnimator.INFINITE);
            ringScaleY.setRepeatCount(ValueAnimator.INFINITE);
            ringScaleX.setInterpolator(new AccelerateDecelerateInterpolator());
            ringScaleY.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet ringSet = new AnimatorSet();
            ringSet.playTogether(ringScaleX, ringScaleY);
            ringSet.start();

        } catch (Exception e) {
            Log.w(TAG, "Error animando anillo", e);
        }
    }

    /**
     * Anima la onda con múltiples pulsos coordinados para efecto más rico
     */
    private void animatePulsingWaves(int duration) {
        try {
            // Onda 1: pulso radial suave
            ObjectAnimator wave1ScaleX = ObjectAnimator.ofFloat(wave, "scaleX", 0.8f, 2.0f);
            ObjectAnimator wave1ScaleY = ObjectAnimator.ofFloat(wave, "scaleY", 0.8f, 2.0f);
            ObjectAnimator wave1Alpha = ObjectAnimator.ofFloat(wave, "alpha", 0.8f, 0f);

            wave1ScaleX.setDuration(duration);
            wave1ScaleY.setDuration(duration);
            wave1Alpha.setDuration(duration);
            wave1ScaleX.setRepeatCount(ValueAnimator.INFINITE);
            wave1ScaleY.setRepeatCount(ValueAnimator.INFINITE);
            wave1Alpha.setRepeatCount(ValueAnimator.INFINITE);
            wave1ScaleX.setInterpolator(new LinearInterpolator());
            wave1ScaleY.setInterpolator(new LinearInterpolator());
            wave1Alpha.setInterpolator(new LinearInterpolator());

            AnimatorSet waveSet1 = new AnimatorSet();
            waveSet1.playTogether(wave1ScaleX, wave1ScaleY, wave1Alpha);
            waveSet1.start();

        } catch (Exception e) {
            Log.w(TAG, "Error animando ondas", e);
        }
    }

    /**
     * Anima el subtítulo con efectos de entrada, escala y movimiento dinámico
     */
    private void animateSubtitleWithEffects(int duration) {
        try {
            // Entrada del subtítulo: viene desde abajo con escala pequeña
            ObjectAnimator subAlpha = ObjectAnimator.ofFloat(subtitle, "alpha", 0f, 1f);
            ObjectAnimator subTransY = ObjectAnimator.ofFloat(subtitle, "translationY", 40f, 0f);
            ObjectAnimator subScaleX = ObjectAnimator.ofFloat(subtitle, "scaleX", 0.7f, 1f);
            ObjectAnimator subScaleY = ObjectAnimator.ofFloat(subtitle, "scaleY", 0.7f, 1f);

            subAlpha.setDuration(duration / 2);
            subTransY.setDuration(duration / 2);
            subScaleX.setDuration(duration / 2);
            subScaleY.setDuration(duration / 2);

            subAlpha.setInterpolator(new DecelerateInterpolator());
            subTransY.setInterpolator(new DecelerateInterpolator());
            subScaleX.setInterpolator(new DecelerateInterpolator());
            subScaleY.setInterpolator(new DecelerateInterpolator());

            // Animación de pulso después de la entrada
            ObjectAnimator subPulseX = ObjectAnimator.ofFloat(subtitle, "scaleX", 1f, 1.08f, 1f);
            ObjectAnimator subPulseY = ObjectAnimator.ofFloat(subtitle, "scaleY", 1f, 1.08f, 1f);

            subPulseX.setDuration(duration / 3);
            subPulseY.setDuration(duration / 3);
            subPulseX.setStartDelay(duration / 2);
            subPulseY.setStartDelay(duration / 2);
            subPulseX.setRepeatCount(1);
            subPulseY.setRepeatCount(1);
            subPulseX.setInterpolator(new AccelerateDecelerateInterpolator());
            subPulseY.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet subSetEntrance = new AnimatorSet();
            subSetEntrance.playTogether(subAlpha, subTransY, subScaleX, subScaleY);
            subSetEntrance.start();

            AnimatorSet subSetPulse = new AnimatorSet();
            subSetPulse.playTogether(subPulseX, subPulseY);
            subSetPulse.start();

        } catch (Exception e) {
            Log.w(TAG, "Error animando subtítulo", e);
        }
    }

    private boolean shouldSkipAnimation() {
        try {
            float scale = Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1f);
            return scale == 0f;
        } catch (Exception ignored) { }
        return false;
    }

    private void goNext() {
        try {
            Log.i(TAG, "Navegando de Splash a Login");
            Intent i = new Intent(SplashActivity.this, login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error al navegar a login", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (videoView != null && videoView.isPlaying()) videoView.pause();
        } catch (Exception ignored) {}
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (videoView != null && !videoView.isPlaying()) videoView.start();
        } catch (Exception ignored) {}
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (videoView != null) videoView.stopPlayback();
        } catch (Exception ignored) {}
    }
}
