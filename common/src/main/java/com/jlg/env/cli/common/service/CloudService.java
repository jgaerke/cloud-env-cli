package com.jlg.env.cli.common.service;

import com.jlg.env.cli.common.Domain;
import com.jlg.env.cli.common.Server;

import java.util.List;
import java.util.Map;

public interface CloudService {
  Server createServer(Server server);

  Domain mapDomain(Server server, Domain domain);

  Domain unmap(Server server, Domain domain);

  Server destroyServer(Server server);

  List<Map<?, ?>> destroyAllServers();
}
