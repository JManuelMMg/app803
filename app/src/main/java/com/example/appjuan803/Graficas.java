package com.example.appjuan803;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad para generar y mostrar gráficas usando MPAndroidChart.
 * Contiene métodos públicos: loadBarChart, loadLineChart, loadPieChart.
 */
public class Graficas extends AppCompatActivity {

    private Button btnBarras, btnLinea, btnPastel;
    private FrameLayout chartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graficas);

        // Referencias a vistas
        btnBarras = findViewById(R.id.btn_barras);
        btnLinea  = findViewById(R.id.btn_linea);
        btnPastel = findViewById(R.id.btn_pastel);
        chartContainer = findViewById(R.id.chartContainer);

        // Iconos (si son GIFs, Glide los animará automáticamente)
        ImageView ivBarras = findViewById(R.id.iv_btn_barras);
        ImageView ivLinea  = findViewById(R.id.iv_btn_linea);
        ImageView ivPastel  = findViewById(R.id.iv_btn_pastel);
        try {
            Glide.with(this).load(R.drawable.gbarras).into(ivBarras);
            Glide.with(this).load(R.drawable.glineal).into(ivLinea);
            Glide.with(this).load(R.drawable.gpastel).into(ivPastel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Listeners: cada botón llama al método público correspondiente
        btnBarras.setOnClickListener(v -> loadBarChart());
        btnLinea.setOnClickListener(v -> loadLineChart());
        btnPastel.setOnClickListener(v -> loadPieChart());

        // Mostrar una gráfica por defecto (opcional)
        loadBarChart();
    }

    /**
     * Limpia el contenedor antes de agregar una nueva gráfica.
     */
    private void clearContainer() {
        if (chartContainer != null) {
            chartContainer.removeAllViews();
        }
    }

    /**
     * Crea y muestra una gráfica de barras con datos de ejemplo.
     */
    public void loadBarChart() {
        try {
            clearContainer();

            BarChart barChart = new BarChart(this);
            barChart.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // Datos de ejemplo
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0f, 120f));
            entries.add(new BarEntry(1f, 90f));
            entries.add(new BarEntry(2f, 60f));
            entries.add(new BarEntry(3f, 30f));
            entries.add(new BarEntry(4f, 150f));

            BarDataSet set = new BarDataSet(entries, "Ventas");
            set.setColors(ColorTemplate.MATERIAL_COLORS);
            set.setValueTextColor(Color.BLACK);

            BarData data = new BarData(set);
            data.setBarWidth(0.9f);

            barChart.setData(data);
            barChart.setFitBars(true);

            // Leyenda y descripción
            Legend l = barChart.getLegend();
            l.setTextColor(Color.DKGRAY);

            Description desc = new Description();
            desc.setText("Gráfica de barras - datos de ejemplo");
            barChart.setDescription(desc);

            // Animación
            barChart.animateY(800);

            // Forzar refresco
            barChart.invalidate();

            // Agregar al contenedor
            chartContainer.addView(barChart);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar gráfica de barras: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Crea y muestra una gráfica de línea con datos de ejemplo.
     */
    public void loadLineChart() {
        try {
            clearContainer();

            LineChart lineChart = new LineChart(this);
            lineChart.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0f, 50f));
            entries.add(new Entry(1f, 70f));
            entries.add(new Entry(2f, 40f));
            entries.add(new Entry(3f, 90f));
            entries.add(new Entry(4f, 60f));

            LineDataSet dataSet = new LineDataSet(entries, "Temperatura");
            dataSet.setColor(ColorTemplate.getHoloBlue());
            dataSet.setCircleColor(ColorTemplate.getHoloBlue());
            dataSet.setLineWidth(2f);
            dataSet.setCircleRadius(4f);
            dataSet.setValueTextColor(Color.BLACK);

            LineData data = new LineData(dataSet);
            lineChart.setData(data);

            Description desc = new Description();
            desc.setText("Gráfica de línea - datos de ejemplo");
            lineChart.setDescription(desc);

            lineChart.getLegend().setTextColor(Color.DKGRAY);
            lineChart.animateX(700);
            lineChart.invalidate();

            chartContainer.addView(lineChart);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar gráfica de línea: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Crea y muestra una gráfica de pastel (pie chart) con datos de ejemplo.
     * Ejercicio solicitado.
     */
    public void loadPieChart() {
        try {
            clearContainer();

            PieChart pieChart = new PieChart(this);
            pieChart.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(40f, "Android"));
            entries.add(new PieEntry(25f, "iOS"));
            entries.add(new PieEntry(20f, "Web"));
            entries.add(new PieEntry(15f, "Otros"));

            PieDataSet dataSet = new PieDataSet(entries, "Distribución de plataformas");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSet.setSliceSpace(3f);
            dataSet.setValueTextColor(Color.WHITE);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);

            pieChart.setUsePercentValues(true);
            Description desc = new Description();
            desc.setText("Gráfica de pastel - ejercicio");
            pieChart.setDescription(desc);

            Legend l = pieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setTextColor(Color.DKGRAY);

            pieChart.animateY(900);
            pieChart.invalidate();

            chartContainer.addView(pieChart);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar gráfica de pastel: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}

