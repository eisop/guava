/*
 * Copyright (C) 2007 The Guava Authors
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

package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.qual.AnnotatedFor;

/** An ordering that treats {@code null} as greater than all other values. */
@AnnotatedFor({"nullness"})
@GwtCompatible(serializable = true)
@ElementTypesAreNonnullByDefault
final class NullsLastOrdering<T extends @Nullable Object> extends Ordering<@Nullable T>
    implements Serializable {
  final Ordering<? super T> ordering;

  NullsLastOrdering(Ordering<? super T> ordering) {
    this.ordering = ordering;
  }

  @Pure
  @Override
  public int compare(@CheckForNull T left, @CheckForNull T right) {
    if (left == right) {
      return 0;
    }
    if (left == null) {
      return LEFT_IS_GREATER;
    }
    if (right == null) {
      return RIGHT_IS_GREATER;
    }
    return ordering.compare(left, right);
  }

  @Override
  @SuppressWarnings("nullness") // should be safe, but not sure if we can avoid the warning
  public <S extends @Nullable T> Ordering<S> reverse() {
    // ordering.reverse() might be optimized, so let it do its thing
    return ordering.reverse().nullsFirst();
  }

  @Override
  public <S extends T> Ordering<@Nullable S> nullsFirst() {
    return ordering.<S>nullsFirst();
  }

  @SuppressWarnings("unchecked") // still need the right way to explain this
  @Override
  public <S extends T> Ordering<@Nullable S> nullsLast() {
    return (Ordering<@Nullable S>) this;
  }

  @Pure
  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object == this) {
      return true;
    }
    if (object instanceof NullsLastOrdering) {
      NullsLastOrdering<?> that = (NullsLastOrdering<?>) object;
      return this.ordering.equals(that.ordering);
    }
    return false;
  }

  @Pure
  @Override
  public int hashCode(@UnknownSignedness NullsLastOrdering<T> this) {
    return ordering.hashCode() ^ -921210296; // meaningless
  }

  @Pure
  @Override
  public String toString() {
    return ordering + ".nullsLast()";
  }

  private static final long serialVersionUID = 0;
}
