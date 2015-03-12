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

package com.cloudera.director.byon.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HostGroupsTest {

  @Test
  public void testExpandGroup() {
    String[] hosts = HostGroups.expand("mycluster-{4..100}.example.com");

    assertThat(hosts).hasSize(97)
        .contains("mycluster-4.example.com")
        .contains("mycluster-50.example.com")
        .contains("mycluster-100.example.com");
  }

  @Test
  public void testExpandGroupWithLeadingZeros() {
    String[] hosts = HostGroups.expand("mycluster-{004..100}.example.com");

    assertThat(hosts).hasSize(97)
        .contains("mycluster-004.example.com")
        .contains("mycluster-010.example.com")
        .contains("mycluster-100.example.com");
  }

  @Test
  public void testDifferentGroupPrefixAndSufix() {
    String[] expected = new String[]{"my-009.example.com", "my-010.example.com", "my-011.example.com"};

    assertExpandsTo("my-[009-011].example.com", expected);
    assertExpandsTo("my-{009-011}.example.com", expected);
  }

  @Test
  public void testExpandSingleHost() {
    assertExpandsTo("single-host.example.com", "single-host.example.com");
  }

  @Test
  public void testExpandWithEmptyString() {
    assertExpandsTo("" /* empty array */);
  }

  @Test
  public void testEmptyStringIsTrimmed() {
    assertExpandsTo("   " /* empty array */);
  }

  @Test
  public void testExpandIpAddress() {
    assertExpandsTo("192.168.0.{5-6}", "192.168.0.5", "192.168.0.6");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testWithTwoRangesInTheSameGroupAreNotAllowed() {
    HostGroups.expand("my-{1..2}-[5-6].example.com");
  }

  @Test
  public void testExpandMultipleGroupsWithRanges() {
    assertExpandsTo("a-{1..2}.x.com,b-{1..2}.x.com  , c.x.com,,",
        "a-1.x.com", "a-2.x.com", "b-1.x.com", "b-2.x.com", "c.x.com");
  }

  @Test
  public void testExpandListOfCommaSeparatedHostnames() {
    assertExpandsTo("a.x.com, b.x.com,c.x.com", "a.x.com", "b.x.com", "c.x.com");
  }

  @Test
  public void testExpandMultipleGroupsOfIpAddressesAndHostnames() {
    assertExpandsTo("192.168.0.{1-2}, a-{1..2}.x.com, c.com,,",
        "192.168.0.1", "192.168.0.2", "a-1.x.com", "a-2.x.com", "c.com");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMixingDifferentRangeBracketsIsNotRecognizedAsAPattern() {
    HostGroups.expand("192.168.0.{1-3]");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReversedBrackets() {
    HostGroups.expand("192.168.0.]1..3[");
  }

  public void assertExpandsTo(String pattern, String... hosts) {
    assertThat(HostGroups.expand(pattern)).isEqualTo(hosts);
  }

}
