package com.jlg.env.cli.common.command;

import com.beust.jcommander.Parameters;
import com.jlg.env.cli.common.service.CloudService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Parameters(commandNames = DestroyAllServers.NAME, commandDescription = "Destroy all servers")
@Component
@Getter
public class DestroyAllServers implements Command {
  static final String NAME = "destroy-all-servers";
  private final CloudService cloudService;

  @Autowired
  public DestroyAllServers(CloudService cloudService) {
    this.cloudService = cloudService;
  }

  @Override
  public boolean isNamed(String name) {
    return NAME.equals(name);
  }

  @Override
  public void execute() {
    List<Map<?, ?>> results = cloudService.destroyAllServers();
    results.forEach(r -> {
      System.out.println("Server: " + r.get("id") + " Status: " + r.get("status"));
    });
  }

}
