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

import static com.cloudera.director.byon.compute.BYONComputeProviderConfigurationPropertyToken.HOSTS;
import static com.cloudera.director.spi.v2.compute.ComputeInstanceTemplate.ComputeInstanceTemplateConfigurationPropertyToken.IMAGE;
import static com.cloudera.director.spi.v2.compute.ComputeInstanceTemplate.ComputeInstanceTemplateConfigurationPropertyToken.TYPE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import com.cloudera.director.spi.v2.compute.ComputeInstance;
import com.cloudera.director.spi.v2.compute.ComputeInstanceTemplate;
import com.cloudera.director.spi.v2.compute.ComputeProvider;
import com.cloudera.director.spi.v2.model.ConfigurationProperty;
import com.cloudera.director.spi.v2.model.util.SimpleConfiguration;
import com.cloudera.director.spi.v2.provider.CloudProvider;
import com.cloudera.director.spi.v2.provider.CloudProviderMetadata;
import com.cloudera.director.spi.v2.provider.Launcher;
import com.cloudera.director.spi.v2.provider.ResourceProviderMetadata;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

public class BYONTest {

  @Test
  public void testFullCycle() throws InterruptedException {

    // After a plugin is discovered and validated we get an instance of the Launcher

    Launcher launcher = new BYONLauncher();

    // We register all the available providers based on metadata

    assertEquals(1, launcher.getCloudProviderMetadata().size());
    CloudProviderMetadata metadata = launcher.getCloudProviderMetadata().get(0);

    assertEquals(BYONCloudProvider.ID, metadata.getId());

    // During environment configuration we ask the user for the following properties

    System.out.println("Configurations required for credentials:");
    for (ConfigurationProperty property :
        metadata.getCredentialsProviderMetadata().getCredentialsConfigurationProperties()) {
      System.out.println(property);
    }

    System.out.println("Other provider level configurations:");
    for (ConfigurationProperty property :
        metadata.getProviderConfigurationProperties()) {
      System.out.println(property);
    }

    // In order to create a cloud provider we need to configure credentials
    // (we expect them to be eagerly validation on cloud provider creation)

    CloudProvider provider = launcher.createCloudProvider(
        BYONCloudProvider.ID,
        new SimpleConfiguration(Collections.<String, String>emptyMap()),
        Locale.getDefault());

    assertNotNull(provider);

    // Get the provider for compute instances

    ResourceProviderMetadata computeMetadata = metadata.getResourceProviderMetadata("compute");

    System.out.println("Configurations required for 'compute' resource provider:");
    for (ConfigurationProperty property :
        computeMetadata.getProviderConfigurationProperties()) {
      System.out.println(property);
    }

    Map<String, String> computeConfig = new HashMap<String, String>();
    computeConfig.put(HOSTS.unwrap().getConfigKey(), "10.0.1.5, 10.0.1.10");

    ComputeProvider<ComputeInstance<ComputeInstanceTemplate>, ComputeInstanceTemplate> compute =
        (ComputeProvider<ComputeInstance<ComputeInstanceTemplate>, ComputeInstanceTemplate>)
            provider.createResourceProvider("compute", new SimpleConfiguration(computeConfig));

    // Prepare a resource template

    System.out.println("Configurations required for template:");
    for (ConfigurationProperty property :
        computeMetadata.getResourceTemplateConfigurationProperties()) {
      System.out.println(property);
    }

    Map<String, String> templateConfig = new HashMap<String, String>();
    templateConfig.put(IMAGE.unwrap().getConfigKey(), "image-123");
    templateConfig.put(TYPE.unwrap().getConfigKey(), "multi-cpu-instance");

    ComputeInstanceTemplate template = compute.createResourceTemplate("template-1",
        new SimpleConfiguration(templateConfig), new HashMap<String, String>());

    assertNotNull(template);

    // Use the template to create one resource

    List<String> instanceIds = Arrays.asList(UUID.randomUUID().toString());

    compute.allocate(template, instanceIds, 1);
    Collection<? extends ComputeInstance<ComputeInstanceTemplate>> instances = compute.find(template, instanceIds);

    assertEquals(1, instances.size());

    ComputeInstance instance = instances.iterator().next();
    assertEquals(instanceIds.get(0), instance.getId());

    // TODO loop and check instance state

    // Run a find by ID

    Collection<? extends ComputeInstance<ComputeInstanceTemplate>> found = compute.find(template, instanceIds);
    assertEquals(1, found.size());

    // Delete the resources

    compute.delete(template, instanceIds);

    // TODO loop until deleted

  }
}
