package com.jlg.env.cli.common;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.jlg.env.cli.common.service.CloudService;
import com.jlg.env.cli.common.service.ScriptService;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Wither;
import org.springframework.core.env.PropertyResolver;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.join;

@Getter
@ToString
public class Server {
  public static final String PENDING = "pending";
  public static final String CREATED = "created";
  public static final String PROVISIONED = "provisioned";
  public static final String DESTROYED = "destroyed";
  public static final String ENV_CLI_SCRIPT_CLASSPATH_BASE = "env.cli.script.classpath.base";

  @Wither
  private final String id;
  @Wither
  private final String name;
  private final String type;
  private final String image;
  private final String size;
  private final String region;
  private final boolean privatelyNetworked;
  @Wither
  private final String ipAddress;
  @Wither
  private final String status;
  private final List<String> environment;
  @JsonIgnore
  private final CloudService cloudService;
  @JsonIgnore
  private final ScriptService scriptService;
  @JsonIgnore
  private final PropertyResolver propertyResolver;

  @JsonCreator
  public Server(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("type") String type,
      @JsonProperty("image") String image,
      @JsonProperty("size") String size,
      @JsonProperty("region") String region,
      @JsonProperty(value = "privatelyNetworked", required = false) boolean privatelyNetworked,
      @JsonProperty(value = "ipAddress", required = false) String ipAddress,
      @JsonProperty(value = "status", required = false) String status,
      @JsonProperty(value = "environment", required = false) List<String> environment,
      @JacksonInject("cloudService") CloudService cloudService,
      @JacksonInject("scriptManager") ScriptService scriptService,
      @JacksonInject("propertyResolver")PropertyResolver propertyResolver) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.image = image;
    this.size = size;
    this.region = region;
    this.privatelyNetworked = privatelyNetworked;
    this.ipAddress = ipAddress;
    this.environment = fromNullable(environment).or(Collections.<String>emptyList());
    this.status = fromNullable(status).or(PENDING);
    this.cloudService = cloudService;
    this.scriptService = scriptService;
    this.propertyResolver = propertyResolver;
  }

  public Server create() {
    if (!PENDING.equals(status)) {
      return this;
    }
    log("Creating: " + this);
    Server server = cloudService.createServer(this).withStatus(CREATED);
    log("Creating: " + server);
    return server;
  }

  public Server provision(ImmutableMap<String, String> environmentVariables) {
    if (!CREATED.equals(status)) {
      return this;
    }
    log("Provisioning: " + this);
    scriptService
        .environment(this, environmentVariables)
        .extract(this, propertyResolver.getProperty(ENV_CLI_SCRIPT_CLASSPATH_BASE) + "/" + type + "/*")
        .execute(this, "provision", ipAddress)
        .cleanUp(this);

    Server server = withStatus(PROVISIONED);
    log("Provisioned: " + status);
    return server;
  }

  public Server execute(String script, String... args) {
    if (!newArrayList(CREATED, PROVISIONED).contains(status)) {
      return this;
    }

    scriptService
        .extract(this, propertyResolver.getProperty(ENV_CLI_SCRIPT_CLASSPATH_BASE) + "/" + type + "/*")
        .execute(this, script, args)
        .cleanUp(this);
    log("executed script: " + name + " with args: " + join(",", args));
    return this;
  }

  public Server destroy() {
    if (!newArrayList(CREATED, PROVISIONED).contains(status)) {
      return this;
    }

    Server server = cloudService.destroyServer(this).withStatus(DESTROYED).withId(null).withIpAddress(null);
    log("destroyed");
    return server;
  }

  private void log(String message) {
    System.out.println(message);
  }
}
