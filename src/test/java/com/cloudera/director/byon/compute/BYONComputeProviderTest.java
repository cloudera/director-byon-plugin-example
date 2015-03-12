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

import static com.cloudera.director.byon.compute.BYONComputeInstanceTemplateConfigurationPropertyToken.PREFERRED_HOSTS;
import static com.cloudera.director.byon.compute.BYONComputeProviderConfigurationPropertyToken.HOSTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.cloudera.director.byon.BYONCloudProvider;
import com.cloudera.director.byon.BYONLauncher;
import com.cloudera.director.spi.v1.model.util.SimpleConfiguration;
import com.cloudera.director.spi.v1.provider.CloudProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class BYONComputeProviderTest {

  private BYONComputeProvider provider;

  @Before
  public void setUp() {
    CloudProvider cloudProvider = new BYONLauncher().createCloudProvider(
        BYONCloudProvider.ID,
        new SimpleConfiguration(Collections.<String, String>emptyMap()),
        Locale.getDefault());

    Map<String, String> configs = new HashMap<String, String>();
    configs.put(HOSTS.unwrap().getConfigKey(), "10.0.1.[5-15]");

    provider = (BYONComputeProvider) cloudProvider
        .createResourceProvider(BYONComputeProvider.ID, new SimpleConfiguration(configs));
  }

  @Test
  public void testAllocate_InOrder() throws InterruptedException {
    BYONComputeInstanceTemplate template = provider.createResourceTemplate(
        "test",
        new SimpleConfiguration(Collections.<String, String>emptyMap()),
        Collections.<String, String>emptyMap()
    );

    List<String> instanceIds = Arrays.asList("ID-1", "ID-2");
    provider.allocate(template, instanceIds, 1);

    assertThat(provider.getAllocations())
        .hasSameSizeAs(instanceIds)
        .containsKeys("ID-1", "ID-2");

    assertThat(provider.getAvailableHosts())
        .hasSize(9)
        .doesNotContain("10.0.1.5", "10.0.1.6")
        .contains("10.0.1.7");

    assertThat(provider.find(template, instanceIds))
        .hasSameSizeAs(instanceIds);
  }

  @Test
  public void testDelete_HostAreNotReturnedToThePool() throws InterruptedException {
    BYONComputeInstanceTemplate template = provider.createResourceTemplate(
        "test",
        new SimpleConfiguration(Collections.<String, String>emptyMap()),
        Collections.<String, String>emptyMap()
    );

    List<String> instanceIds = Arrays.asList("ID-1", "ID-2");

    provider.allocate(template, instanceIds, 1);
    provider.delete(template, instanceIds);

    assertThat(provider.getAvailableHosts())
        .hasSize(9)
        .doesNotContain("10.0.1.5", "10.0.1.6")
        .contains("10.0.1.7");
  }

  @Test
  public void testAllocate_WithManyPreferredHosts() throws Exception {
    Map<String, String> configs = new HashMap<String, String>();
    configs.put(PREFERRED_HOSTS.unwrap().getConfigKey(), "10.0.1.[10-15]");

    BYONComputeInstanceTemplate template = provider.createResourceTemplate(
        "test",
        new SimpleConfiguration(configs),
        Collections.<String, String>emptyMap()
    );

    List<String> instanceIds = Arrays.asList("ID-1", "ID-2");
    provider.allocate(template, instanceIds, 1);

    assertThat(provider.getAvailableHosts())
        .hasSize(9)
        .doesNotContain("10.0.1.10", "10.0.1.11")
        .contains("10.0.1.5", "10.0.1.12");

    assertThat(provider.find(template, instanceIds))
        .hasSameSizeAs(instanceIds);
  }

  @Test
  public void testAllocate_WithOnePreferredHost() throws Exception {
    Map<String, String> configs = new HashMap<String, String>();
    configs.put(PREFERRED_HOSTS.unwrap().getConfigKey(), "10.0.1.10");

    BYONComputeInstanceTemplate template = provider.createResourceTemplate(
        "test",
        new SimpleConfiguration(configs),
        Collections.<String, String>emptyMap()
    );

    List<String> instanceIds = Arrays.asList("ID-1", "ID-2");
    provider.allocate(template, instanceIds, 1);

    assertThat(provider.getAvailableHosts())
        .hasSize(9)
        .doesNotContain("10.0.1.10", "10.0.1.5")
        .contains("10.0.1.6", "10.0.1.11");

    assertThat(provider.find(template, instanceIds))
        .hasSameSizeAs(instanceIds);
  }
}
