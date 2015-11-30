package com.jlg.env.cli.common;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jlg.env.cli.common.service.CloudService;
import lombok.Getter;
import lombok.experimental.Wither;

@Getter
public class Domain {
  private final String name;
  private final String ipAddress;
  @Wither
  private final String server;
  @JsonIgnore
  private CloudService cloudService;


  @JsonCreator
  public Domain(
      @JsonProperty("name") String name,
      @JsonProperty("ipAddress") String ipAddress,
      @JsonProperty("server") String server,
      @JacksonInject("cloudService") CloudService cloudService
  ) {
    this.name = name;
    this.ipAddress = ipAddress;
    this.server = server;
    this.cloudService = cloudService;
  }

  public Domain map(Server server) {
    return cloudService.mapDomain(server, this);
  }

  public Domain unmap(Server server) {
    return cloudService.unmap(server, this);
  }
}
