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
import android.widget.TextView;
import com.example.appjuan803.database.AppDatabase;
import com.example.appjuan803.models.Cita;
import com.example.appjuan803.utils.Utils;
import es.dmoral.toasty.Toasty;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AdminFragment extends Fragment implements CitaAdapter.OnCitaClickListener {

    private RecyclerView rvCitas;
    private CitaAdapter adapter;
    private AppDatabase database;
    private List<Cita> citaList = new ArrayList<>();
    private String horaSeleccionada = "";
    private Cita citaActual;
    private boolean esValido = false;

    public AdminFragment() {}

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Inicializar vista
        rvCitas = view.findViewById(R.id.rvCitas);

        // Obtener la base de datos
        database = Utils.getDatabase(getContext());

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar citas desde la BD
        obtenerCitas();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_superior, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_agregar) {
            lanzarAlertDialogCita();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura el RecyclerView con LinearLayoutManager y adaptador
     */
    private void setupRecyclerView() {
        rvCitas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CitaAdapter(citaList, this);
        rvCitas.setAdapter(adapter);
    }

    /**
     * Obtiene las citas de la BD en background y actualiza la UI
     */
    private void obtenerCitas() {
        new AsyncTask<Void, Void, List<Cita>>() {
            @Override
            protected List<Cita> doInBackground(Void... voids) {
                return database.citaDao().obtenerCitas();
            }

            @Override
            protected void onPostExecute(List<Cita> citas) {
                super.onPostExecute(citas);
                citaList.clear();
                citaList.addAll(citas);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * Lanza el AlertDialog personalizado para agregar/actualizar cita
     */
    private void lanzarAlertDialogCita() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_add_update_cita, null);
        builder.setView(dialogView);

        // Referencias a los controles
        EditText etNombreCliente = dialogView.findViewById(R.id.et_nombre_cliente);
        EditText etTelefonoCliente = dialogView.findViewById(R.id.et_telefono_cliente);
        EditText etAsuntoCita = dialogView.findViewById(R.id.et_asunto_cita);
        EditText etHora = dialogView.findViewById(R.id.et_hora_cita);
        ImageButton ibtnHora = dialogView.findViewById(R.id.ibtn_hora);
        Spinner spiDias = dialogView.findViewById(R.id.spiDias);

        // Configurar Spinner de días
        String[] dias = getResources().getStringArray(R.array.dias_semana);
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, dias);
        spiDias.setAdapter(adapterDias);

        // Configurar click del botón hora
        ibtnHora.setOnClickListener(v -> obtenerHora(etHora));

        // Crear el diálogo
        AlertDialog dialog = builder
                .setPositiveButton("Aceptar", (dialogInterface, which) -> {
                    // Validar campos
                    if (validarCampos(etNombreCliente, etTelefonoCliente, etAsuntoCita, etHora)) {
                        // Crear cita
                        citaActual = new Cita();
                        citaActual.idCita = UUID.randomUUID().toString();
                        citaActual.nomCliente = etNombreCliente.getText().toString().trim();
                        citaActual.telCliente = etTelefonoCliente.getText().toString().trim();
                        citaActual.asuntoCita = etAsuntoCita.getText().toString().trim();
                        citaActual.horaCita = etHora.getText().toString();
                        citaActual.diaCita = (String) spiDias.getSelectedItem();

                        // Agregar a BD
                        agregarCita(citaActual);

                        // Actualizar RecyclerView
                        obtenerCitas();

                        // Cerrar diálogo
                        dialogInterface.dismiss();

                        // Mostrar mensaje de éxito
                        Toasty.success(getContext(), "Cita agregada correctamente",
                                Toasty.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .create();

        dialog.show();
    }

    /**
     * Muestra el TimePickerDialog para seleccionar la hora
     */
    private void obtenerHora(EditText etHora) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog tpd = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute) -> {
                    String hora = String.format("%02d:%02d", hourOfDay, minute);
                    etHora.setText(hora);
                    horaSeleccionada = hora;
                    Toasty.info(getContext(), "Hora: " + hora, Toasty.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        tpd.show();
    }

    /**
     * Valida que los campos obligatorios no estén vacíos
     */
    private boolean validarCampos(EditText etNombre, EditText etTelefono,
                                   EditText etAsunto, EditText etHora) {
        String nombre = etNombre.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String asunto = etAsunto.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toasty.error(getContext(), "El nombre del cliente es requerido",
                    Toasty.LENGTH_SHORT).show();
            return false;
        }

        if (telefono.isEmpty()) {
            Toasty.error(getContext(), "El teléfono es requerido",
                    Toasty.LENGTH_SHORT).show();
            return false;
        }

        if (asunto.isEmpty()) {
            Toasty.error(getContext(), "El asunto es requerido",
                    Toasty.LENGTH_SHORT).show();
            return false;
        }

        if (hora.isEmpty()) {
            Toasty.error(getContext(), "La hora es requerida",
                    Toasty.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Agrega una cita a la BD en background
     */
    private void agregarCita(Cita cita) {
        new AsyncTask<Cita, Void, Void>() {
            @Override
            protected Void doInBackground(Cita... citas) {
                database.citaDao().agregarCita(citas[0]);
                return null;
            }
        }.execute(cita);
    }

    /**
     * Actualiza una cita en la BD en background
     */
    private void actualizarCita(Cita cita) {
        new AsyncTask<Cita, Void, Void>() {
            @Override
            protected Void doInBackground(Cita... citas) {
                database.citaDao().actualizarCita(citas[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                obtenerCitas();
                Toasty.success(getContext(), "Cita actualizada correctamente",
                        Toasty.LENGTH_SHORT).show();
            }
        }.execute(cita);
    }

    /**
     * Elimina una cita de la BD en background
     */
    private void eliminarCita(Cita cita) {
        new AsyncTask<Cita, Void, Void>() {
            @Override
            protected Void doInBackground(Cita... citas) {
                database.citaDao().eliminarCita(citas[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                obtenerCitas();
                Toasty.success(getContext(), "Cita eliminada correctamente",
                        Toasty.LENGTH_SHORT).show();
            }
        }.execute(cita);
    }

    @Override
    public void onEditClick(Cita cita) {
        Toasty.info(getContext(), "Editar: " + cita.nomCliente, Toasty.LENGTH_SHORT).show();
        // TODO: Implementar edición
    }

    @Override
    public void onDeleteClick(Cita cita) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar cita")
                .setMessage("¿Está seguro de que desea eliminar la cita de " + cita.nomCliente + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarCita(cita);
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}