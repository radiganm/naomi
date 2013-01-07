// @file     Factory
// @author   Mac Radigan

package org.radigan.naomi.service;

import org.radigan.naomi.service.Naomi;
import org.radigan.naomi.roar.service.Roar;
import org.radigan.naomi.wumpus.service.Wumpus;
import org.radigan.naomi.service.Module;
import java.util.List;

public interface Factory {
  public Naomi getNaomi();
  public Roar getRoar();
  public Wumpus getWumpus();
  public void loadModules();
  public List<Module> getModules();
  public List<Module> getModules(Class clazz);
  public void clearModules();
  public void addModules(List<Class> classes);
}

// *EOF*
