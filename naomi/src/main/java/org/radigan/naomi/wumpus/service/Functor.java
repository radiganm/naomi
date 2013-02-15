// @file     Functor.java
// @author   Mac Radigan

package org.radigan.naomi.wumpus.service;

import java.util.List;
import java.util.Map;

public interface Functor {
  public String getNamespace();
  public String getName();
  public Map<String,Map<String,String> > getProducts();
  public Map<String,Map<String,String> > getParameters();
  public Map<String,Map<String,String> > getDependencies();
  public List<String> getProductSignature();
  public List<String> getParameterSignature();
  public List<String> getDependencySignature();
  public void call(Map<String,String> parameters) throws RuntimeException;
}

// *EOF*
