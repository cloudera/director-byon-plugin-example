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
public enum BYONComputeInstanceTemplateConfigurationPropertyToken
    implements com.cloudera.director.spi.v1.model.ConfigurationPropertyToken {

  PREFERRED_HOSTS(new SimpleConfigurationPropertyBuilder()
      .configKey("preferredHosts")
      .name("Preferred hosts")
      .defaultDescription("A comma separated subset of host group " +
          "expressions that should be preferred for new instances of this template (best effort)")
      .build());

  private final ConfigurationProperty configurationProperty;

  private BYONComputeInstanceTemplateConfigurationPropertyToken(ConfigurationProperty configurationProperty) {
    this.configurationProperty = configurationProperty;
  }

  @Override
  public ConfigurationProperty unwrap() {
    return configurationProperty;
  }
}
