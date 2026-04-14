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
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {

    private List<Cita> listaCitas;
    private OnCitaClickListener listener;

    public interface OnCitaClickListener {
        void onEditClick(Cita cita);
        void onDeleteClick(Cita cita);
    }

    public CitaAdapter(List<Cita> listaCitas, OnCitaClickListener listener) {
        this.listaCitas = listaCitas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_citas, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = listaCitas.get(position);
        holder.tvNombre.setText(cita.nomCliente);
        holder.tvTelefono.setText(cita.telCliente);
        holder.tvHora.setText(cita.horaCita);
        holder.tvDia.setText(cita.diaCita);
        holder.tvAsunto.setText(cita.asuntoCita);

        holder.btnEditar.setOnClickListener(v -> listener.onEditClick(cita));
        holder.btnBorrar.setOnClickListener(v -> listener.onDeleteClick(cita));
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public void setCitas(List<Cita> nuevasCitas) {
        this.listaCitas = nuevasCitas;
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