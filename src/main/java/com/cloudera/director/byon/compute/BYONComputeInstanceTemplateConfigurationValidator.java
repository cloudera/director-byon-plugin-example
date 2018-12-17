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

import com.cloudera.director.spi.v2.model.ConfigurationValidator;
import com.cloudera.director.spi.v2.model.Configured;
import com.cloudera.director.spi.v2.model.LocalizationContext;
import com.cloudera.director.spi.v2.model.exception.PluginExceptionConditionAccumulator;
import com.cloudera.director.spi.v2.util.Preconditions;

/**
 * Validates BYON compute instance template configuration.
 */
@SuppressWarnings({"unused", "FieldCanBeLocal" })
public class BYONComputeInstanceTemplateConfigurationValidator implements ConfigurationValidator {

  /**
   * The BYON compute provider.
   */
  private final BYONComputeProvider provider;

  /**
   * Creates an BYON compute instance template configuration validator with the specified
   * parameters.
   *
   * @param provider the BYON compute provider
   */
  public BYONComputeInstanceTemplateConfigurationValidator(BYONComputeProvider provider) {
    this.provider = Preconditions.checkNotNull(provider, "provider");
  }

  @Override
  public void validate(String name, Configured configuration, PluginExceptionConditionAccumulator accumulator,
      LocalizationContext localizationContext) {

    // TODO add validations
  }
}
