/*
 * Copyright (C) 2006 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.IndexOrHigh;
import org.checkerframework.checker.index.qual.LTEqLengthOf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.common.value.qual.ArrayLen;

/**
 * Utility class for converting between various ASCII case formats. Behavior is undefined for
 * non-ASCII input.
 *
 * @author Mike Bostock
 * @since 1.0
 */
@GwtCompatible
@ElementTypesAreNonnullByDefault
public enum CaseFormat {
  /** Hyphenated variable naming convention, e.g., "lower-hyphen". */
  LOWER_HYPHEN(CharMatcher.is('-'), "-") {
    @Override
    String normalizeWord(String word) {
      return Ascii.toLowerCase(word);
    }

    @Override
    String convert(CaseFormat format, String s) {
      if (format == LOWER_UNDERSCORE) {
        return s.replace('-', '_');
      }
      if (format == UPPER_UNDERSCORE) {
        return Ascii.toUpperCase(s.replace('-', '_'));
      }
      return super.convert(format, s);
    }
  },

  /** C++ variable naming convention, e.g., "lower_underscore". */
  LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
    @Override
    String normalizeWord(String word) {
      return Ascii.toLowerCase(word);
    }

    @Override
    String convert(CaseFormat format, String s) {
      if (format == LOWER_HYPHEN) {
        return s.replace('_', '-');
      }
      if (format == UPPER_UNDERSCORE) {
        return Ascii.toUpperCase(s);
      }
      return super.convert(format, s);
    }
  },

  /** Java variable naming convention, e.g., "lowerCamel". */
  LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
    @Override
    String normalizeWord(String word) {
      return firstCharOnlyToUpper(word);
    }

    @Override
    String normalizeFirstWord(String word) {
      return Ascii.toLowerCase(word);
    }
  },

  /** Java and C++ class naming convention, e.g., "UpperCamel". */
  UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
    @Override
    String normalizeWord(String word) {
      return firstCharOnlyToUpper(word);
    }
  },

  /** Java and C++ constant naming convention, e.g., "UPPER_UNDERSCORE". */
  UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
    @Override
    String normalizeWord(String word) {
      return Ascii.toUpperCase(word);
    }

    @Override
    String convert(CaseFormat format, String s) {
      if (format == LOWER_HYPHEN) {
        return Ascii.toLowerCase(s.replace('_', '-'));
      }
      if (format == LOWER_UNDERSCORE) {
        return Ascii.toLowerCase(s);
      }
      return super.convert(format, s);
    }
  };

  private final CharMatcher wordBoundary;
  private final @ArrayLen({0, 1}) String wordSeparator;

  CaseFormat(CharMatcher wordBoundary, @ArrayLen({0, 1}) String wordSeparator) {
    this.wordBoundary = wordBoundary;
    this.wordSeparator = wordSeparator;
  }

  /**
   * Converts the specified {@code String str} from this format to the specified {@code format}. A
   * "best effort" approach is taken; if {@code str} does not conform to the assumed format, then
   * the behavior of this method is undefined but we make a reasonable effort at converting anyway.
   */
  public final String to(CaseFormat format, String str) {
    checkNotNull(format);
    checkNotNull(str);
    return (format == this) ? str : convert(format, str);
  }

  /** Enum values can override for performance reasons. */
  /*
   * j = wordBoundary.indexIn ensures that j is -1 or
   * j >= 0 && j < s.length()
   */
  @SuppressWarnings("nullness:argument") // out is initialized when i == 0
  String convert(CaseFormat format, String s) {
    // deal with camel conversion
    StringBuilder out = null;
    @IndexOrHigh("s") int i = 0;
    @GTENegativeOne @LTEqLengthOf("s") int j = -1;
    while ((j = wordBoundary.indexIn(s, ++j)) != -1) {
      if (i == 0) {
        // include some extra space for separators
        out = new StringBuilder(s.length() + 4 * format.wordSeparator.length());
        out.append(format.normalizeFirstWord(s.substring(i, j)));
      } else {
        requireNonNull(out).append(format.normalizeWord(s.substring(i, j)));
      }
      out.append(format.wordSeparator);
      i = j + wordSeparator.length();
    }
    return (i == 0)
        ? format.normalizeFirstWord(s)
        : requireNonNull(out).append(format.normalizeWord(s.substring(i))).toString();
  }

  /**
   * Returns a {@code Converter} that converts strings from this format to {@code targetFormat}.
   *
   * @since 16.0
   */
  public Converter<String, String> converterTo(CaseFormat targetFormat) {
    return new StringConverter(this, targetFormat);
  }

  private static final class StringConverter extends Converter<String, String>
      implements Serializable {

    private final CaseFormat sourceFormat;
    private final CaseFormat targetFormat;

    StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat) {
      this.sourceFormat = checkNotNull(sourceFormat);
      this.targetFormat = checkNotNull(targetFormat);
    }

    @Override
    protected String doForward(String s) {
      return sourceFormat.to(targetFormat, s);
    }

    @Override
    protected String doBackward(String s) {
      return targetFormat.to(sourceFormat, s);
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof StringConverter) {
        StringConverter that = (StringConverter) object;
        return sourceFormat.equals(that.sourceFormat) && targetFormat.equals(that.targetFormat);
      }
      return false;
    }

    @Override
    public int hashCode(@UnknownSignedness StringConverter this) {
      return sourceFormat.hashCode() ^ targetFormat.hashCode();
    }

    @Override
    public String toString() {
      return sourceFormat + ".converterTo(" + targetFormat + ")";
    }

    private static final long serialVersionUID = 0L;
  }

  abstract String normalizeWord(String word);

  String normalizeFirstWord(String word) {
    return normalizeWord(word);
  }

  private static String firstCharOnlyToUpper(String word) {
    return word.isEmpty()
        ? word
        : Ascii.toUpperCase(word.charAt(0)) + Ascii.toLowerCase(word.substring(1));
  }
}
