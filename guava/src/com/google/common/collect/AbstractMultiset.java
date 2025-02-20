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

import static com.google.common.collect.Multisets.setCountImpl;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.WeakOuter;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.pico.qual.Assignable;
import org.checkerframework.checker.pico.qual.Mutable;
import org.checkerframework.checker.pico.qual.Readonly;
import org.checkerframework.checker.pico.qual.ReceiverDependentMutable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.checkerframework.framework.qual.AnnotatedFor;

/**
 * This class provides a skeletal implementation of the {@link Multiset} interface. A new multiset
 * implementation can be created easily by extending this class and implementing the {@link
 * Multiset#entrySet()} method, plus optionally overriding {@link #add(Object, int)} and {@link
 * #remove(Object, int)} to enable modifications to the multiset.
 *
 * <p>The {@link #count} and {@link #size} implementations all iterate across the set returned by
 * {@link Multiset#entrySet()}, as do many methods acting on the set returned by {@link
 * #elementSet()}. Override those methods for better performance.
 *
 * @author Kevin Bourrillion
 * @author Louis Wasserman
 */
@AnnotatedFor({"nullness"})
@GwtCompatible
@ElementTypesAreNonnullByDefault
@ReceiverDependentMutable abstract class AbstractMultiset<E extends @Nullable @Readonly Object> extends AbstractCollection<E>
    implements Multiset<E> {
  // Query Operations

  @Pure
  @Override
  public boolean isEmpty() {
    return entrySet().isEmpty();
  }

  @Pure
  @Override
  public boolean contains(@CheckForNull @UnknownSignedness @Readonly Object element) {
    return count(element) > 0;
  }

  // Modification Operations
  @CanIgnoreReturnValue
  @Override
  public final boolean add(@Mutable AbstractMultiset<E> this, @ParametricNullness E element) {
    add(element, 1);
    return true;
  }

  @CanIgnoreReturnValue
  @Override
  public int add(@Mutable AbstractMultiset<E> this, @ParametricNullness E element, int occurrences) {
    throw new UnsupportedOperationException();
  }

  @CanIgnoreReturnValue
  @Override
  public final boolean remove(@Mutable AbstractMultiset<E> this, @CheckForNull @UnknownSignedness Object element) {
    return remove(element, 1) > 0;
  }

  @CanIgnoreReturnValue
  @Override
  public int remove(@Mutable AbstractMultiset<E> this, @CheckForNull Object element, int occurrences) {
    throw new UnsupportedOperationException();
  }

  @CanIgnoreReturnValue
  @Override
  public int setCount(@Mutable AbstractMultiset<E> this, @ParametricNullness E element, int count) {
    return setCountImpl(this, element, count);
  }

  @CanIgnoreReturnValue
  @Override
  public boolean setCount(@Mutable AbstractMultiset<E> this, @ParametricNullness E element, int oldCount, int newCount) {
    return setCountImpl(this, element, oldCount, newCount);
  }

  // Bulk Operations

  /**
   * {@inheritDoc}
   *
   * <p>This implementation is highly efficient when {@code elementsToAdd} is itself a {@link
   * Multiset}.
   */
  @CanIgnoreReturnValue
  @Override
  public final boolean addAll(@Mutable AbstractMultiset<E> this, Collection<? extends E> elementsToAdd) {
    return Multisets.addAllImpl(this, elementsToAdd);
  }

  @CanIgnoreReturnValue
  @Override
  public final boolean removeAll(@Mutable AbstractMultiset<E> this, Collection<?> elementsToRemove) {
    return Multisets.removeAllImpl(this, elementsToRemove);
  }

  @CanIgnoreReturnValue
  @Override
  public final boolean retainAll(@Mutable AbstractMultiset<E> this, Collection<?> elementsToRetain) {
    return Multisets.retainAllImpl(this, elementsToRetain);
  }

  @Override
  public abstract void clear(@Mutable AbstractMultiset<E> this);

  // Views

  @LazyInit @CheckForNull private transient @Assignable Set<E> elementSet;

  @SideEffectFree
  @Override
  public @ReceiverDependentMutable Set<E> elementSet() {
    Set<E> result = elementSet;
    if (result == null) {
      elementSet = result = createElementSet();
    }
    return result;
  }

  /**
   * Creates a new instance of this multiset's element set, which will be returned by {@link
   * #elementSet()}.
   */
  @ReceiverDependentMutable Set<E> createElementSet() {
    return new @ReceiverDependentMutable ElementSet();
  }

  @WeakOuter
  @ReceiverDependentMutable class ElementSet extends Multisets.ElementSet<E> {
    @Override
    @ReceiverDependentMutable Multiset<E> multiset() {
      return AbstractMultiset.this;
    }

    @Override
    public @ReceiverDependentMutable Iterator<E> iterator() {
      return elementIterator();
    }
  }

  abstract @ReceiverDependentMutable Iterator<E> elementIterator();

  @LazyInit @CheckForNull private transient @Assignable Set<Entry<E>> entrySet;

  @SideEffectFree
  @Override
  public Set<Entry<E>> entrySet() {
    Set<Entry<E>> result = entrySet;
    if (result == null) {
      entrySet = result = createEntrySet();
    }
    return result;
  }

  @WeakOuter
  @ReceiverDependentMutable class EntrySet extends Multisets.EntrySet<E> {
    @Override
    Multiset<E> multiset() {
      return AbstractMultiset.this;
    }

    @Override
    public Iterator<Entry<E>> iterator() {
      return entryIterator();
    }

    @Override
    public @NonNegative int size() {
      return distinctElements();
    }
  }

  Set<Entry<E>> createEntrySet() {
    return new EntrySet();
  }

  abstract Iterator<Entry<E>> entryIterator();

  abstract int distinctElements();

  // Object methods

  /**
   * {@inheritDoc}
   *
   * <p>This implementation returns {@code true} if {@code object} is a multiset of the same size
   * and if, for each element, the two multisets have the same count.
   */
  @Pure
  @Override
  public final boolean equals(@Readonly AbstractMultiset<E> this, @CheckForNull @UnknownSignedness Object object) {
    return Multisets.equalsImpl(this, object);
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation returns the hash code of {@link Multiset#entrySet()}.
   */
  @Pure
  @Override
  public final int hashCode(@Readonly @UnknownSignedness AbstractMultiset<E> this) {
    return entrySet().hashCode();
  }

  /**
   * {@inheritDoc}
   *
   * <p>This implementation returns the result of invoking {@code toString} on {@link
   * Multiset#entrySet()}.
   */
  @Pure
  @Override
  public final String toString(@Readonly AbstractMultiset<E> this) {
    return entrySet().toString();
  }
}
