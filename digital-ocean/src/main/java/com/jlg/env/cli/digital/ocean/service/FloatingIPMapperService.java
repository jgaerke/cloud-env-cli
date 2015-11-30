package com.jlg.env.cli.digital.ocean.service;

import com.google.common.collect.ImmutableMap;
import com.jlg.env.cli.common.Domain;
import com.jlg.env.cli.common.Server;
import com.jlg.env.cli.digital.ocean.exception.FloatingIPAssignmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit.Call;
import retrofit.Response;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.valueOf;

@Component
public class FloatingIPMapperService {

  private final FloatingIPActionService floatingIPActionService;

  @Autowired
  public FloatingIPMapperService(FloatingIPActionService floatingIPActionService) {
    this.floatingIPActionService = floatingIPActionService;
  }

  public Domain map(Server server, Domain domain) {
    Map<String, Object> request = ImmutableMap.of(
        "type", "assign",
        "droplet_id", server.getId()
    );

    Response<Object> response = null;
    Call<Object> callable = floatingIPActionService.invoke(domain.getIpAddress(), request);

    try {
      response = callable.execute();
    } catch (IOException e) {
      throw new FloatingIPAssignmentException("Error assigning floating ip address", e);
    }

    if (!valueOf(response.code()).startsWith("2")) {
      throw new FloatingIPAssignmentException("Error assinging floating ip address. Status Code: " + response.code());
    }

    return domain.withServer(server.getName());
  }


  public Domain unmap(Server server, Domain domain) {
    Map<String, Object> request = ImmutableMap.of(
        "type", "unassign",
        "droplet_id", server.getId()
    );

    Response<Object> response = null;
    Call<Object> callable = floatingIPActionService.invoke(domain.getIpAddress(), request);

    try {
      response = callable.execute();
    } catch (IOException e) {
      throw new FloatingIPAssignmentException("Error unassigning floating ip address", e);
    }

    if (!valueOf(response.code()).startsWith("2")) {
      throw new FloatingIPAssignmentException("Error unassinging floating ip address. Status Code: " + response.code());
    }

    return domain.withServer(null);
  }
}
