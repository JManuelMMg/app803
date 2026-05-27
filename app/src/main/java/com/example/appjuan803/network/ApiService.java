package com.example.appjuan803.network;

import com.example.appjuan803.models.LoginRequest;
import com.example.appjuan803.models.LoginResponse;
import com.example.appjuan803.models.Reservacion;
import com.example.appjuan803.models.ReservacionListResponse;
import com.example.appjuan803.models.EstadisticasResponse;
import com.example.appjuan803.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz de Retrofit para definir los endpoints de la API
 */
public interface ApiService {

    // ============= AUTENTICACIÓN =============

    /**
     * Endpoint de login
     * POST /api/auth/login
     */
    @POST("/api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Crear nuevo cliente/usuario normal
     * POST /api/auth/register
     */
    @POST("/api/auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    /**
     * Obtener información del usuario autenticado
     * GET /api/auth/me
     */
    @GET("/api/auth/me")
    Call<LoginResponse.UsuarioData> getMe(
        @Header("Authorization") String authorization
    );

    /**
     * Logout del usuario
     * POST /api/auth/logout
     */
    @POST("/api/auth/logout")
    Call<Void> logout(
        @Header("Authorization") String authorization
    );

    // ============= RESERVACIONES =============

    /**
     * Listar todas las reservaciones (paginado)
     * GET /api/reservaciones?skip=0&limit=10
     */
    @GET("/api/reservaciones")
    Call<ReservacionListResponse> listarReservaciones(
        @Header("Authorization") String authorization,
        @Query("skip") int skip,
        @Query("limit") int limit
    );

    /**
     * Crear una nueva reservación
     * POST /api/reservaciones
     */
    @POST("/api/reservaciones")
    Call<Reservacion> crearReservacion(
        @Header("Authorization") String authorization,
        @Body Reservacion reservacion
    );

    /**
     * Obtener una reservación por ID
     * GET /api/reservaciones/{id}
     */
    @GET("/api/reservaciones/{id}")
    Call<Reservacion> obtenerReservacion(
        @Header("Authorization") String authorization,
        @Path("id") int id
    );

    /**
     * Actualizar una reservación
     * PUT /api/reservaciones/{id}
     */
    @PUT("/api/reservaciones/{id}")
    Call<Reservacion> actualizarReservacion(
        @Header("Authorization") String authorization,
        @Path("id") int id,
        @Body Reservacion reservacion
    );

    /**
     * Eliminar una reservación
     * DELETE /api/reservaciones/{id}
     */
    @DELETE("/api/reservaciones/{id}")
    Call<Void> eliminarReservacion(
        @Header("Authorization") String authorization,
        @Path("id") int id
    );

    // ============= ADMIN - DASHBOARD =============

    /**
     * Obtener estadísticas del dashboard (solo admin)
     * GET /api/admin/dashboard
     */
    @GET("/api/admin/dashboard")
    Call<EstadisticasResponse> obtenerEstadisticas(
        @Header("Authorization") String authorization
    );
}

