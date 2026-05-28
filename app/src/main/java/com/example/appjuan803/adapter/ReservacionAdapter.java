package com.example.appjuan803.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appjuan803.R;
import com.example.appjuan803.CrearReservacionActivity;
import com.example.appjuan803.models.Reservacion;
import com.example.appjuan803.network.ApiService;
import com.example.appjuan803.network.RetrofitClient;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adaptador para mostrar reservaciones en RecyclerView
 * Utiliza CardView para cada reservación
 * Permite editar y eliminar (según permisos)
 */
public class ReservacionAdapter extends RecyclerView.Adapter<ReservacionAdapter.ViewHolder> {

    private static final String TAG = "ReservacionAdapter";

    private List<Reservacion> reservaciones;
    private Context context;
    private String userRol;
    private String token;

    public ReservacionAdapter(List<Reservacion> reservaciones, Context context, String userRol) {
        this.reservaciones = reservaciones;
        this.context = context;
        this.userRol = userRol;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reservacion reservacion = reservaciones.get(position);
        holder.bind(reservacion);
    }

    @Override
    public int getItemCount() {
        return reservaciones != null ? reservaciones.size() : 0;
    }

    /**
     * Actualizar lista de reservaciones
     */
    public void setReservaciones(List<Reservacion> reservaciones) {
        this.reservaciones = reservaciones;
        notifyDataSetChanged();
    }

    /**
     * Establecer token de autenticación
     */
    public void setToken(String token) {
        this.token = token;
    }

    // ============= VIEW HOLDER =============

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardView;
        private TextView tvEvento;
        private TextView tvTipoEvento;
        private TextView tvFecha;
        private TextView tvLugar;
        private TextView tvCantidad;
        private TextView tvUsuario;
        private TextView tvDescripcion;
        private Button btnEditar;
        private Button btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            tvEvento = itemView.findViewById(R.id.tvEvento);
            tvTipoEvento = itemView.findViewById(R.id.tvTipoEvento);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }

        public void bind(final Reservacion reservacion) {
            // Enlazar datos
            tvEvento.setText(reservacion.getEvento());
            String tipoEvento = reservacion.getTipoEvento();
            tvTipoEvento.setText(tipoEvento == null || tipoEvento.isEmpty() ? "Evento general" : tipoEvento);
            tvFecha.setText("Fecha: " + reservacion.getFecha());
            tvLugar.setText("Lugar: " + reservacion.getLugar());
            tvCantidad.setText("Lugares reservados: " + reservacion.getCantidad());

            if ("admin".equals(userRol)) {
                String nombre = reservacion.getUsuarioNombre();
                String correo = reservacion.getUsuarioCorreo();
                String cliente = nombre == null || nombre.isEmpty() ? "Cliente sin nombre" : nombre;
                if (correo != null && !correo.isEmpty()) {
                    cliente += " (" + correo + ")";
                }
                tvUsuario.setText("Cliente: " + cliente);
                tvUsuario.setVisibility(View.VISIBLE);
            } else {
                tvUsuario.setVisibility(View.GONE);
            }

            if (reservacion.getDescripcion() != null && !reservacion.getDescripcion().isEmpty()) {
                tvDescripcion.setText(reservacion.getDescripcion());
                tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                tvDescripcion.setVisibility(View.GONE);
            }

            // Configurar riples en click
            cardView.setOnClickListener(v -> {
                Log.d(TAG, "Click en reservación: " + reservacion.getId());
                // Acción al hacer click en la tarjeta
            });

            // Configurar botones según rol
            if ("admin".equals(userRol)) {
                btnEditar.setVisibility(View.VISIBLE);
                btnEliminar.setVisibility(View.VISIBLE);

                btnEditar.setOnClickListener(v -> editarReservacion(reservacion));
                btnEliminar.setOnClickListener(v -> mostrarConfirmacionEliminar(reservacion));
            } else {
                btnEditar.setVisibility(View.GONE);
                btnEliminar.setVisibility(View.GONE);
            }
        }

        /**
         * Editar reservación
         */
        private void editarReservacion(Reservacion reservacion) {
            Log.d(TAG, "Editando reservación: " + reservacion.getId());
            Intent intent = new Intent(context, CrearReservacionActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("edit_mode", true);
            intent.putExtra("reservacion_id", reservacion.getId());
            intent.putExtra("evento", reservacion.getEvento());
            intent.putExtra("tipo_evento", reservacion.getTipoEvento());
            intent.putExtra("fecha", reservacion.getFecha());
            intent.putExtra("lugar", reservacion.getLugar());
            intent.putExtra("descripcion", reservacion.getDescripcion());
            intent.putExtra("cantidad", reservacion.getCantidad());
            context.startActivity(intent);
        }

        /**
         * Mostrar diálogo de confirmación para eliminar
         */
        private void mostrarConfirmacionEliminar(final Reservacion reservacion) {
            // Crear un diálogo simple
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Eliminar Reservación")
                    .setMessage("¿Estás seguro de que deseas eliminar esta reservación?")
                    .setPositiveButton("Eliminar", (dialog, which) -> eliminarReservacion(reservacion))
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

        /**
         * Eliminar reservación
         */
        private void eliminarReservacion(final Reservacion reservacion) {
            Log.d(TAG, "Eliminando reservación: " + reservacion.getId());

            if (token == null) {
                Toast.makeText(context, "Token no disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = RetrofitClient.getApiService();
            String authorization = "Bearer " + token;

            Call<Void> call = apiService.eliminarReservacion(authorization, reservacion.getId());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Reservación eliminada con éxito");
                        Toast.makeText(context, "Reservación eliminada", Toast.LENGTH_SHORT).show();

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            reservaciones.remove(position);
                            notifyItemRemoved(position);
                        }
                    } else {
                        Log.e(TAG, "Error al eliminar: " + response.code());
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Error en llamada API", t);
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

