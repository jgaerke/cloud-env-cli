package com.jlg.env.cli.common;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.experimental.Wither;
import org.springframework.core.env.PropertyResolver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.beust.jcommander.internal.Lists.newArrayList;
import static com.google.common.base.Optional.fromNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

@Getter
public class Environment {
  private static final String PENDING = "pending";
  private static final String ACTIVATED = "activated";
  private static final String DEACTIVATED = "deactivated";
  private static final String CREATED = "created";
  private static final String DESTROYED = "destroyed";

  private final String name;
  @Wither
  private final String status;
  @Wither
  private final Domain domain;
  @Wither
  private final List<Server> servers;
  @JsonIgnore
  private final PropertyResolver propertyResolver;

  @JsonCreator
  public Environment(
      @JsonProperty("name") String name,
      @JsonProperty(value = "status", required = false) String status,
      @JsonProperty("domain") Domain domain,
      @JsonProperty("servers") List<Server> servers,
      @JacksonInject("propertyResolver") PropertyResolver propertyResolver) {
    this.name = name;
    this.domain = domain;
    this.status = fromNullable(status).or(PENDING);
    this.servers = servers;
    this.propertyResolver = propertyResolver;
  }

  public Environment create() {
    if (!PENDING.equals(status)) {
      return this;
    }

    List<Server> renamed = servers
        .stream()
        .map(s -> s.withName(!s.getName().contains(name + "-") ? name + "-" + s.getName() : s.getName()))
        .collect(toList());

    List<CompletableFuture<Server>> creationFutures = renamed
        .stream()
        .map(s -> supplyAsync(() -> s.create()))
        .collect(toList());

    CompletableFuture.allOf(creationFutures.toArray(new CompletableFuture[creationFutures.size()])).join();

    List<Server> created = creationFutures.stream().map(f -> {
      try {
        return f.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }).collect(toList());

    List<CompletableFuture<Server>> provisioningFutures = created
        .stream()
        .map(s -> supplyAsync(() -> {
          final ImmutableMap.Builder<String, String> variables = ImmutableMap.builder();
          created.forEach(c -> {
            variables.put("SERVER_" + c.getName().toUpperCase().replace("-", "_") + "_IP_ADDRESS", c.getIpAddress());
          });
          s.getEnvironment().forEach(k -> {
            variables.put(k.toUpperCase().replace(".", "_"), propertyResolver.getProperty(k));
          });
          return s.provision(variables.build());
        }))
        .collect(toList());

    CompletableFuture.allOf(provisioningFutures.toArray(new CompletableFuture[provisioningFutures.size()])).join();


    List<Server> provisioned = provisioningFutures
        .stream()
        .map(f -> {
          try {
            return f.get();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .collect(toList());

    return withServers(provisioned).withStatus(CREATED);
  }

  public Environment activate(String serverName) {
    if (domain == null || domain.getIpAddress() == null) {
      return this;
    }
    Server server = servers.stream().filter(s -> s.getName().equals(serverName)).findFirst().get();
    return withDomain(domain.map(server).withServer(server.getName())).withStatus(ACTIVATED);
  }

  public Environment deactivate() {
    if (domain == null || domain.getServer() == null || domain.getIpAddress() == null) {
      return this;
    }
    Server server = servers.stream().filter(s -> s.getName().equals(domain.getServer())).findFirst().get();
    return withDomain(domain.unmap(server)).withStatus(DEACTIVATED);
  }

  public Environment destroy() {
    if (!newArrayList(CREATED, ACTIVATED).contains(status)) {
      return this;
    }

    List<CompletableFuture<Server>> futures = servers
        .stream()
        .map(s -> supplyAsync(() -> s.destroy()))
        .collect(toList());

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

    List<Server> destroyed = futures
        .stream()
        .map(f -> {
          try {
            return f.get();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        })
        .collect(toList());

    return withServers(destroyed).withStatus(DESTROYED);
  }

  public Environment reset() {
    List<Server> resetServers = servers
        .stream()
        .map(s -> {
          Server resetServer = s
              .withId(null)
              .withStatus(Server.PENDING)
              .withIpAddress(null);
          if (resetServer.getName().contains(name + "-")) {
            resetServer = resetServer.withName(resetServer.getName().substring((name + "-").length()));
          }
          return resetServer;
        })
        .collect(toList());
    return withDomain(domain.withServer(null)).withServers(resetServers).withStatus(PENDING);
  }
}
