// @file     WumpusImpl.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.impl

import org.radigan.naomi.wumpus.service.Wumpus
import org.radigan.naomi.wumpus.service.Functor
import org.radigan.naomi.wumpus.service.FunctorList
import org.radigan.naomi.wumpus.utilities.WumpusUtil
import org.radigan.naomi.utilities.ServiceFactory
import org.apache.log4j.Logger

public class WumpusImpl implements Wumpus {

  protected ServiceFactory serviceFactory = null
  protected WumpusUtil wumpusUtil = null
  protected Logger log = null
  protected List<Functor> functors = []

  public WumpusImpl() { 
    serviceFactory = ServiceFactory.getInstance()
    log = serviceFactory.getLogger(this)
    initializeFunctors()
  }

  public initializeFunctors() {
    serviceFactory.loadModules()
    serviceFactory.getModules(FunctorList).each { m ->
      def f = m.newInstance()
      f.initialize()
      functors.addAll(f)
    }
    serviceFactory.getModules(Functor).each { m ->
      def f = m.newInstance()
      f.initialize()
      functors << f
    }
    wumpusUtil = new WumpusUtil(functors)
  }

  public WumpusUtil getWumpusUtil() {
    return wumpusUtil
  }

  public String toString() {
    def sb = new StringBuilder() 
    sb << getWumpusUtil()
    return sb.toString()
  }

}

// *EOF*
