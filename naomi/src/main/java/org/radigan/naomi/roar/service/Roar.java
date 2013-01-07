// @file     Roar.java
// @author   Mac Radigan

package org.radigan.naomi.roar.service;

import java.util.List;
import org.radigan.naomi.wumpus.service.Functor;

public interface Roar {
  public boolean isRunning();
  public void start();
  public void stop();
  public void registerFunctor(List<Functor> functors);
  public void unregisterFunctor(List<Functor> functors);
  public void registerInterfaces(List<Functor> functors);
  public void unregisterInterfaces(List<Functor> functors);
}

// *EOF*
