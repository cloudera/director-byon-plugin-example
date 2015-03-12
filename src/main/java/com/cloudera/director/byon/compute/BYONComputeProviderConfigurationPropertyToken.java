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
import com.cloudera.director.spi.v1.model.util.SimpleConfigurationPropertyBuilder;

// Fully qualifying class name due to compiler bug
public enum BYONComputeProviderConfigurationPropertyToken
    implements com.cloudera.director.spi.v1.model.ConfigurationPropertyToken {

  /**
   * @see com.cloudera.director.byon.util.HostGroups#expand(String)
   */
  HOSTS(new SimpleConfigurationPropertyBuilder()
      .configKey("hosts")
      .name("Hosts")
      .required(true)
      .defaultDescription("A comma separated list of host group patterns to be " +
          "used for allocations. On termination allocated hosts are not returned to the pool.")
      .build());

  /**
   * The configuration property.
   */
  private final ConfigurationProperty configurationProperty;

  private BYONComputeProviderConfigurationPropertyToken(ConfigurationProperty configurationProperty) {
    this.configurationProperty = configurationProperty;
  }

  @Override
  public ConfigurationProperty unwrap() {
    return configurationProperty;
  }
}
