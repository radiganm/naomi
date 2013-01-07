// @file     NaomiImpl
// @author   Mac Radigan

package org.radigan.naomi.impl

import org.radigan.naomi.service.Naomi
import org.radigan.naomi.roar.impl.RoarImpl
import org.radigan.naomi.roar.service.Roar
import org.radigan.naomi.utilities.ServiceFactory
import org.apache.log4j.Logger

public class NaomiImpl implements Naomi {
  protected Logger log = null
  protected ServiceFactory serviceFactory = null
  protected Roar roar = null
  public NaomiImpl() {
    serviceFactory = ServiceFactory.getInstance()
    log = serviceFactory.getLogger(this)
    roar = serviceFactory.getRoar()
    initializeServer()
  }
  protected void initializeServer() {
    def modules = []
    //modules << org.radigan.naomi.wumpus.impl.SimulationFunctor
    //modules << org.radigan.naomi.roar.impl.InterfaceResource
    //modules << org.radigan.naomi.nyancat.impl.GraphvizReport
    serviceFactory.getResourceManager().getText('/META-INF/services/org.radigan.naomi.service.Module').split("\\r?\\n").each { line ->
      if(!line.startsWith('#')) modules << Class.forName(line)
    }
    serviceFactory.addModules(modules)
  }
  public void start() {
    roar.start()
  }
  public void stop() {
    roar.stop()
  }
}

// *EOF*
