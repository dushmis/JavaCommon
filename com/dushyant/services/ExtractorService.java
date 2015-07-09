package com.dushyant.services;

import java.util.List;

/**
 * @param <T>
 */
public abstract class ExtractorService<T extends DBTable> {
  public abstract List<T> extract() throws Exception;

}
