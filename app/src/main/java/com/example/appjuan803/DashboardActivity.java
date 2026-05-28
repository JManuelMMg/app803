package com.example.appjuan803;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appjuan803.models.EstadisticasResponse;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity de Dashboard para administradores
 * Muestra estadísticas de reservaciones
 */
public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private TextView tvTotalReservaciones;
    private TextView tvTotalUsuarios;
    private TextView tvReservacionesPorMes;
    private TextView tvEventosPopulares;
    private TextView tvLugaresPopulares;
    private TextView tvOcupacionEventos;
    private TextView tvReservasPorUsuario;
    private ProgressBar progressBar;
    private Button btnVolver;
    private ScrollView scrollView;
    private BarChart barChartMeses;
    private PieChart pieChartEventos;
    private BarChart barChartLugares;
    private BarChart barChartOcupacion;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Log.d(TAG, "onCreate: Inicializando Dashboard");

        // Obtener token del intent
        token = getIntent().getStringExtra("token");

        // Inicializar vistas
        initializeView();

        // Configurar listeners
        setupListeners();

        // Cargar estadísticas
        loadEstadisticas();
    }

    /**
     * Inicializar las vistas del layout
     */
    private void initializeView() {
        tvTotalReservaciones = findViewById(R.id.tvTotalReservaciones);
        tvTotalUsuarios = findViewById(R.id.tvTotalUsuarios);
        tvReservacionesPorMes = findViewById(R.id.tvReservacionesPorMes);
        tvEventosPopulares = findViewById(R.id.tvEventosPopulares);
        tvLugaresPopulares = findViewById(R.id.tvLugaresPopulares);
        tvOcupacionEventos = findViewById(R.id.tvOcupacionEventos);
        tvReservasPorUsuario = findViewById(R.id.tvReservasPorUsuario);
        progressBar = findViewById(R.id.progressBar);
        btnVolver = findViewById(R.id.btnVolver);
        scrollView = findViewById(R.id.scrollView);
        barChartMeses = findViewById(R.id.barChartMeses);
        pieChartEventos = findViewById(R.id.pieChartEventos);
        barChartLugares = findViewById(R.id.barChartLugares);
        barChartOcupacion = findViewById(R.id.barChartOcupacion);

        // Ocultar contenido mientras se carga
        scrollView.setVisibility(android.view.View.GONE);
    }

    /**
     * Configurar listeners de botones
     */
    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());
    }

    /**
     * Cargar estadísticas desde la API
     */
    private void loadEstadisticas() {
        Log.d(TAG, "Cargando estadísticas...");
        progressBar.setVisibility(android.view.View.VISIBLE);

        ApiService apiService = RetrofitClient.getApiService();
        String authorization = "Bearer " + token;

        Call<EstadisticasResponse> call = apiService.obtenerEstadisticas(authorization);

        call.enqueue(new Callback<EstadisticasResponse>() {
            @Override
            public void onResponse(
                Call<EstadisticasResponse> call,
                Response<EstadisticasResponse> response
            ) {
                progressBar.setVisibility(android.view.View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    EstadisticasResponse estadisticas = response.body();
                    mostrarEstadisticas(estadisticas);
                    scrollView.setVisibility(android.view.View.VISIBLE);
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code());
                    Toast.makeText(
                        DashboardActivity.this,
                        "Error al cargar estadísticas",
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<EstadisticasResponse> call, Throwable t) {
                progressBar.setVisibility(android.view.View.GONE);

                Log.e(TAG, "Error en llamada API", t);
                Toast.makeText(
                    DashboardActivity.this,
                    "Error de conexión: " + t.getMessage(),
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    /**
     * Mostrar las estadísticas en el layout
     */
    private void mostrarEstadisticas(EstadisticasResponse estadisticas) {
        // Total de reservaciones
        tvTotalReservaciones.setText(
            "Total de Reservaciones: " + estadisticas.getTotalReservaciones()
        );

        // Total de usuarios
        tvTotalUsuarios.setText(
            "Total de Usuarios: " + estadisticas.getTotalUsuarios()
        );

        // Reservaciones por mes
        StringBuilder mesBuilder = new StringBuilder("📅 Reservaciones por Mes:\n");
        List<Map<String, Object>> resPorMes = estadisticas.getReservacionesPorMes();
        if (resPorMes != null && !resPorMes.isEmpty()) {
            for (Map<String, Object> item : resPorMes) {
                mesBuilder.append("  • ").append(item.get("mes"))
                        .append(": ").append(item.get("cantidad"))
                        .append(" reservaciones\n");
            }
        }
        tvReservacionesPorMes.setText(mesBuilder.toString());

        // Eventos más populares
        StringBuilder eventosBuilder = new StringBuilder("🎉 Eventos Más Populares:\n");
        List<Map<String, Object>> eventos = estadisticas.getEventosMasPopulares();
        if (eventos != null && !eventos.isEmpty()) {
            for (Map<String, Object> item : eventos) {
                eventosBuilder.append("  • ").append(item.get("tipo_evento"))
                        .append(" - ").append(item.get("evento"))
                        .append(": ").append(item.get("cantidad"))
                        .append(" reservaciones\n");
            }
        }
        tvEventosPopulares.setText(eventosBuilder.toString());

        // Lugares más reservados
        StringBuilder lugaresBuilder = new StringBuilder("📍 Lugares Más Reservados:\n");
        List<Map<String, Object>> lugares = estadisticas.getLugaresMasReservados();
        if (lugares != null && !lugares.isEmpty()) {
            for (Map<String, Object> item : lugares) {
                lugaresBuilder.append("  • ").append(item.get("lugar"))
                        .append(": ").append(item.get("cantidad"))
                        .append(" reservaciones\n");
            }
        }
        tvLugaresPopulares.setText(lugaresBuilder.toString());

        StringBuilder ocupacionBuilder = new StringBuilder("Ocupación por evento:\n");
        List<Map<String, Object>> ocupacion = estadisticas.getOcupacionEventos();
        if (ocupacion != null && !ocupacion.isEmpty()) {
            for (Map<String, Object> item : ocupacion) {
                ocupacionBuilder.append("  - ").append(item.get("tipo_evento"))
                        .append(" - ").append(item.get("evento"))
                        .append(": ").append(item.get("porcentaje"))
                        .append("% del total\n");
            }
        }
        tvOcupacionEventos.setText(ocupacionBuilder.toString());

        StringBuilder usuariosBuilder = new StringBuilder("Reservas por evento y usuario:\n");
        List<Map<String, Object>> reservasUsuario = estadisticas.getReservasPorEventoUsuario();
        if (reservasUsuario != null && !reservasUsuario.isEmpty()) {
            for (Map<String, Object> item : reservasUsuario) {
                usuariosBuilder.append("  - ").append(item.get("evento"))
                        .append(" | ").append(item.get("usuario"))
                        .append(" (").append(item.get("correo")).append(")")
                        .append(": ").append(item.get("cantidad"))
                        .append(" lugares\n");
            }
        }
        tvReservasPorUsuario.setText(usuariosBuilder.toString());

        renderBarChart(barChartMeses, resPorMes, "mes", "Reservaciones por mes");
        renderPieChart(pieChartEventos, eventos, "evento");
        renderBarChart(barChartLugares, lugares, "lugar", "Lugares reservados");
        renderBarChart(barChartOcupacion, ocupacion, "evento", "Ocupación");

        Log.d(TAG, "Estadísticas mostradas correctamente");
    }

    private void renderBarChart(
        BarChart chart,
        List<Map<String, Object>> items,
        String labelKey,
        String title
    ) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Map<String, Object> item = items.get(i);
                entries.add(new BarEntry(i, numberValue(item.get("cantidad"))));
                labels.add(String.valueOf(item.get(labelKey)));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, title);
        dataSet.setColors(Arrays.asList(0xFF2196F3, 0xFF4CAF50, 0xFFE45050, 0xFFFF9800, 0xFF7E57C2));
        dataSet.setValueTextSize(11f);

        chart.setData(new BarData(dataSet));
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setLabelRotationAngle(-25f);
        chart.invalidate();
    }

    private void renderPieChart(PieChart chart, List<Map<String, Object>> items, String labelKey) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        if (items != null) {
            for (Map<String, Object> item : items) {
                entries.add(new PieEntry(numberValue(item.get("cantidad")), String.valueOf(item.get(labelKey))));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Eventos");
        dataSet.setColors(Arrays.asList(0xFF2196F3, 0xFF4CAF50, 0xFFE45050, 0xFFFF9800, 0xFF7E57C2));
        dataSet.setValueTextSize(11f);

        chart.setData(new PieData(dataSet));
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(false);
        chart.setEntryLabelTextSize(10f);
        chart.invalidate();
    }

    private float numberValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        try {
            return Float.parseFloat(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0f;
        }
    }
}

