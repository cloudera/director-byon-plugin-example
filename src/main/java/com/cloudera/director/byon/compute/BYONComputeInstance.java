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

import com.cloudera.director.spi.v2.compute.util.AbstractComputeInstance;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class BYONComputeInstance
    extends AbstractComputeInstance<BYONComputeInstanceTemplate, Void> {

  public static final Type TYPE = new ResourceType("BYONComputeInstance");

  protected BYONComputeInstance(BYONComputeInstanceTemplate template,
      String identifier, InetAddress privateIpAddress) {
    super(template, identifier, privateIpAddress);
  }

  @Override
  public Type getType() {
    return TYPE;
  }

  @Override
  public Map<String, String> getProperties() {
    return new HashMap<String, String>();
  }
}
