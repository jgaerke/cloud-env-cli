package com.jlg.env.cli.digital.ocean.service;

import com.jlg.env.cli.common.Server;
import com.jlg.env.cli.digital.ocean.exception.DropletCreationException;
import com.jlg.env.cli.digital.ocean.exception.DropletCreationTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.PropertyResolver;
import org.springframework.stereotype.Component;
import retrofit.Call;
import retrofit.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.jlg.env.cli.common.support.MapUtil.get;
import static java.lang.String.valueOf;
import static java.lang.System.getProperty;
import static java.util.Collections.singletonList;

@Component
public class DropletCreatorService {
  public static final String ACTIVE_STATUS = "active";
  private final DropletService dropletService;
  private PropertyResolver propertyResolver;
  private final int dropletCreationWaitTimeInSeconds;

  @Autowired
  public DropletCreatorService(
      DropletService dropletService,
      PropertyResolver propertyResolver,
      @Value("${digital.ocean.droplet.creation.wait.time.in.seconds}")
      int dropletCreationWaitTimeInSeconds
  ) {
    this.dropletService = dropletService;
    this.propertyResolver = propertyResolver;
    this.dropletCreationWaitTimeInSeconds = dropletCreationWaitTimeInSeconds;
  }

  public Server create(Server server) {
    Map<String, Object> droplet = null;

    Map<String, Object> request = new HashMap<>();
    request.put("name", server.getName());
    request.put("image", server.getImage());
    request.put("region", server.getRegion());
    request.put("size", server.getSize());
    request.put("backups", false);
    request.put("ipv6", false);
    request.put("user_data", null);
    addSSHKeyIfProvided(request);
    request.put("private_networking", server.isPrivatelyNetworked());

    Response<Map<String, Object>> response = null;
    Call<Map<String, Object>> callable = dropletService.create(request);

    try {
      response = callable.execute();
    } catch (IOException e) {
      throw new DropletCreationException("Error creating droplet", e);
    }

    if (!valueOf(response.code()).startsWith("2")) {
      throw new DropletCreationException("Error creating droplet. Status Code: " + response.code());
    }

    //set droplet id
    Integer dropletId = get(response.body(), "droplet.id");

    droplet = waitUntilActiveOrTimeIsUp(dropletId.toString(), dropletCreationWaitTimeInSeconds);

    if (!droplet.get("status").equals(ACTIVE_STATUS)) {
      throw new DropletCreationTimeoutException(
          dropletId,
          "Droplet was not ready within expected time frame of: " +
              dropletCreationWaitTimeInSeconds + " seconds."
      );
    }
    //set droplet ip address
    List<Map<String, Object>> v4Addresses = get(droplet, "networks.v4");
    return server.
        withId(dropletId.toString()).
        withStatus(get(droplet, "status")).
        withIpAddress(get(v4Addresses.get(0), "ip_address"));
  }

  private void addSSHKeyIfProvided(Map<String, Object> request) {
    String sshKey = propertyResolver.getProperty("digital.ocean.ssh.key");
    if (!isNullOrEmpty(sshKey)) {
      System.out.println("SSH KEY: " + sshKey);
      request.put("ssh_keys", singletonList(sshKey));
    }
  }

  private Map<String, Object> waitUntilActiveOrTimeIsUp(String dropletId, int secondsToWait) {
    try {
      Map<String, Object> droplet = get(dropletService.getById(dropletId).execute().body(), "droplet");
      int ctr = 0;
      int maxIterations = (secondsToWait * 1000) / 5000;
      while (ctr < maxIterations) {
        if (droplet.get("status").equals("active")) {
          break;
        }
        try {
          Thread.sleep(5000);
          System.out.print("...");
        } catch (InterruptedException interruptEx) {
          throw new RuntimeException(interruptEx);
        }
        droplet = get(dropletService.getById(dropletId).execute().body(), "droplet");
        ctr += 1;
      }
      System.out.println(getProperty("line.separator"));
      return droplet;
    } catch (IOException e) {
      throw new DropletCreationException(e);
    }
  }
}
