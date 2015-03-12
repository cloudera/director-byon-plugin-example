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

import static com.cloudera.director.byon.util.Strings.countOccurrencesOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility functions for working with host group expressions.
 */
public final class HostGroups {

  static final String GROUP_SEPARATOR = ",";

  static final Pattern CURLY_BRACKET_PATTERN = Pattern.compile("^.*(\\{(\\d+)(\\.\\.|-)(\\d+)\\}).*$");
  static final Pattern SQUARE_BRACKET_PATTERN = Pattern.compile("^.*(\\[(\\d+)(\\.\\.|-)(\\d+)\\]).*$");
  static final int GROUP_COUNT_ON_PATTERN_MATCH = 4;

  private HostGroups() {
  }

  /**
   * Expand one or multiple comma separated contiguous groups of hosts as single hosts.
   * <p/>
   * Example input:
   * <p/>
   * <pre>
   * cluster-[1-5].example.com, 192.168.0.{4..10}, example.com
   * </pre>
   */
  public static String[] expand(String hostGroupExpression) {
    List<String> parts = new ArrayList<String>();
    for (String group : Strings.splitTrimOmitEmpty(hostGroupExpression, GROUP_SEPARATOR)) {
      parts.addAll(Arrays.asList(expandSingleContiguousGroup(group)));
    }
    return parts.toArray(new String[parts.size()]);
  }

  /**
   * Expand a single contiguous group as a list of hosts.
   * <p/>
   * Example inputs:
   * <p/>
   * <pre>
   * cluster-[1-5].example.com
   * cluster-{1..5}.example.com
   * cluster-{01..05}.example.com
   * 192.168.0.[4-10]
   * </pre>
   */
  private static String[] expandSingleContiguousGroup(String expression) {
    if (!hasASingleRangeOrNoneWithMatchingBrackets(expression)) {
      throw new IllegalArgumentException("A host group expression can " +
          "contain a single range enclosed within [ ] or { } or none.");
    }

    Matcher matcher = tryPatterns(expression, CURLY_BRACKET_PATTERN, SQUARE_BRACKET_PATTERN);
    if (matcher != null && matcher.groupCount() == GROUP_COUNT_ON_PATTERN_MATCH) {

      String block = matcher.group(1);
      int paddingSize = matcher.group(2).length();

      int begin = Integer.parseInt(matcher.group(2));
      int end = Integer.parseInt(matcher.group(4));
      if (begin >= end) {
        throw new IllegalArgumentException("Invalid range " + block);
      }

      String result[] = new String[end - begin + 1];
      for (int i = begin; i <= end; i++) {
        result[i - begin] = expression.replace(block,
            String.format("%0" + paddingSize + "d", i));
      }
      return result;

    } else {
      if (Strings.containsAny(expression, "{", "}", "[", "]")) {
        throw new IllegalArgumentException("Invalid range in host group expression: " + expression);
      }

      if (expression.isEmpty()) {
        return new String[]{};  // no hosts in this group
      }

      // assuming the group expression is actually a single host
      return new String[]{expression};
    }
  }

  /**
   * A naive check for matching brackets.
   * <p/>
   * TODO improve to use a stack based implementation if needed
   */
  private static boolean hasASingleRangeOrNoneWithMatchingBrackets(String expression) {
    int numberOfRanges = countOccurrencesOf(expression, '[') + countOccurrencesOf(expression, '{');
    return countOccurrencesOf(expression, '[') == countOccurrencesOf(expression, ']') &&
        countOccurrencesOf(expression, '{') == countOccurrencesOf(expression, '}') &&
        (numberOfRanges == 1 || numberOfRanges == 0);
  }

  /**
   * Try multiple patterns on a string and return the first one matching or absent.
   */
  private static Matcher tryPatterns(String target, Pattern... patterns) {
    for (Pattern candidate : patterns) {
      Matcher result = candidate.matcher(target);
      if (result.matches()) {
        return result;
      }
    }
    return null;
  }
}
