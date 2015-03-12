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

import com.cloudera.director.byon.compute.BYONComputeProvider;
import com.cloudera.director.spi.v1.model.ConfigurationProperty;
import com.cloudera.director.spi.v1.model.Configured;
import com.cloudera.director.spi.v1.provider.CloudProvider;
import com.cloudera.director.spi.v1.provider.CloudProviderMetadata;
import com.cloudera.director.spi.v1.provider.ResourceProvider;
import com.cloudera.director.spi.v1.provider.ResourceProviderMetadata;
import com.cloudera.director.spi.v1.provider.util.SimpleCloudProviderMetadataBuilder;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class BYONCloudProvider implements CloudProvider {

  public static final String ID = "byon";

  private static final List<ResourceProviderMetadata> RESOURCE_PROVIDER_METADATA =
      Collections.singletonList(BYONComputeProvider.METADATA);

  protected static final CloudProviderMetadata METADATA = new SimpleCloudProviderMetadataBuilder()
      .id(ID)
      .name("Bring Your Own Nodes (BYON)")
      .description("A fake provider implementation that allocates " +
          "instances from a static list of hosts")
      .configurationProperties(Collections.<ConfigurationProperty>emptyList())
      .credentialsProviderMetadata(BYONCredentialsProvider.METADATA)
      .resourceProviderMetadata(RESOURCE_PROVIDER_METADATA)
      .build();

  @Override
  public CloudProviderMetadata getMetadata() {
    return METADATA;
  }

  @Override
  public ResourceProvider createResourceProvider(
      String resourceProviderId, Configured configuration) {

    if (BYONComputeProvider.METADATA.getId().equals(resourceProviderId)) {
      return new BYONComputeProvider(configuration);
    }

    throw new NoSuchElementException("Invalid provider id: " + resourceProviderId);
  }
}
