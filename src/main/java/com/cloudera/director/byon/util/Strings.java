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

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions for working with strings.
 */
public final class Strings {

  private Strings() {
  }

  /**
   * Count the number of occurrences for a character in a string.
   *
   * @return the number of times the character is found in the input string
   */
  public static int countOccurrencesOf(String input, char candidate) {
    if (input == null) {
      throw new NullPointerException("input is null");
    }

    int count = 0;
    for (char element : input.toCharArray()) {
      if (element == candidate) {
        count++;
      }
    }
    return count;
  }

  /**
   * Split the input string using the separator, trim the parts and omit any empty strings.
   *
   * @param input     an arbitrary input string (not null)
   * @param separator regular expression used for splitting
   * @return a list of strings or empty
   */
  public static List<String> splitTrimOmitEmpty(String input, String separator) {
    if (input == null) {
      throw new NullPointerException("input is null");
    }
    if (separator == null) {
      throw new NullPointerException("separator is null");
    }

    String[] parts = input.split(separator);

    List<String> result = new ArrayList<String>(parts.length);
    for (String candidate : parts) {
      String trimmed = candidate.trim();
      if (!trimmed.isEmpty()) {
        result.add(trimmed);
      }
    }

    return result;
  }

  /**
   * Check the if the input string contains any of the fragments.
   *
   * @param input     arbitrary input string (not null)
   * @param fragments list of at least of fragment to search for
   * @return whether the input contains one of the fragments
   */
  public static boolean containsAny(String input, String... fragments) {
    if (input == null) {
      throw new NullPointerException("input is null");
    }
    if (fragments.length == 0) {
      throw new IllegalArgumentException("At least one fragment needed");
    }

    for (String candidate : fragments) {
      if (input.contains(candidate)) {
        return true;
      }
    }
    return false;
  }
}
