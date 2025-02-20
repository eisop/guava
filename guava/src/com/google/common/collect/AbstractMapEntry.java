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
import com.google.common.base.Objects;
import java.util.Map.Entry;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.checker.pico.qual.Mutable;
import org.checkerframework.checker.pico.qual.Readonly;
import org.checkerframework.checker.pico.qual.ReceiverDependentMutable;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.framework.qual.AnnotatedFor;

/**
 * Implementation of the {@code equals}, {@code hashCode}, and {@code toString} methods of {@code
 * Entry}.
 *
 * @author Jared Levy
 */
@AnnotatedFor({"nullness"})
@GwtCompatible
@ElementTypesAreNonnullByDefault
@ReceiverDependentMutable abstract class AbstractMapEntry<K extends @Nullable Object, V extends @Readonly @Nullable Object>
    implements Entry<K, V> {

  @Pure
  @Override
  @ParametricNullness
  public abstract K getKey();

  @Pure
  @Override
  @ParametricNullness
  public abstract V getValue();

  @Override
  @ParametricNullness
  public V setValue(@Mutable AbstractMapEntry<K, V> this, @ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }

  @Pure
  @Override
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof Entry) {
      Entry<?, ?> that = (Entry<?, ?>) object;
      return Objects.equal(this.getKey(), that.getKey())
          && Objects.equal(this.getValue(), that.getValue());
    }
    return false;
  }

  @Pure
  @Override
  public int hashCode(@UnknownSignedness AbstractMapEntry<K, V> this) {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }

  /** Returns a string representation of the form {@code {key}={value}}. */
  @Pure
  @Override
  public String toString() {
    return getKey() + "=" + getValue();
  }
}
