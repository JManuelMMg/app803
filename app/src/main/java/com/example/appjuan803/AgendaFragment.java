package com.example.appjuan803;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.appjuan803.database.AppDatabase;
import com.example.appjuan803.models.Cita;
import com.example.appjuan803.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class AgendaFragment extends Fragment {

    // Listas de citas para cada día
    private ArrayList<Cita> citasLunes = new ArrayList<>();
    private ArrayList<Cita> citasMartes = new ArrayList<>();
    private ArrayList<Cita> citasMiercoles = new ArrayList<>();
    private ArrayList<Cita> citasJueves = new ArrayList<>();
    private ArrayList<Cita> citasViernes = new ArrayList<>();
    private ArrayList<Cita> citasSabado = new ArrayList<>();
    private ArrayList<Cita> citasDomingo = new ArrayList<>();
    
    // Base de datos
    private AppDatabase database;
    
    // RecyclerViews
    private RecyclerView rvCitasLunes, rvCitasMartes, rvCitasMiercoles, rvCitasJueves,
                         rvCitasViernes, rvCitasSabado, rvCitasDomingo;

    public AgendaFragment() {
        // Required empty public constructor
    }

    public static AgendaFragment newInstance() {
        AgendaFragment fragment = new AgendaFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        // Inicializar referencias a RecyclerViews
        rvCitasLunes = view.findViewById(R.id.rvCitasLunes);
        rvCitasMartes = view.findViewById(R.id.rvCitasMartes);
        rvCitasMiercoles = view.findViewById(R.id.rvCitasMiercoles);
        rvCitasJueves = view.findViewById(R.id.rvCitasJueves);
        rvCitasViernes = view.findViewById(R.id.rvCitasViernes);
        rvCitasSabado = view.findViewById(R.id.rvCitasSabado);
        rvCitasDomingo = view.findViewById(R.id.rvCitasDomingo);

        // Obtener instancia de BD
        database = Utils.getDatabase(getContext());

        // Obtener todas las citas
        obtenerTodasCitas();

        return view;
    }

    /**
     * Obtiene todas las citas de la BD en hilo secundario
     */
    private void obtenerTodasCitas() {
        AsyncTask.execute(() -> {
            List<Cita> listaCitas = database.citaDao().obtenerCitas();
            
            // Ejecutar en hilo principal
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> obtenerCitasDia(listaCitas));
            }
        });
    }

    /**
     * Organiza las citas por día de la semana
     */
    private void obtenerCitasDia(List<Cita> listaCitas) {
        // Limpiar listas
        citasLunes.clear();
        citasMartes.clear();
        citasMiercoles.clear();
        citasJueves.clear();
        citasViernes.clear();
        citasSabado.clear();
        citasDomingo.clear();

        // Distribuir citas por día
        for (Cita cita : listaCitas) {
            switch (cita.diaCita) {
                case "Lunes":
                    citasLunes.add(cita);
                    break;
                case "Martes":
                    citasMartes.add(cita);
                    break;
                case "Miércoles":
                    citasMiercoles.add(cita);
                    break;
                case "Jueves":
                    citasJueves.add(cita);
                    break;
                case "Viernes":
                    citasViernes.add(cita);
                    break;
                case "Sábado":
                    citasSabado.add(cita);
                    break;
                case "Domingo":
                    citasDomingo.add(cita);
                    break;
            }
        }

        // Configurar RecyclerViews
        setupRecyclerView(rvCitasLunes, citasLunes);
        setupRecyclerView(rvCitasMartes, citasMartes);
        setupRecyclerView(rvCitasMiercoles, citasMiercoles);
        setupRecyclerView(rvCitasJueves, citasJueves);
        setupRecyclerView(rvCitasViernes, citasViernes);
        setupRecyclerView(rvCitasSabado, citasSabado);
        setupRecyclerView(rvCitasDomingo, citasDomingo);
    }

    /**
     * Configura un RecyclerView con su adaptador
     */
    private void setupRecyclerView(RecyclerView rv, List<Cita> lista) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        CitasDiaAdapter adapter = new CitasDiaAdapter(lista);
        rv.setAdapter(adapter);
    }
}


