package com.example.appjuan803;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import java.util.Locale;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Implementación mínima del reproductor musical.
 * - Permite reproducir sonidos del sistema (notificación, alarma, timbre).
 * - Si existen recursos locales en res/raw con nombres esperados, activa los botones
 *   para reproducirlos; si no, los desactiva (carga opcional).
 * - Permite seleccionar un archivo de audio desde almacenamiento y reproducirlo.
 */
public class ReproductorMusicaActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_AUDIO = 1001;

    private LinearLayout panelAlarmas, panelCancion, panelPropia;
    private Button btnSystem, btnSystem2, btnSystem3;
    private Button btnLocal, btnlocal2;
    private Button btnSelect, btnExternal, btnPause;
    private TextView txtSelectedSong, txtDuration;
    private ProgressBar progressExternal;

    private MediaPlayer mediaPlayerSystem; // para sonidos del sistema
    private MediaPlayer mediaPlayerLocal1, mediaPlayerLocal2; // para canciones en raw (si existen)
    private MediaPlayer mediaPlayerExternal; // para archivo seleccionado

    private Uri selectedUri;
    private final Handler handler = new Handler();
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_reproductor);
        } catch (Exception e) {
            // Fall back: si el layout tiene problemas, informar y cerrar para evitar crashs posteriores
            e.printStackTrace();
            Toast.makeText(this, "Interfaz reproductor no disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Panels
        panelAlarmas = findViewById(R.id.panelAlarmas);
        panelCancion = findViewById(R.id.panelCancion);
        panelPropia = findViewById(R.id.panelPropia);

        // Si cualquier vista esencial no existe, evitar NPE cerrando la activity con mensaje
        if (panelAlarmas == null || panelCancion == null || panelPropia == null) {
            Toast.makeText(this, "Interfaz reproductor incompleta", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tabs (comprobar null antes de asignar listeners)
        View tabAlarmas = findViewById(R.id.tabAlarmas);
        View tabCancion = findViewById(R.id.tabCancion);
        View tabPropia = findViewById(R.id.tabPropia);
        if (tabAlarmas != null) tabAlarmas.setOnClickListener(v -> showPanel(panelAlarmas));
        if (tabCancion != null) tabCancion.setOnClickListener(v -> showPanel(panelCancion));
        if (tabPropia != null) tabPropia.setOnClickListener(v -> showPanel(panelPropia));

        // System buttons
        btnSystem  = findViewById(R.id.btnSystem);
        btnSystem2 = findViewById(R.id.btnSystem2);
        btnSystem3 = findViewById(R.id.btnSystem3);

        if (btnSystem != null) btnSystem.setOnClickListener(v -> playSystemSound(RingtoneManager.TYPE_NOTIFICATION));
        if (btnSystem2 != null) btnSystem2.setOnClickListener(v -> playSystemSound(RingtoneManager.TYPE_ALARM));
        if (btnSystem3 != null) btnSystem3.setOnClickListener(v -> playSystemSound(RingtoneManager.TYPE_RINGTONE));

        // Local song buttons (dependen de recursos en res/raw; carga opcional)
        btnLocal  = findViewById(R.id.btnLocal);
        btnlocal2 = findViewById(R.id.btnlocal2);
        if (btnLocal != null) setupLocalButton(btnLocal, "brayan", 0);
        if (btnlocal2 != null) setupLocalButton(btnlocal2, "miriam", 1);

        // Propia (selección externa)
        btnSelect = findViewById(R.id.btnSelect);
        btnExternal = findViewById(R.id.btnExternal);
        btnPause = findViewById(R.id.btnPause);
        txtSelectedSong = findViewById(R.id.txtSelectedSong);
        progressExternal = findViewById(R.id.progressExternal);
        txtDuration = findViewById(R.id.txtDuration);

        if (btnSelect != null) btnSelect.setOnClickListener(v -> openAudioPicker());

        if (btnExternal != null) btnExternal.setOnClickListener(v -> {
            if (selectedUri != null) {
                playExternal(selectedUri);
            }
        });

        if (btnPause != null) btnPause.setOnClickListener(v -> {
            try {
                if (mediaPlayerExternal != null && mediaPlayerExternal.isPlaying()) {
                    mediaPlayerExternal.pause();
                    btnPause.setText("▶");
                } else if (mediaPlayerExternal != null) {
                    mediaPlayerExternal.start();
                    btnPause.setText("⏸");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error reproducir/pausar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Mostrar panel por defecto
        showPanel(panelAlarmas);
    }

    private void showPanel(View panel) {
        panelAlarmas.setVisibility(panel == panelAlarmas ? View.VISIBLE : View.GONE);
        panelCancion.setVisibility(panel == panelCancion ? View.VISIBLE : View.GONE);
        panelPropia.setVisibility(panel == panelPropia ? View.VISIBLE : View.GONE);
    }

    private void playSystemSound(int type) {
        try {
            Uri uri = RingtoneManager.getDefaultUri(type);
            if (uri == null) return;
            releaseMediaPlayer(mediaPlayerSystem);
            mediaPlayerSystem = new MediaPlayer();
            mediaPlayerSystem.setDataSource(this, uri);
            mediaPlayerSystem.prepare();
            mediaPlayerSystem.start();
            mediaPlayerSystem.setOnCompletionListener(mp -> releaseMediaPlayer(mediaPlayerSystem));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLocalButton(Button btn, String rawName, int index) {
        int resId = getResources().getIdentifier(rawName, "raw", getPackageName());
        if (resId == 0) {
            // recurso no existe -> desactivar boton
            btn.setEnabled(false);
            btn.setAlpha(0.5f);
            btn.setOnClickListener(null);
            return;
        }

        // existe, preparar MediaPlayer
        MediaPlayer mp = MediaPlayer.create(this, resId);
        if (index == 0) mediaPlayerLocal1 = mp; else mediaPlayerLocal2 = mp;

        btn.setOnClickListener(v -> {
            MediaPlayer target = (index == 0) ? mediaPlayerLocal1 : mediaPlayerLocal2;
            if (target == null) return;
            if (target.isPlaying()) {
                target.pause();
                btn.setText("▶");
            } else {
                target.start();
                btn.setText("⏸");
                target.setOnCompletionListener(m -> btn.setText("▶"));
            }
        });
    }

    private void openAudioPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                // Persistir permiso de lectura/escritura si el intent lo otorga
                if ((data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (Exception ignored) {}
                }
                if ((data.getFlags() & Intent.FLAG_GRANT_WRITE_URI_PERMISSION) != 0) {
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    } catch (Exception ignored) {}
                }
                selectedUri = uri;
                String name = queryDisplayName(getContentResolver(), uri);
                txtSelectedSong.setText(name != null ? name : uri.getLastPathSegment());
            }
        }
    }

    private String queryDisplayName(ContentResolver resolver, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return cursor.getString(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private void playExternal(Uri uri) {
        try {
            releaseMediaPlayer(mediaPlayerExternal);
            mediaPlayerExternal = new MediaPlayer();
            mediaPlayerExternal.setDataSource(this, uri);
            mediaPlayerExternal.setOnErrorListener((mp, what, extra) -> {
                // manejar error de reproducción y liberar
                handler.post(() -> Toast.makeText(this, "Error al reproducir el audio", Toast.LENGTH_SHORT).show());
                releaseMediaPlayer(mediaPlayerExternal);
                return true;
            });

            mediaPlayerExternal.setOnPreparedListener(mp -> {
                try {
                    mp.start();
                    if (btnPause != null) btnPause.setText("⏸");

                    // Actualizar progreso si la vista existe
                    if (progressExternal != null) progressExternal.setMax(mp.getDuration());
                    progressRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayerExternal != null && mediaPlayerExternal.isPlaying()) {
                                int pos = mediaPlayerExternal.getCurrentPosition();
                                if (progressExternal != null) progressExternal.setProgress(pos);
                                if (txtDuration != null) txtDuration.setText(formatMillis(mediaPlayerExternal.getDuration()));
                                handler.postDelayed(this, 500);
                            }
                        }
                    };
                    handler.post(progressRunnable);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mediaPlayerExternal.setOnCompletionListener(mp -> {
                if (btnPause != null) btnPause.setText("▶");
                if (progressExternal != null) progressExternal.setProgress(0);
                if (progressRunnable != null) handler.removeCallbacks(progressRunnable);
            });

            // Preparación asíncrona para no bloquear el hilo UI
            mediaPlayerExternal.prepareAsync();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatMillis(int millis) {
        int seconds = millis / 1000;
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format(Locale.getDefault(), "%d:%02d", min, sec);
    }

    private void releaseMediaPlayer(MediaPlayer mp) {
        if (mp != null) {
            try {
                mp.stop();
            } catch (IllegalStateException ignored) {}
            mp.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer(mediaPlayerSystem);
        releaseMediaPlayer(mediaPlayerLocal1);
        releaseMediaPlayer(mediaPlayerLocal2);
        releaseMediaPlayer(mediaPlayerExternal);
        if (progressRunnable != null) handler.removeCallbacks(progressRunnable);
    }
}