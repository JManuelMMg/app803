package com.example.appjuan803.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.appjuan803.models.Cita;
import java.util.List;

@Dao
public interface CitaDao {
    @Query("SELECT * from citas")
    List<Cita> obtenerCitas();

    @Insert
    void agregarCita(Cita... cita);

    @Update
    void actualizarCita(Cita... cita);

    @Delete
    void eliminarCita(Cita cita);
}
