package com.example.appjuan803;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.appjuan803.database.AppDatabase;
import com.example.appjuan803.models.Cita;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AdminFragment extends Fragment implements CitaAdapter.OnCitaClickListener {

    private RecyclerView rvCitas;
    private CitaAdapter adapter;
    private AppDatabase db;
    private String horaSeleccionada = "No definida";

    public AdminFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Indica que este fragmento tiene sus propios botones en la barra
    }

    // ESTA FUNCIÓN FALTABA: Carga el archivo de menú superior
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_superior, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        rvCitas = view.findViewById(R.id.rvCitas);
        rvCitas.setLayoutManager(new LinearLayoutManager(getContext()));

        db = Room.databaseBuilder(getContext().getApplicationContext(),
                AppDatabase.class, "AppJuan803_db").build();

        adapter = new CitaAdapter(new ArrayList<>(), this);
        rvCitas.setAdapter(adapter);

        cargarCitas();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_agregar) {
            mostrarDialogoAgregar();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoAgregar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_add_update_cita, null);
        builder.setView(dialogView);

        EditText etNombre = dialogView.findViewById(R.id.et_nombre_cliente);
        EditText etTel = dialogView.findViewById(R.id.et_telefono_cliente);
        EditText etAsunto = dialogView.findViewById(R.id.et_asunto_cita);
        ImageButton btnHora = dialogView.findViewById(R.id.ibtn_hora);
        Spinner spiDias = dialogView.findViewById(R.id.spiDias);

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        ArrayAdapter<String> adapterSpi = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, dias);
        spiDias.setAdapter(adapterSpi);

        btnHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            TimePickerDialog tpd = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                horaSeleccionada = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute);
                Toast.makeText(getContext(), "Hora: " + horaSeleccionada, Toast.LENGTH_SHORT).show();
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
            tpd.show();
        });

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = etNombre.getText().toString();
            String tel = etTel.getText().toString();
            String asunto = etAsunto.getText().toString();
            String dia = spiDias.getSelectedItem().toString();

            if (nombre.isEmpty() || tel.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y Teléfono son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Cita nuevaCita = new Cita();
            nuevaCita.idCita = UUID.randomUUID().toString();
            nuevaCita.nomCliente = nombre;
            nuevaCita.telCliente = tel;
            nuevaCita.asuntoCita = asunto;
            nuevaCita.diaCita = dia;
            nuevaCita.horaCita = horaSeleccionada;

            guardarCitaEnDB(nuevaCita);
        });

        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void guardarCitaEnDB(Cita cita) {
        AsyncTask.execute(() -> {
            db.citaDao().agregarCita(cita);
            cargarCitas();
        });
    }

    private void cargarCitas() {
        AsyncTask.execute(() -> {
            List<Cita> lista = db.citaDao().obtenerCitas();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> adapter.setCitas(lista));
            }
        });
    }

    @Override
    public void onEditClick(Cita cita) {
        Toast.makeText(getContext(), "Editando a: " + cita.nomCliente, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(Cita cita) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar")
                .setMessage("¿Borrar cita de " + cita.nomCliente + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    AsyncTask.execute(() -> {
                        db.citaDao().eliminarCita(cita);
                        cargarCitas();
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}