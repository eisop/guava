/*
 * Copyright (C) 2011 The Guava Authors
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

package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ForwardingObject;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signedness.qual.UnknownSignedness;

/**
 * An executor service which forwards all its method calls to another executor service. Subclasses
 * should override one or more methods to modify the behavior of the backing executor service as
 * desired per the <a href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 *
 * @author Kurt Alfred Kluever
 * @since 10.0
 */
@CanIgnoreReturnValue // TODO(cpovirk): Consider being more strict.
@GwtIncompatible
@ElementTypesAreNonnullByDefault
public abstract class ForwardingExecutorService extends ForwardingObject
    implements ExecutorService {
  /** Constructor for use by subclasses. */
  protected ForwardingExecutorService() {}

  @Override
  protected abstract ExecutorService delegate();

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return delegate().awaitTermination(timeout, unit);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return delegate().invokeAll(tasks);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> List<Future<T>> invokeAll(
      Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException {
    return delegate().invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> T invokeAny(Collection<? extends Callable<T>> tasks)
      throws InterruptedException, ExecutionException {
    return delegate().invokeAny(tasks);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> T invokeAny(
      Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return delegate().invokeAny(tasks, timeout, unit);
  }

  @Override
  public boolean isShutdown() {
    return delegate().isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return delegate().isTerminated();
  }

  @Override
  public void shutdown() {
    delegate().shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return delegate().shutdownNow();
  }

  @Override
  public void execute(Runnable command) {
    delegate().execute(command);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> Future<T> submit(Callable<T> task) {
    return delegate().submit(task);
  }

  @Override
  public Future<?> submit(Runnable task) {
    return delegate().submit(task);
  }

  @Override
  public <T extends @Nullable @UnknownSignedness Object> Future<T> submit(
      Runnable task, @ParametricNullness T result) {
    return delegate().submit(task, result);
  }
}
