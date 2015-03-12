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

import com.cloudera.director.spi.v1.model.ConfigurationProperty;

import java.util.Locale;

public enum BYONComputeInstanceTemplateConfigurationProperty implements ConfigurationProperty {

  NETWORK("network", false, null, "Network identifier (not used)");

  // TODO: think about how we can remove all the boilerplate code bellow

  private final String configKey;
  private final boolean required;
  private final String defaultValue;
  private final String description;

  private BYONComputeInstanceTemplateConfigurationProperty(String configKey, boolean required,
      String defaultValue, String description) {
    this.configKey = configKey;
    this.required = required;
    this.defaultValue = defaultValue;
    this.description = description;
  }

  @Override
  public String getConfigKey() {
    return configKey;
  }

  @Override
  public boolean isRequired() {
    return required;
  }

  @Override
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getDescription(Locale locale) {
    return description;
  }

  @Override
  public String getMissingValueErrorMessage() {
    return null;
  }

  @Override
  public boolean isSensitive() {
    return false;
  }
}
