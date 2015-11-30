package com.jlg.env.cli.digital.ocean.service;

import com.jlg.env.cli.common.service.CloudService;
import com.jlg.env.cli.common.Domain;
import com.jlg.env.cli.common.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DigitalOceanCloudService implements CloudService {
  private final DropletCreatorService dropletCreatorService;
  private final DropletDestroyerService dropletDestroyerService;
  private final FloatingIPMapperService floatingIPMapperService;

  @Autowired
  public DigitalOceanCloudService(
      DropletCreatorService dropletCreatorService,
      DropletDestroyerService dropletDestroyerService,
      FloatingIPMapperService floatingIPMapperService) {
    this.dropletCreatorService = dropletCreatorService;
    this.dropletDestroyerService = dropletDestroyerService;
    this.floatingIPMapperService = floatingIPMapperService;
  }

  @Override
  public Server createServer(Server server) {
    return dropletCreatorService.create(server);
  }

  @Override
  public Domain mapDomain(Server server, Domain domain) {
    return floatingIPMapperService.map(server, domain);
  }

  @Override
  public Domain unmap(Server server, Domain domain) {
    return floatingIPMapperService.unmap(server, domain);
  }

  @Override
  public Server destroyServer(Server server) {
    return dropletDestroyerService.destroy(server);
  }

  @Override
  public List<Map<?, ?>> destroyAllServers() {
    return dropletDestroyerService.destroyAllDroplets();
  }
}
