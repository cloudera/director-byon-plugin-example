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
import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;

public class StringsTests {

  @Test
  public void testCountOccurrencesOf_Boundaries() {
    assertThat(Strings.countOccurrencesOf("a", 'a')).isEqualTo(1);
    assertThat(Strings.countOccurrencesOf("abca", 'a')).isEqualTo(2);
    assertThat(Strings.countOccurrencesOf("aaaa", 'a')).isEqualTo(4);
  }

  @Test
  public void testCountOccurrencesOf_NotFound() {
    assertThat(Strings.countOccurrencesOf("test", 'a')).isEqualTo(0);
    assertThat(Strings.countOccurrencesOf("", 'x')).isEqualTo(0);
  }

  @Test(expected = NullPointerException.class)
  public void testCountOccurrencesOf_NullNotAccepted() {
    Strings.countOccurrencesOf(null, 'x');
  }

  @Test
  public void testSplitTrimOmitEmpty() {
    assertThat(Strings.splitTrimOmitEmpty("a, b,c ,,", ","))
        .containsExactly("a", "b", "c");
  }

  @Test
  public void testSplitTrimOmitEmpty_EmptyInput() {
    assertThat(Strings.splitTrimOmitEmpty("", ",")).isEmpty();
    assertThat(Strings.splitTrimOmitEmpty(",,,,", ",")).isEmpty();
  }

  @Test
  public void testSplitTrimOmitEmpty_NullNotAccepted() {
    try {
      Strings.splitTrimOmitEmpty(null, ",");
      fail("Expected to get a NPE on null input");

    } catch (NullPointerException expected) {
      assertThat(expected).hasMessageContaining("input");
    }

    try {
      Strings.splitTrimOmitEmpty("A", null);
      fail("Expected to get a NPE on a null separator");

    } catch (NullPointerException expected) {
      assertThat(expected).hasMessageContaining("separator");
    }
  }

  @Test
  public void testContainsAny() {
    assertThat(Strings.containsAny("1234", "1")).isTrue();
    assertThat(Strings.containsAny("2341", "1")).isTrue();
    assertThat(Strings.containsAny("1234", "5", "6", "1")).isTrue();
  }

  @Test(expected = NullPointerException.class)
  public void testContainsAny_NullNotAccepted() {
    Strings.containsAny(null, "a");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testContainsAny_AtLeastOneFragmentRequired() {
    Strings.containsAny("abc");
  }
}
