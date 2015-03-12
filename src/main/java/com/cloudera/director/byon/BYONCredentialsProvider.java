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

import static com.cloudera.director.byon.BYONCredentialsProviderConfigurationProperty.PASSWORD;
import static com.cloudera.director.byon.BYONCredentialsProviderConfigurationProperty.USERNAME;

import com.cloudera.director.byon.internal.UsernamePassword;
import com.cloudera.director.spi.v1.model.ConfigurationProperty;
import com.cloudera.director.spi.v1.model.Configured;
import com.cloudera.director.spi.v1.provider.CredentialsProvider;
import com.cloudera.director.spi.v1.provider.CredentialsProviderMetadata;
import com.cloudera.director.spi.v1.provider.util.SimpleCredentialsProviderMetadata;
import com.cloudera.director.spi.v1.util.ConfigurationPropertiesUtil;

import java.util.List;

/**
 * Convert a configuration to a cloud provider specific credentials object
 */
public class BYONCredentialsProvider implements CredentialsProvider<UsernamePassword> {

  private static final List<ConfigurationProperty> CONFIGURATION_PROPERTIES =
      ConfigurationPropertiesUtil.asList(BYONCredentialsProviderConfigurationProperty.values());

  public static CredentialsProviderMetadata METADATA =
      new SimpleCredentialsProviderMetadata(CONFIGURATION_PROPERTIES);

  @Override
  public CredentialsProviderMetadata getMetadata() {
    return METADATA;
  }

  @Override
  public UsernamePassword createCredentials(Configured configuration) {
    return new UsernamePassword(
        configuration.getConfigurationValue(USERNAME),
        configuration.getConfigurationValue(PASSWORD)
    );
  }
}
