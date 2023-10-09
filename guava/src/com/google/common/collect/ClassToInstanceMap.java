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
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotMock;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.AnnotatedFor;

/**
 * A map, each entry of which maps a Java <a href="http://tinyurl.com/2cmwkz">raw type</a> to an
 * instance of that type. In addition to implementing {@code Map}, the additional type-safe
 * operations {@link #putInstance} and {@link #getInstance} are available.
 *
 * <p>Like any other {@code Map<Class, Object>}, this map may contain entries for primitive types,
 * and a primitive type and its corresponding wrapper type may map to different values.
 *
 * <p>This class's support for {@code null} requires some explanation: From release 31.0 onward,
 * Guava specifies the nullness of its types through annotations. In the case of {@code
 * ClassToInstanceMap}, it specifies that both the key and value types are restricted to
 * non-nullable types. This specification is reasonable for <i>keys</i>, which must be non-null
 * classes. This is in contrast to the specification for <i>values</i>: Null values <i>are</i>
 * supported by the implementation {@link MutableClassToInstanceMap}, even though that
 * implementation and this interface specify otherwise. Thus, if you use a nullness checker, you can
 * safely suppress any warnings it produces when you write null values into a {@code
 * MutableClassToInstanceMap}. Just be sure to be prepared for null values when reading from it,
 * since nullness checkers will assume that vaules are non-null then, too.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/NewCollectionTypesExplained#classtoinstancemap">{@code
 * ClassToInstanceMap}</a>.
 *
 * <p>To map a generic type to an instance of that type, use {@link
 * com.google.common.reflect.TypeToInstanceMap} instead.
 *
 * @param <B> the common supertype that all entries must share; often this is simply {@link Object}
 * @author Kevin Bourrillion
 * @since 2.0
 */
@DoNotMock("Use ImmutableClassToInstanceMap or MutableClassToInstanceMap")
@GwtCompatible
@AnnotatedFor({"nullness"})
@ElementTypesAreNonnullByDefault
// If we ever support non-null projections (https://github.com/jspecify/jspecify/issues/86), we
// we might annotate this as...
// ClassToInstanceMap<B extends @Nullable Object> extends Map<Class<? extends @Nonnull B>, B>
// ...and change its methods similarly (<T extends @Nonnull B> or Class<@Nonnull T>).
public interface ClassToInstanceMap<B> extends Map<Class<? extends B>, B> {
  /**
   * Returns the value the specified class is mapped to, or {@code null} if no entry for this class
   * is present. This will only return a value that was bound to this specific class, not a value
   * that may have been bound to a subtype.
   */
  @CheckForNull
  <T extends B> T getInstance(Class<T> type);

  /**
   * Maps the specified class to the specified value. Does <i>not</i> associate this value with any
   * of the class's supertypes.
   *
   * @return the value previously associated with this class (possibly {@code null}), or {@code
   *     null} if there was no previous entry.
   */
  @CanIgnoreReturnValue
  @CheckForNull
  <T extends B> T putInstance(Class<T> type, T value);
}
