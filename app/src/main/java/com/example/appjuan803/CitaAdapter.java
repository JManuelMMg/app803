package com.example.appjuan803;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appjuan803.models.Cita;
import java.util.ArrayList;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Cita> listaCitas;
    private List<Cita> listaFiltrada;
    private OnEditClickListener onEditListener;
    private OnDeleteClickListener onDeleteListener;

    // Interfaz para eventos de editar
    public interface OnEditClickListener {
        void onEditClick(Cita cita);
    }

    // Interfaz para eventos de eliminar
    public interface OnDeleteClickListener {
        void onDeleteClick(Cita cita);
    }

    // Interfaz heredada (compatible con código anterior)
    public interface OnCitaClickListener {
        void onEditClick(Cita cita);
        void onDeleteClick(Cita cita);
    }

    // Constructor con dos interfaces
    public CitaAdapter(List<Cita> listaCitas, OnEditClickListener onEditListener, 
                       OnDeleteClickListener onDeleteListener) {
        this.listaCitas = new ArrayList<>(listaCitas);
        this.listaFiltrada = new ArrayList<>(listaCitas);
        this.onEditListener = onEditListener;
        this.onDeleteListener = onDeleteListener;
    }

    // Constructor original (mantener compatibilidad)
    public CitaAdapter(List<Cita> listaCitas, OnCitaClickListener listener) {
        this.listaCitas = new ArrayList<>(listaCitas);
        this.listaFiltrada = new ArrayList<>(listaCitas);
        this.onEditListener = listener::onEditClick;
        this.onDeleteListener = listener::onDeleteClick;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_citas, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = listaFiltrada.get(position);
        holder.tvNombre.setText(cita.nomCliente);
        holder.tvTelefono.setText(cita.telCliente);
        holder.tvHora.setText(cita.horaCita);
        holder.tvDia.setText(cita.diaCita);
        holder.tvAsunto.setText(cita.asuntoCita);

        holder.btnEditar.setOnClickListener(v -> {
            if (onEditListener != null) {
                onEditListener.onEditClick(cita);
            }
        });

        holder.btnBorrar.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.onDeleteClick(cita);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    /**
     * Filtra las citas según el texto ingresado
     */
    public void filtrarCliente(String texto) {
        listaFiltrada.clear();
        
        if (texto == null || texto.isEmpty()) {
            listaFiltrada.addAll(listaCitas);
        } else {
            String filtroLower = texto.toLowerCase();
            for (Cita cita : listaCitas) {
                if (cita.nomCliente.toLowerCase().contains(filtroLower) ||
                    cita.telCliente.toLowerCase().contains(filtroLower) ||
                    cita.asuntoCita.toLowerCase().contains(filtroLower)) {
                    listaFiltrada.add(cita);
                }
            }
        }
        
        notifyDataSetChanged();
    }

    public void setCitas(List<Cita> nuevasCitas) {
        this.listaCitas = new ArrayList<>(nuevasCitas);
        this.listaFiltrada = new ArrayList<>(nuevasCitas);
        notifyDataSetChanged();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvTelefono, tvHora, tvDia, tvAsunto;
        ImageButton btnEditar, btnBorrar;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNomNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelCliente);
            tvHora = itemView.findViewById(R.id.tvHoraCita);
            tvDia = itemView.findViewById(R.id.tvDiaCita);
            tvAsunto = itemView.findViewById(R.id.tvAsuntoCliente);
            btnEditar = itemView.findViewById(R.id.ibtnEditar);
            btnBorrar = itemView.findViewById(R.id.ibtnBorrar);
        }
    }
}

