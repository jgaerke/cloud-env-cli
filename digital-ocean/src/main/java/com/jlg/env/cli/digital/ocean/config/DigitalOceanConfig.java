package com.jlg.env.cli.digital.ocean.config;

import com.jlg.env.cli.digital.ocean.service.FloatingIPActionService;
import com.jlg.env.cli.digital.ocean.service.OAuthTokenInjector;
import com.jlg.env.cli.digital.ocean.service.DropletService;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

@Configuration
public class DigitalOceanConfig {

  @Bean
  public OkHttpClient okHttpClient(OAuthTokenInjector oAuthTokenInjector) {
    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.interceptors().add(oAuthTokenInjector);
    return okHttpClient;
  }

  @Value("${digital.ocean.api.base.url}")
  String digitalOceanAPIBaseUrl;

  @Bean
  public DropletService dropletService(OkHttpClient okHttpClient) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(digitalOceanAPIBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build();
    return retrofit.create(DropletService.class);
  }

  @Bean
  public FloatingIPActionService floatingIPActionService(OkHttpClient okHttpClient) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(digitalOceanAPIBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(JacksonConverterFactory.create())
        .build();
    return retrofit.create(FloatingIPActionService.class);
  }
}
