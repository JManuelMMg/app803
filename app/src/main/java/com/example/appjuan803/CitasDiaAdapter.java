package com.example.appjuan803;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appjuan803.models.Cita;
import java.util.List;

public class CitasDiaAdapter extends RecyclerView.Adapter<CitasDiaAdapter.ViewHolder> {

    private List<Cita> listaCitas;

    public CitasDiaAdapter(List<Cita> listaCitas) {
        this.listaCitas = listaCitas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rv_citas_dia, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cita cita = listaCitas.get(position);
        
        holder.tvHora.setText(cita.horaCita);
        holder.tvNombre.setText(cita.nomCliente);
        holder.tvTelefono.setText(cita.telCliente);
        holder.tvMotivo.setText(cita.asuntoCita);
    }

    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvNombre, tvTelefono, tvMotivo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvMotivo = itemView.findViewById(R.id.tvMotivo);
        }
    }
}

