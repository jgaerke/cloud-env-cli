package com.jlg.env.cli.digital.ocean.service;


import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthTokenInjector implements Interceptor {
  @Value("${digital.ocean.api.token}")
  String digitalOceanApiToken;

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    request = request.newBuilder()
        .header("Authorization", "Bearer " + digitalOceanApiToken)
        .build();
    return chain.proceed(request);
  }
}
