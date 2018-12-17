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
import com.cloudera.director.spi.v2.compute.ComputeInstanceTemplate;
import com.cloudera.director.spi.v2.model.ConfigurationProperty;
import com.cloudera.director.spi.v2.model.Configured;
import com.cloudera.director.spi.v2.model.LocalizationContext;
import com.cloudera.director.spi.v2.util.ConfigurationPropertiesUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BYONComputeInstanceTemplate extends ComputeInstanceTemplate {

  /**
   * The list of configuration properties (including inherited properties).
   */
  private static final List<ConfigurationProperty> CONFIGURATION_PROPERTIES =
      ConfigurationPropertiesUtil.merge(
          ComputeInstanceTemplate.getConfigurationProperties(),
          ConfigurationPropertiesUtil.asConfigurationPropertyList(
              BYONComputeInstanceTemplateConfigurationPropertyToken.values())
      );

  public static List<ConfigurationProperty> getConfigurationProperties() {
    return CONFIGURATION_PROPERTIES;
  }

  private final List<String> preferredHosts;

  public BYONComputeInstanceTemplate(String name,
      Configured configuration, Map<String, String> tags,
      LocalizationContext providerLocalizationContext) {
    super(name, configuration, tags, providerLocalizationContext);
    LocalizationContext localizationContext = getLocalizationContext();

    String hostGroupExpressions = configuration.getConfigurationValue(
        BYONComputeInstanceTemplateConfigurationPropertyToken.PREFERRED_HOSTS, localizationContext);

    if (hostGroupExpressions != null) {
      preferredHosts = Collections.unmodifiableList(
          Arrays.asList(HostGroups.expand(hostGroupExpressions)));
    } else {
      preferredHosts = Collections.emptyList();
    }
  }

  public List<String> getPreferredHosts() {
    return preferredHosts;
  }
}
