package com.example.appjuan803.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appjuan803.R;
import com.example.appjuan803.models.Evento;

import java.util.List;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.ViewHolder> {

    public interface OnReservarClickListener {
        void onReservarClick(Evento evento);
    }

    private List<Evento> eventos;
    private final OnReservarClickListener listener;

    public EventoAdapter(List<Evento> eventos, OnReservarClickListener listener) {
        this.eventos = eventos;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(eventos.get(position));
    }

    @Override
    public int getItemCount() {
        return eventos != null ? eventos.size() : 0;
    }

    public void setEventos(List<Evento> eventos) {
        this.eventos = eventos;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitulo;
        private final TextView tvTipo;
        private final TextView tvFecha;
        private final TextView tvLugar;
        private final TextView tvDescripcion;
        private final TextView tvCapacidad;
        private final AppCompatButton btnReservar;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvEventoTitulo);
            tvTipo = itemView.findViewById(R.id.tvEventoTipo);
            tvFecha = itemView.findViewById(R.id.tvEventoFecha);
            tvLugar = itemView.findViewById(R.id.tvEventoLugar);
            tvDescripcion = itemView.findViewById(R.id.tvEventoDescripcion);
            tvCapacidad = itemView.findViewById(R.id.tvEventoCapacidad);
            btnReservar = itemView.findViewById(R.id.btnReservarEvento);
        }

        void bind(Evento evento) {
            tvTitulo.setText(evento.getTitulo());
            tvTipo.setText(evento.getTipoEvento() != null ? evento.getTipoEvento() : "Evento general");
            tvFecha.setText("Fecha: " + evento.getFecha());
            tvLugar.setText("Lugar: " + evento.getLugar());

            if (evento.getDescripcion() != null && !evento.getDescripcion().trim().isEmpty()) {
                tvDescripcion.setText(evento.getDescripcion());
                tvDescripcion.setVisibility(View.VISIBLE);
            } else {
                tvDescripcion.setVisibility(View.GONE);
            }

            if (evento.getCapacidad() != null) {
                tvCapacidad.setText("Cupo: " + evento.getCapacidad() + " personas");
            } else {
                tvCapacidad.setText("Cupo abierto");
            }

            btnReservar.setOnClickListener(v -> listener.onReservarClick(evento));
        }
    }
}
