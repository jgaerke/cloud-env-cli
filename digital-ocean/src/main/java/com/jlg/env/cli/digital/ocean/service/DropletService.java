package com.jlg.env.cli.digital.ocean.service;

import retrofit.Call;
import retrofit.http.*;

import java.util.Map;

public interface DropletService {
  @POST("/v2/droplets")
  Call<Map<String, Object>> create(@Body Map<String, Object> request);

  @GET("/v2/droplets/{id}")
  Call<Map<String, Object>> getById(@Path("id") String dropletId);

  @GET("/v2/droplets")
  Call<Map<String, Object>> getAll();

  @DELETE("/v2/droplets/{id}")
  Call<Object> delete(@Path("id") String dropletId);
}
