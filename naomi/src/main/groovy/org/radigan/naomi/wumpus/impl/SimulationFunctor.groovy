// @file     SimulationFunctor.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.impl

import org.apache.log4j.Logger
import org.radigan.naomi.wumpus.service.AbstractFunctor

public class SimulationFunctor extends AbstractFunctor {
  def log = Logger.getLogger(SimulationFunctor.class)
  public SimulationFunctor() {} // SPI support
  public SimulationFunctor(
    String name, 
    List<String> prods,
    List<String> deps,
    List<String> params) {
    this.namespace = DEFAULT_NAMESPACE
    this.name = name
    deps.eachWithIndex { type, index -> 
      this.dependencies[TYPE] = type
      this.dependencies[NAME] = "p${index}"
    }
    prods.eachWithIndex { prod, index -> 
      this.products[TYPE] = prod
      this.products[NAME] = "p${index}"
    }
    params.eachWithIndex { param, index -> 
      this.parameters[TYPE] = param
      this.parameters[NAME] = "a${index}"
    }
  }
  public SimulationFunctor(
    String namespace, 
    String name, 
    List<String> prods,
    List<String> deps,
    List<String> params) {
    this.namespace = namespace
    this.name = name
    deps.eachWithIndex { type, index -> 
      this.dependencies[TYPE] = type
      this.dependencies[NAME] = "p${index}"
    }
    prods.eachWithIndex { prod, index -> 
      this.products[TYPE] = prod
      this.products[NAME] = "p${index}"
    }
    params.eachWithIndex { param, index -> 
      this.parameters[TYPE] = param
      this.parameters[NAME] = "a${index}"
    }
  }
  public SimulationFunctor(
    String namespace, 
    String name, 
    Map<String,Map<String,String> > products,
    Map<String,Map<String,String> > dependencies,
    Map<String,Map<String,String> > parameters) {
    this.namespace = namespace
    this.name = name
    this.dependencies = dependencies
    this.products = products
    this.parameters = parameters
  }
  public void initialize() {
  }
  public void call(Map<String,String> parameters) throws RuntimeException {
    log.info("calling ${getName()} with parameters: {parameters}")
  }
}

// *EOF*
