// @file     SimulationFunctor.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.impl

import org.apache.log4j.Logger
import org.radigan.naomi.wumpus.service.AbstractFunctor

public class SimulationFunctor extends AbstractFunctor {
  def log = Logger.getLogger(SimulationFunctor.class)
  def namespace = DEFAULT_NAMESPACE
  def name = ""
  def dependencies = []
  def products = []
  def parameters = []
  public SimulationFunctor() {} // SPI support
  public SimulationFunctor(
    String name, 
    List<String> products,
    List<String> dependencies,
    List<String> parameters) {
    this.name = name
    this.dependencies = dependencies
    this.products = products
    this.parameters = parameters
  }
  public SimulationFunctor(
    String namespace, 
    String name, 
    List<String> products,
    List<String> dependencies,
    List<String> parameters) {
    this.namespace = namespace
    this.name = name
    this.dependencies = dependencies
    this.products = products
    this.parameters = parameters
  }
  public void initialize() {
  }
  public String getNamespace() {
    return namespace
  }
  public String getName() {
    return name
  }
  public String getQname() {
    return "${namespace}:${name}"
  }
  public List<String> getProducts() {
    return products
  }
  public List<String> getParameters() {
    return parameters
  }
  public List<String> getDependencies() {
    return dependencies
  }
  public void call(Map<String,String> parameters) throws RuntimeException {
    log.info("calling ${getName()} with parameters: ${parameters}")
  }
}

// *EOF*
