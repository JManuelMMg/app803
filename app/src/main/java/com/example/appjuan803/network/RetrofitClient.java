package com.example.appjuan803.network;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Cliente singleton de Retrofit
 * Gestiona la conexión con la API de FastAPI
 */
public class RetrofitClient {

    private static final String TAG = "RetrofitClient";

    // URL base del servidor FastAPI
    // Cambiar según tu ambiente:
    // - Local: http://192.168.1.XX:8000
    // - Remoto: http://tu-servidor.com
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    private static Retrofit retrofit;
    private static ApiService apiService;

    /**
     * Obtener instancia del cliente Retrofit
     * Si no existe, la crea con configuración completa
     */
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Crear interceptor de logging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                message -> Log.d(TAG, message)
            );
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Crear cliente HTTP
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(loggingInterceptor);

            // Crear Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Log.d(TAG, "Retrofit instance creado con URL: " + BASE_URL);
        }
        return retrofit;
    }

    /**
     * Obtener instancia del servicio API
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Reiniciar la instancia del cliente (útil para cambiar URL)
     */
    public static void resetInstance() {
        retrofit = null;
        apiService = null;
        Log.d(TAG, "Retrofit instance reiniciado");
    }
}

