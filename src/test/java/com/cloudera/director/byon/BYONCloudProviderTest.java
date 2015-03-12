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

package com.cloudera.director.byon;

import static com.cloudera.director.byon.compute.BYONComputeProviderConfigurationPropertyToken.HOSTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.cloudera.director.byon.compute.BYONComputeProvider;
import com.cloudera.director.spi.v1.model.util.SimpleConfiguration;
import com.cloudera.director.spi.v1.provider.CloudProvider;
import com.cloudera.director.spi.v1.provider.ResourceProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

public class BYONCloudProviderTest {

  private CloudProvider cloudProvider;

  @Before
  public void setUp() {
    cloudProvider = new BYONLauncher().createCloudProvider(
        BYONCloudProvider.ID,
        new SimpleConfiguration(Collections.<String, String>emptyMap()),
        Locale.getDefault());
  }

  @Test
  public void testCreateResourceProvider() {
    Map<String, String> configs = new HashMap<String, String>();
    configs.put(HOSTS.unwrap().getConfigKey(), "10.0.1.[5-15]");

    ResourceProvider provider = cloudProvider.createResourceProvider(BYONComputeProvider.ID,
        new SimpleConfiguration(configs));

    assertThat(provider.getProviderMetadata().getId()).isEqualTo(BYONComputeProvider.ID);

    assertThat(provider.getProviderMetadata().getResourceTemplateConfigurationProperties())
        .hasSameSizeAs(cloudProvider.getProviderMetadata().getResourceProviderMetadata(BYONComputeProvider.ID)
            .getResourceTemplateConfigurationProperties());
  }

  @Test(expected = NoSuchElementException.class)
  public void testCreateResourceProvider_InvalidId() {
    cloudProvider.createResourceProvider("dummy", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateResourceProvider_EmptyListOfHosts() {
    Map<String, String> configs = new HashMap<String, String>();
    configs.put(HOSTS.unwrap().getConfigKey(), "");

    cloudProvider.createResourceProvider(BYONComputeProvider.ID, new SimpleConfiguration(configs));
  }
}
