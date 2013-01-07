// @file     Functor.java
// @author   Mac Radigan

package org.radigan.naomi.wumpus.service;

import java.util.List;
import java.util.Map;

public interface Functor {
  public String getNamespace();
  public String getName();
  public List<String> getProducts();
  public List<String> getParameters();
  public List<String> getDependencies();
  public void call(Map<String,String> parameters) throws RuntimeException;
}

// *EOF*
