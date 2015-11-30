package com.jlg.env.cli.digital.ocean.service;

import com.jlg.env.cli.common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import retrofit.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.stream.Collectors.toList;

@Component
@Profile(value = {"default", "digital-ocean"})
public class DropletDestroyerService {

  private final DropletService dropletService;

  @Autowired
  public DropletDestroyerService(DropletService dropletService) {
    this.dropletService = dropletService;
  }

  public Server destroy(Server server) {
    try {
      Integer code = dropletService.delete(server.getId()).execute().code();
      return server;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Map<?, ?>> destroyAllDroplets() {
    try {
      Response<Map<String, Object>> response = dropletService.getAll().execute();
      List<Map<String, Object>> droplets = (List<Map<String, Object>>) response.body().get("droplets");
      List<CompletableFuture<Map<?, ?>>> deletionFutures = droplets.stream().map(d -> CompletableFuture.<Map<?, ?>>supplyAsync(() -> {
        try {
          String dropletId = String.valueOf(d.get("id"));
          Integer code = dropletService.delete(dropletId.toString()).execute().code();
          return of("id", dropletId, "status", code);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      })).collect(toList());

      CompletableFuture.allOf(deletionFutures.toArray(new CompletableFuture[deletionFutures.size()])).join();

      return deletionFutures.stream().map(f -> {
        try {
          return f.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }).collect(toList());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
