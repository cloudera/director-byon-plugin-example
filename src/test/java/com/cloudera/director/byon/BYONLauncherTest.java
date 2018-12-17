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

import static org.assertj.core.api.Assertions.assertThat;

import com.cloudera.director.spi.v2.model.util.SimpleConfiguration;
import com.cloudera.director.spi.v2.provider.CloudProvider;
import com.cloudera.director.spi.v2.provider.Launcher;

import java.util.Collections;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.junit.Test;

public class BYONLauncherTest {

  @Test
  public void testCreateCloudProvider() {
    Launcher launcher = new BYONLauncher();

    CloudProvider provider = launcher.createCloudProvider(BYONCloudProvider.ID,
        new SimpleConfiguration(Collections.<String, String>emptyMap()), Locale.getDefault());

    assertThat(launcher.getCloudProviderMetadata()).hasSize(1);
    assertThat(provider.getProviderMetadata().getId()).isEqualTo(BYONCloudProvider.ID);

    assertThat(provider.getProviderMetadata().getCredentialsProviderMetadata()
        .getCredentialsConfigurationProperties()).isEmpty();
  }

  @Test(expected = NoSuchElementException.class)
  public void testCreateCloudProvider_InvalidId() {
    new BYONLauncher().createCloudProvider("dummy", null, Locale.getDefault());
  }
}
