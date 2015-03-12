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
import com.cloudera.director.spi.v1.model.LocalizationContext;
import com.cloudera.director.spi.v1.provider.CloudProviderMetadata;
import com.cloudera.director.spi.v1.provider.ResourceProvider;
import com.cloudera.director.spi.v1.provider.ResourceProviderMetadata;
import com.cloudera.director.spi.v1.provider.util.AbstractCloudProvider;
import com.cloudera.director.spi.v1.provider.util.SimpleCloudProviderMetadataBuilder;
import com.cloudera.director.spi.v1.provider.util.SimpleCredentialsProviderMetadata;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class BYONCloudProvider extends AbstractCloudProvider {

  public static final String ID = "byon";

  private static final List<ResourceProviderMetadata> RESOURCE_PROVIDER_METADATA =
      Collections.singletonList(BYONComputeProvider.METADATA);

  protected static final CloudProviderMetadata METADATA = new SimpleCloudProviderMetadataBuilder()
      .id(ID)
      .name("Bring Your Own Nodes (BYON)")
      .description("A cloud provider implementation that allocates " +
          "instances from a static list of hosts")
      .configurationProperties(Collections.<ConfigurationProperty>emptyList())
      .credentialsProviderMetadata(
          new SimpleCredentialsProviderMetadata(Collections.<ConfigurationProperty>emptyList()))
      .resourceProviderMetadata(RESOURCE_PROVIDER_METADATA)
      .build();

  public BYONCloudProvider(LocalizationContext rootLocalizationContext) {
    super(METADATA, rootLocalizationContext);
  }

  @Override
  public ResourceProvider createResourceProvider(
      String resourceProviderId, Configured configuration) {

    if (BYONComputeProvider.METADATA.getId().equals(resourceProviderId)) {
      return new BYONComputeProvider(configuration, getLocalizationContext());
    }

    throw new NoSuchElementException(
        String.format("Invalid resource provider ID: %s", resourceProviderId));
  }
}
