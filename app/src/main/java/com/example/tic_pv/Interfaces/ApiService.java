package com.example.tic_pv.Interfaces;

import com.example.tic_pv.Modelo.NotificacionRequest;
import com.example.tic_pv.Modelo.ProgramarNotificacionRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    // Endpoint para despertar el servidor
    @GET("/")
    Call<Void> despertarServidor();


    @POST("enviarNotificacion")
    Call<Void> enviarNotificacion(@Body NotificacionRequest request);

    @POST("programar")
    Call<Void> programarNotificacion(@Body ProgramarNotificacionRequest request);

    @GET("ejecutar")
    Call<Void> ejecutarNotificacionesProgramadas();
}
