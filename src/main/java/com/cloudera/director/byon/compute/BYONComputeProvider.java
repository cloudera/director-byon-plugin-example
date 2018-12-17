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

import com.cloudera.director.byon.util.HostGroups;
import com.cloudera.director.spi.v2.compute.util.AbstractComputeInstance;
import com.cloudera.director.spi.v2.compute.util.AbstractComputeProvider;
import com.cloudera.director.spi.v2.model.ConfigurationProperty;
import com.cloudera.director.spi.v2.model.ConfigurationValidator;
import com.cloudera.director.spi.v2.model.Configured;
import com.cloudera.director.spi.v2.model.InstanceState;
import com.cloudera.director.spi.v2.model.InstanceStatus;
import com.cloudera.director.spi.v2.model.LocalizationContext;
import com.cloudera.director.spi.v2.model.Resource;
import com.cloudera.director.spi.v2.model.util.CompositeConfigurationValidator;
import com.cloudera.director.spi.v2.model.util.SimpleInstanceState;
import com.cloudera.director.spi.v2.provider.ResourceProviderMetadata;
import com.cloudera.director.spi.v2.provider.util.SimpleResourceProviderMetadata;
import com.cloudera.director.spi.v2.util.ConfigurationPropertiesUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A provider for compute resources that works with a predefined list of hosts.
 */
public class BYONComputeProvider
    extends AbstractComputeProvider<BYONComputeInstance, BYONComputeInstanceTemplate> {

  private static final Logger LOG = Logger.getLogger(BYONComputeProvider.class.getName());

  protected static final List<ConfigurationProperty> CONFIGURATION_PROPERTIES =
      ConfigurationPropertiesUtil.asConfigurationPropertyList(
          BYONComputeProviderConfigurationPropertyToken.values());

  public static final String ID = "compute";

  public static final ResourceProviderMetadata METADATA = SimpleResourceProviderMetadata.builder()
      .id(ID)
      .name("BYON Compute Provider")
      .description("Allocates instances from a predefined list")
      .providerClass(BYONComputeProvider.class)
      .providerConfigurationProperties(CONFIGURATION_PROPERTIES)
      .resourceTemplateConfigurationProperties(
          BYONComputeInstanceTemplate.getConfigurationProperties())
      .build();

  private final Deque<String> availableHosts = new ArrayDeque<String>();
  private final Map<String, String> allocations = new HashMap<String, String>();

  private final ConfigurationValidator resourceTemplateConfigurationValidator;

  public BYONComputeProvider(Configured configuration,
      LocalizationContext cloudLocalizationContext) {
    super(configuration, METADATA, cloudLocalizationContext);
    LocalizationContext localizationContext = getLocalizationContext();

    String hostGroupExpressions = configuration.getConfigurationValue(
        BYONComputeProviderConfigurationPropertyToken.HOSTS, localizationContext);

    Collections.addAll(availableHosts, HostGroups.expand(hostGroupExpressions));
    if (availableHosts.isEmpty()) {
      throw new IllegalArgumentException("Host group expressions expands " +
          "to an empty list: " + hostGroupExpressions);
    }

    this.resourceTemplateConfigurationValidator =
        new CompositeConfigurationValidator(METADATA.getResourceTemplateConfigurationValidator(),
            new BYONComputeInstanceTemplateConfigurationValidator(this));
  }

  synchronized Deque<String> getAvailableHosts() {
    return availableHosts;
  }

  synchronized Map<String, String> getAllocations() {
    return allocations;
  }

  @Override
  public ConfigurationValidator getResourceTemplateConfigurationValidator() {
    return resourceTemplateConfigurationValidator;
  }

  @Override
  public Resource.Type getResourceType() {
    return AbstractComputeInstance.TYPE;
  }

  @Override
  public BYONComputeInstanceTemplate createResourceTemplate(
      String name, Configured configuration, Map<String, String> tags) {
    return new BYONComputeInstanceTemplate(name, configuration, tags, getLocalizationContext());
  }

  @Override
  public synchronized Collection<BYONComputeInstance> allocate(BYONComputeInstanceTemplate template,
      Collection<String> instanceIds, int minCount) throws InterruptedException {

    if (availableHosts.size() < minCount) {
      throw new IllegalStateException(String.format("Not enough capacity. Requested at " +
          "least %d, only have %d available", minCount, availableHosts.size()));
    }

    Iterator<String> instanceIdsIter = instanceIds.iterator();
    int limit = Math.min(availableHosts.size(), instanceIds.size());

    List<BYONComputeInstance> result = new ArrayList<>();

    // Try to allocate as many preferred hosts as possible first

    for (String host : template.getPreferredHosts()) {
      try {
        //noinspection ResultOfMethodCallIgnored
        InetAddress hostAddress = InetAddress.getByName(host);
        if (availableHosts.remove(host)) {
          String id = instanceIdsIter.next();

          allocations.put(id, host);
          LOG.info(String.format("New preferred allocation: %s -> %s", host, id));

          result.add(new BYONComputeInstance(template, id, hostAddress));

          limit--;
          if (limit == 0) {
            break;
          }
        }

      } catch (UnknownHostException e) {
        throw new IllegalArgumentException(e);
      }
    }

    // Pick any other instance if more are needed

    while (limit > 0) {
      String id = instanceIdsIter.next();
      String host = availableHosts.peekFirst();

      availableHosts.removeFirst();
      allocations.put(id, host);

      try {
        result.add(new BYONComputeInstance(template, id, InetAddress.getByName(host)));
      } catch (UnknownHostException e) {
        throw new IllegalArgumentException(e);
      }

      LOG.info(String.format("New allocation: %s -> %s", host, id));

      limit--;
    }

    return result;
  }

  @Override
  public synchronized Collection<BYONComputeInstance> find(
      BYONComputeInstanceTemplate template, Collection<String> instanceIds)
      throws InterruptedException {

    List<BYONComputeInstance> result = new ArrayList<>();
    for (String currentId : instanceIds) {
      String host = allocations.get(currentId);
      if (host != null) {
        try {
          result.add(new BYONComputeInstance(template, currentId, InetAddress.getByName(host)));

        } catch (UnknownHostException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return result;
  }

  @Override
  public synchronized Map<String, InstanceState> getInstanceState(
      BYONComputeInstanceTemplate template, Collection<String> instanceIds) {

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
  public synchronized void delete(BYONComputeInstanceTemplate template,
      Collection<String> instanceIds) throws InterruptedException {

    for (String currentId : instanceIds) {
      String host = allocations.remove(currentId);
      LOG.info(String.format("Deleted allocation: %s -> %s", host, currentId));
    }
  }

  @Override
  public Map<String, Set<String>> getHostKeyFingerprints(BYONComputeInstanceTemplate template,
      Collection<String> instanceIds) {
    return Collections.emptyMap();
  }

}
