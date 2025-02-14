/*
 * This file is part of VanillaGradle, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.gradle.vanilla.internal.util;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class AsyncUtils {

    private AsyncUtils() {
    }

    public static <T> CompletableFuture<T> failedFuture(final Throwable ex) {
        final CompletableFuture<T> ret = new CompletableFuture<>();
        ret.completeExceptionally(ex);
        return ret;
    }

    public static <T> CompletableFuture<T> failableFuture(final Callable<T> action, final Executor executor) {
        final CompletableFuture<T> result = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                result.complete(action.call());
            } catch (final Exception ex) {
                result.completeExceptionally(ex);
            }
        });
        return result;
    }

    public static <T> Supplier<T> memoizedSupplier(final Supplier<T> input) {
        return new Supplier<T>() {
            private volatile @MonotonicNonNull T value;
            @Override
            public T get() {
                if (this.value == null) {
                    synchronized (this) {
                        if (this.value == null) {
                            return this.value = input.get();
                        }
                    }
                }
                return this.value;
            }
        };
    }

}
