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

import com.cloudera.director.spi.v1.model.Configured;
import com.cloudera.director.spi.v1.provider.CloudProvider;
import com.cloudera.director.spi.v1.provider.util.AbstractLauncher;

import java.util.Collections;
import java.util.Locale;
import java.util.NoSuchElementException;

@SuppressWarnings("PMD.UnusedFormalParameter")
public class BYONLauncher extends AbstractLauncher {

  public BYONLauncher() {
    super(Collections.singletonList(BYONCloudProvider.METADATA), null);
  }

  @Override
  public CloudProvider createCloudProvider(String cloudProviderId, Configured ignored, Locale locale) {
    if (!BYONCloudProvider.ID.equals(cloudProviderId)) {
      throw new NoSuchElementException("Cloud provider not found: " + cloudProviderId);
    }

    return new BYONCloudProvider(getLocalizationContext(locale));
  }
}
