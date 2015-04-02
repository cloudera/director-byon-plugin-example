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

import com.cloudera.director.spi.v1.model.ConfigurationProperty;

import java.util.Locale;

/**
 * An enum of properties required for building credentials
 */
public enum BYONCredentialsProviderConfigurationProperty implements ConfigurationProperty {

  USERNAME("username", "BYON cloud provider username", false),

  PASSWORD("password", "BYON cloud provider password", true);

  /// TODO: think about how we can remove all the boilerplate code below

  private final String configKey;
  private final String description;
  private final boolean sensitive;

  private BYONCredentialsProviderConfigurationProperty(String configKey,
      String description, boolean sensitive) {
    this.configKey = configKey;
    this.description = description;
    this.sensitive = sensitive;
  }

  @Override
  public String getConfigKey() {
    return configKey;
  }

  @Override
  public boolean isRequired() {
    return true;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

  @Override
  public String getDescription(Locale locale) {
    return description;
  }

  @Override
  public String getMissingValueErrorMessage() {
    return "Configuration property not found: " + name();
  }

  @Override
  public boolean isSensitive() {
    return sensitive;
  }
}
