package com.dushyant.util;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <pre>
 *     Util.measureTime("FIRST");
 *     sleep("test", 2);
 *     final long first = Util.measureTimeEnd("FIRST");
 *     System.out.println("first = " + first);
 *
 *     final Duration x = Util.measureTime(() -> sleep("x", 2));
 *     System.out.println("x = " + x);
 *
 *     final Integer integer = Util.measureTime("WITHSUPPLIER", () -> {
 *       sleep("WITHSUPPLIER", 3);
 *       return 3;
 *     });
 *     System.out.println("integer = " + integer);
 *     --------
 *     Util.<String>use(connection, Util::longProcess);
 *     --------
 * </pre>
 * Utils
 */
public class Util {
  /**
   * noop method, mostly using in ignored printstack to avoid warnings
   */
  @SuppressWarnings({"unused", "RedundantSuppression"})
  public static final Consumer<Object> NOOP = whatever -> {
  };
  private static final Map<String, Instant> DURATION_MAP = Collections.synchronizedMap(new WeakHashMap<>());

  public Util() {
    System.out.println("Util.Util");
  }

  /**
   * @param tag      TAG
   * @param runnable Runnable interface
   * @return Duration
   */
  public static Duration measureTime(String tag, Runnable runnable) {
    measureTime(tag);
    runnable.run();
    return measureTimeEndDuration(tag);
  }

  /**
   * @param runnable Runnable interface
   * @return Duration
   */
  public static Duration measureTime(Runnable runnable) {
    return measureTime(System.nanoTime() + "", runnable);
  }

  /**
   * @param supplier supplier so it returns something
   * @param <R>      generics, so it can returns whatever you want <T>
   * @return R
   */
  public static <R> R measureTime(Supplier<R> supplier) {
    return Util.measureTime(System.nanoTime() + "", supplier);
  }

  /**
   * @param tag      TAG
   * @param supplier supplies R
   * @param <R>      what should it supply
   * @return R
   */
  public static <R> R measureTime(String tag, Supplier<R> supplier) {
    measureTime(tag);
    final R r = supplier.get();
    Util.measureTimeEndDuration(tag);
    return r;
  }

  /**
   * @param tag TAG
   */
  public static void measureTime(String tag) {
    DURATION_MAP.computeIfAbsent(tag, s -> Instant.now());
  }

  /**
   * @param tag TAG
   * @return second times
   */
  public static long measureTimeEnd(String tag) {
    return measureTimeEndDuration(tag).getSeconds();
  }

  /**
   * A string representation of this duration using ISO-8601 seconds
   * based representation, such as {@code PT8H6M12.345S}.
   * <p>
   * The format of the returned string will be {@code PTnHnMnS}, where n is
   * the relevant hours, minutes or seconds part of the duration.
   * Any fractional seconds are placed after a decimal point i the seconds section.
   * If a section has a zero value, it is omitted.
   * The hours, minutes and seconds will all have the same sign.
   * <p>
   * Examples:
   * <pre>
   *    "20.345 seconds"                 -- "PT20.345S
   *    "15 minutes" (15 * 60 seconds)   -- "PT15M"
   *    "10 hours" (10 * 3600 seconds)   -- "PT10H"
   *    "2 days" (2 * 86400 seconds)     -- "PT48H"
   * </pre>
   * Note that multiples of 24 hours are not output as days to avoid confusion
   * with {@code Period}.
   *
   * @return an ISO-8601 representation of this duration, not null
   */
  public static Duration measureTimeEndDuration(String tag) {
    Duration between = Duration.ofNanos(0);
    try {
      if (tag == null) {
        return between;
      }
      final Instant remove = DURATION_MAP.remove(tag);
      between = Duration.between(remove == null ? Instant.now() : remove, Instant.now());
    } catch (Throwable ignored) {
    }
    final long seconds = between.getSeconds();
    System.out.printf("Util.measureTime# Time taken for tag:%s %ds or %dns%n", tag, seconds, between.getNano());
    return between;
  }

  /**
   * @param tag TAG
   * @param s   sleep time
   */
  public static void sleep(String tag, long s) {
    boolean interrupt = false;
    try {
      TimeUnit.SECONDS.sleep(s);
    } catch (InterruptedException e) {
      interrupt = true;
      System.err.println(MessageFormat.format("{0} with {1}", tag, s));
    } finally {
      if (interrupt) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /**
   * @param function  Function
   * @param closeable Closeable
   * @param <R>       R
   * @return returns R
   */
  public static <R> R use(AutoCloseable closeable, Function<AutoCloseable, R> function) {
    Objects.requireNonNull(closeable);
    return function.apply(closeable);
  }
}
