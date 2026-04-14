package com.example.appjuan803;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class menu_inicio extends AppCompatActivity {

    // Declaración de variables para los botones
    private CardView btnSaldo, btnGastos, btnPerfil, btnReportar, btnBuscar, btnExit;
    private GestorDatos gestorDatos;
    private List<Gasto> listaGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_inicio);

        // Inicializar gestor de datos y lista de gastos
        gestorDatos = new GestorDatos(this);
        listaGastos = new ArrayList<>();
        inicializarDatosEjemplo();

        // 🔹 1. Referenciar los botones del XML
        inicializarVistas();

        // 🔹 2. Configurar los eventos click
        configurarClicks();
    }

    /**
     * Método para inicializar las vistas (findViewById)
     */
    private void inicializarVistas() {
        btnSaldo = findViewById(R.id.btn_saldo);
        btnGastos = findViewById(R.id.btn_gastos);
        btnPerfil = findViewById(R.id.btn_perfil);
        btnReportar = findViewById(R.id.btn_reportar);
        btnBuscar = findViewById(R.id.btn_buscar);
        btnExit = findViewById(R.id.btn_exit);
    }

    /**
     * Método para configurar los listeners de cada botón
     */
    private void configurarClicks() {

        // 💰 Botón: Saldo - Mostrar y gestionar saldo actual
        btnSaldo.setOnClickListener(v -> mostrarDialogoSaldo());

        // 💸 Botón: Gastos - Gestionar lista de gastos
        btnGastos.setOnClickListener(v -> mostrarDialogoGastos());

        // 👤 Botón: Perfil - Ver y editar información de perfil
        btnPerfil.setOnClickListener(v -> mostrarDialogoPerfil());

        // 📢 Botón: Reportar - Generar reporte de gastos y saldo
        btnReportar.setOnClickListener(v -> generarReporte());

        // 🔍 Botón: Buscar - Buscar gastos por descripción o categoría
        btnBuscar.setOnClickListener(v -> mostrarDialogoBuscar());

        // 🚪 Botón: Exit (✅ PROGRAMADO - Con confirmación)
        btnExit.setOnClickListener(v -> mostrarConfirmacionSalida());
    }

    /**
     * ✅ Método para mostrar diálogo de confirmación al salir
     */
    private void mostrarConfirmacionSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Salir de la aplicación")
                .setMessage("¿Estás seguro de que deseas cerrar la aplicación?")
                .setIcon(R.drawable.ic_exit)
                .setCancelable(false)
                .setPositiveButton("Sí, salir", (dialog, which) -> {
                    // Cerrar aplicación completamente
                    finishAffinity();  // Cierra todas las actividades de la pila
                    System.exit(0);    // Finaliza el proceso
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();  // Solo cierra el diálogo
                })
                .show();
    }

    /**
     * 💰 Mostrar diálogo para gestionar el saldo
     */
    private void mostrarDialogoSaldo() {
        double saldoActual = gestorDatos.obtenerSaldo();

        new AlertDialog.Builder(this)
                .setTitle("💰 Gestión de Saldo")
                .setMessage("Saldo actual: $" + String.format("%.2f", saldoActual) + "\n\n¿Desea agregar o retirar dinero?")
                .setPositiveButton("Agregar", (dialog, which) -> mostrarDialogoAgregarSaldo())
                .setNegativeButton("Retirar", (dialog, which) -> mostrarDialogoRetirarSaldo())
                .setNeutralButton("Ver Historial", (dialog, which) -> mostrarHistorialSaldo())
                .show();
    }

    /**
     * Agregar dinero al saldo
     */
    private void mostrarDialogoAgregarSaldo() {
        EditText input = new EditText(this);
        input.setHint("Monto a agregar");

        new AlertDialog.Builder(this)
                .setTitle("💰 Agregar Saldo")
                .setView(input)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    try {
                        double monto = Double.parseDouble(input.getText().toString());
                        gestorDatos.agregarSaldo(monto);
                        Toast.makeText(this, "Saldo agregado: $" + String.format("%.2f", monto), Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Retirar dinero del saldo
     */
    private void mostrarDialogoRetirarSaldo() {
        EditText input = new EditText(this);
        input.setHint("Monto a retirar");

        new AlertDialog.Builder(this)
                .setTitle("💰 Retirar Saldo")
                .setView(input)
                .setPositiveButton("Retirar", (dialog, which) -> {
                    try {
                        double monto = Double.parseDouble(input.getText().toString());
                        if (gestorDatos.restarSaldo(monto)) {
                            Toast.makeText(this, "Saldo retirado: $" + String.format("%.2f", monto), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Saldo insuficiente", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Mostrar historial de saldo (simulado)
     */
    private void mostrarHistorialSaldo() {
        String historial = "📊 Historial de Saldo:\n\n" +
                          "Saldo inicial: $1000.00\n" +
                          "Última actualización: " + new java.util.Date().toString() + "\n\n" +
                          "Saldo actual: $" + String.format("%.2f", gestorDatos.obtenerSaldo());

        new AlertDialog.Builder(this)
                .setTitle("📊 Historial de Saldo")
                .setMessage(historial)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * 💸 Mostrar diálogo para gestionar gastos
     */
    private void mostrarDialogoGastos() {
        String[] opciones = {"Ver Gastos", "Agregar Gasto", "Editar Gasto", "Eliminar Gasto"};

        new AlertDialog.Builder(this)
                .setTitle("💸 Gestión de Gastos")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: mostrarListaGastos(); break;
                        case 1: mostrarDialogoAgregarGasto(); break;
                        case 2: mostrarDialogoEditarGasto(); break;
                        case 3: mostrarDialogoEliminarGasto(); break;
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Mostrar lista de gastos
     */
    private void mostrarListaGastos() {
        if (listaGastos.isEmpty()) {
            Toast.makeText(this, "No hay gastos registrados", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder lista = new StringBuilder("📋 Lista de Gastos:\n\n");
        double total = 0;

        for (Gasto gasto : listaGastos) {
            lista.append("• ").append(gasto.toString()).append("\n");
            total += gasto.getMonto();
        }

        lista.append("\n💰 Total gastado: $").append(String.format("%.2f", total));

        new AlertDialog.Builder(this)
                .setTitle("📋 Lista de Gastos")
                .setMessage(lista.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Agregar nuevo gasto
     */
    private void mostrarDialogoAgregarGasto() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText etDescripcion = new EditText(this);
        etDescripcion.setHint("Descripción del gasto");

        EditText etMonto = new EditText(this);
        etMonto.setHint("Monto");
        etMonto.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText etCategoria = new EditText(this);
        etCategoria.setHint("Categoría (ej: Comida, Transporte)");

        layout.addView(etDescripcion);
        layout.addView(etMonto);
        layout.addView(etCategoria);

        new AlertDialog.Builder(this)
                .setTitle("➕ Agregar Gasto")
                .setView(layout)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    try {
                        String descripcion = etDescripcion.getText().toString().trim();
                        double monto = Double.parseDouble(etMonto.getText().toString());
                        String categoria = etCategoria.getText().toString().trim();

                        if (descripcion.isEmpty() || categoria.isEmpty()) {
                            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Gasto nuevoGasto = new Gasto(descripcion, monto, categoria,
                            new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
                        listaGastos.add(nuevoGasto);

                        Toast.makeText(this, "Gasto agregado correctamente", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Editar gasto existente (simplificado)
     */
    private void mostrarDialogoEditarGasto() {
        if (listaGastos.isEmpty()) {
            Toast.makeText(this, "No hay gastos para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Función de edición disponible próximamente", Toast.LENGTH_SHORT).show();
    }

    /**
     * Eliminar gasto (simplificado)
     */
    private void mostrarDialogoEliminarGasto() {
        if (listaGastos.isEmpty()) {
            Toast.makeText(this, "No hay gastos para eliminar", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Función de eliminación disponible próximamente", Toast.LENGTH_SHORT).show();
    }

    /**
     * 👤 Mostrar diálogo de perfil
     */
    private void mostrarDialogoPerfil() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        TextView tvInfo = new TextView(this);
        tvInfo.setText("👤 Información de Perfil\n\n" +
                      "Nombre: " + gestorDatos.obtenerNombreUsuario() + "\n" +
                      "Email: " + gestorDatos.obtenerEmailUsuario());
        tvInfo.setPadding(0, 0, 0, 30);

        EditText etNombre = new EditText(this);
        etNombre.setHint("Nuevo nombre");
        etNombre.setText(gestorDatos.obtenerNombreUsuario());

        EditText etEmail = new EditText(this);
        etEmail.setHint("Nuevo email");
        etEmail.setText(gestorDatos.obtenerEmailUsuario());

        layout.addView(tvInfo);
        layout.addView(etNombre);
        layout.addView(etEmail);

        new AlertDialog.Builder(this)
                .setTitle("👤 Perfil de Usuario")
                .setView(layout)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (!nombre.isEmpty()) gestorDatos.guardarNombreUsuario(nombre);
                    if (!email.isEmpty()) gestorDatos.guardarEmailUsuario(email);

                    Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * 📢 Generar reporte de gastos y saldo
     */
    private void generarReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("📊 REPORTE FINANCIERO\n\n");

        // Información del usuario
        reporte.append("👤 Usuario: ").append(gestorDatos.obtenerNombreUsuario()).append("\n");
        reporte.append("📧 Email: ").append(gestorDatos.obtenerEmailUsuario()).append("\n\n");

        // Saldo actual
        double saldo = gestorDatos.obtenerSaldo();
        reporte.append("💰 Saldo Actual: $").append(String.format("%.2f", saldo)).append("\n\n");

        // Gastos
        reporte.append("💸 GASTOS REGISTRADOS:\n");
        if (listaGastos.isEmpty()) {
            reporte.append("No hay gastos registrados\n");
        } else {
            double totalGastado = 0;
            for (int i = 0; i < listaGastos.size(); i++) {
                Gasto gasto = listaGastos.get(i);
                reporte.append(String.format("%d. %s - $%.2f (%s) - %s\n",
                    i + 1, gasto.getDescripcion(), gasto.getMonto(),
                    gasto.getCategoria(), gasto.getFecha()));
                totalGastado += gasto.getMonto();
            }
            reporte.append("\n💰 Total Gastado: $").append(String.format("%.2f", totalGastado));
            reporte.append("\n💰 Saldo Restante: $").append(String.format("%.2f", saldo - totalGastado));
        }

        reporte.append("\n\n📅 Fecha del Reporte: ").append(new java.util.Date().toString());

        new AlertDialog.Builder(this)
                .setTitle("📊 Reporte Financiero")
                .setMessage(reporte.toString())
                .setPositiveButton("OK", null)
                .setNeutralButton("Copiar", (dialog, which) -> {
                    // Copiar al portapapeles (simulado)
                    Toast.makeText(this, "Reporte copiado al portapapeles", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    /**
     * 🔍 Mostrar diálogo de búsqueda
     */
    private void mostrarDialogoBuscar() {
        EditText input = new EditText(this);
        input.setHint("Buscar por descripción o categoría");

        new AlertDialog.Builder(this)
                .setTitle("🔍 Buscar Gastos")
                .setView(input)
                .setPositiveButton("Buscar", (dialog, which) -> {
                    String termino = input.getText().toString().toLowerCase().trim();
                    if (termino.isEmpty()) {
                        Toast.makeText(this, "Ingrese un término de búsqueda", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<Gasto> resultados = new ArrayList<>();
                    for (Gasto gasto : listaGastos) {
                        if (gasto.getDescripcion().toLowerCase().contains(termino) ||
                            gasto.getCategoria().toLowerCase().contains(termino)) {
                            resultados.add(gasto);
                        }
                    }

                    if (resultados.isEmpty()) {
                        Toast.makeText(this, "No se encontraron resultados", Toast.LENGTH_SHORT).show();
                    } else {
                        StringBuilder resultado = new StringBuilder("🔍 Resultados de búsqueda:\n\n");
                        for (Gasto gasto : resultados) {
                            resultado.append("• ").append(gasto.toString()).append("\n");
                        }

                        new AlertDialog.Builder(this)
                                .setTitle("🔍 Resultados")
                                .setMessage(resultado.toString())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Inicializar datos de ejemplo para demostración
     */
    private void inicializarDatosEjemplo() {
        listaGastos.add(new Gasto("Compra supermercado", 150.50, "Alimentación", "15/04/2024"));
        listaGastos.add(new Gasto("Gasolina", 80.00, "Transporte", "14/04/2024"));
        listaGastos.add(new Gasto("Cena restaurante", 120.75, "Entretenimiento", "13/04/2024"));
        listaGastos.add(new Gasto("Medicamentos", 45.25, "Salud", "12/04/2024"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpieza opcional de referencias
        btnSaldo = null;
        btnGastos = null;
        btnPerfil = null;
        btnReportar = null;
        btnBuscar = null;
        btnExit = null;
    }
}