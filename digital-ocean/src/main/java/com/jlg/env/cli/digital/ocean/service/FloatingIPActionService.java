package com.jlg.env.cli.digital.ocean.service;

import retrofit.Call;
import retrofit.http.*;

import java.util.Map;

public interface FloatingIPActionService {
  @POST("/v2/floating_ips/{ipAddress}/actions")
  Call<Object> invoke(@Path("ipAddress") String ipAddress, @Body Map<String, Object> request);
}
