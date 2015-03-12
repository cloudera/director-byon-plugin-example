/*
 * Copyright (c) 2015 Cloudera, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudera.director.byon.compute;

import com.cloudera.director.spi.v1.compute.util.AbstractComputeInstance;
import com.cloudera.director.spi.v1.compute.util.AbstractComputeProvider;
import com.cloudera.director.spi.v1.model.ConfigurationProperty;
import com.cloudera.director.spi.v1.model.Configured;
import com.cloudera.director.spi.v1.model.InstanceState;
import com.cloudera.director.spi.v1.model.InstanceStatus;
import com.cloudera.director.spi.v1.model.Resource;
import com.cloudera.director.spi.v1.model.util.SimpleInstanceState;
import com.cloudera.director.spi.v1.provider.ResourceProviderMetadata;
import com.cloudera.director.spi.v1.provider.util.SimpleResourceProviderMetadata;
import com.cloudera.director.spi.v1.util.ConfigurationPropertiesUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BYONComputeProvider extends AbstractComputeProvider<BYONComputeInstance, BYONComputeInstanceTemplate> {

  private static final Logger LOG = Logger.getLogger(BYONComputeProvider.class.getName());

  protected static final List<ConfigurationProperty> CONFIGURATION_PROPERTIES =
      ConfigurationPropertiesUtil.asList(BYONComputeProviderConfigurationProperty.values());

  public static final String ID = "compute";

  public static final ResourceProviderMetadata METADATA = SimpleResourceProviderMetadata.builder()
      .id(ID)
      .name("BYON Compute Provider")
      .description("Allocates instances from a predefined list")
      .providerConfigurationProperties(CONFIGURATION_PROPERTIES)
      .resourceTemplateConfigurationProperties(BYONComputeInstanceTemplate.getConfigurationProperties())
      .build();

  private final Deque<String> availableHosts = new ArrayDeque<String>();
  private final Map<String, String> allocations = new HashMap<String, String>();

  public BYONComputeProvider(Configured configuration) {
    super(configuration);

    String listOfHosts = configuration.getConfigurationValue(
        BYONComputeProviderConfigurationProperty.HOSTS);

    for (String host : listOfHosts.split(",")) {
      String trimmed = host.trim();
      if (!trimmed.isEmpty()) {
        availableHosts.add(trimmed);
      }
    }
  }

  @Override
  public ResourceProviderMetadata getProviderMetadata() {
    return METADATA;
  }

  @Override
  public Resource.Type getResourceType() {
    return AbstractComputeInstance.TYPE;
  }

  @Override
  public BYONComputeInstanceTemplate createResourceTemplate(
      String name, Configured configuration, Map<String, String> tags) {
    return new BYONComputeInstanceTemplate(name, configuration, tags);
  }

  @Override
  public Collection<BYONComputeInstance> allocate(BYONComputeInstanceTemplate template,
      Collection<String> instanceIds, int minCount) throws InterruptedException {

    if (availableHosts.size() < minCount) {
      throw new IllegalStateException("Not enough capacity");
    }

    List<BYONComputeInstance> result = new ArrayList<BYONComputeInstance>();
    Iterator<String> instanceIdsIter = instanceIds.iterator();

    int limit = Math.min(availableHosts.size(), instanceIds.size());
    while (limit > 0) {
      String id = instanceIdsIter.next();
      String host = availableHosts.peekFirst();

      try {
        result.add(new BYONComputeInstance(template, id, InetAddress.getByName(host)));

      } catch (UnknownHostException e) {
        throw new RuntimeException(e);
      }

      availableHosts.removeFirst();
      allocations.put(id, host);

      limit--;
    }

    return result;
  }

  @Override
  public Collection<BYONComputeInstance> find(BYONComputeInstanceTemplate template, Collection<String> instanceIds)
      throws InterruptedException {
    List<BYONComputeInstance> result = new ArrayList<BYONComputeInstance>();
    for (String currentId : instanceIds) {
      String host = allocations.get(currentId);
      if (host != null) {
        try {
          result.add(new BYONComputeInstance(template, host, InetAddress.getByName(host)));

        } catch (UnknownHostException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return result;
  }

  @Override
  public Map<String, InstanceState> getInstanceState(Collection<String> instanceIds) {
    Map<String, InstanceState> result = new HashMap<String, InstanceState>();
    for (String currentId : instanceIds) {
      if (allocations.containsKey(currentId)) {
        result.put(currentId, new SimpleInstanceState(InstanceStatus.RUNNING));
      } else {
        result.put(currentId, new SimpleInstanceState(InstanceStatus.DELETED));
      }
    }
    return result;
  }

  @Override
  public void delete(Collection<String> instanceIds) throws InterruptedException {
    for (String currentId : instanceIds) {
      String host = this.allocations.remove(currentId);
      if (host != null) {
        availableHosts.add(host);
      }
    }
  }

  public Deque<String> getAvailableHosts() {
    return availableHosts;
  }
}
