// @file     AbstractFunctor.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.service
import org.radigan.naomi.service.Module

public abstract class AbstractFunctor implements Functor, Module {
  public static final String DEFAULT_NAMESPACE = 'ns1'
  public static final String NAME = 'name'
  public static final String TYPE = 'type'
  def namespace = DEFAULT_NAMESPACE
  def name = ""
  def dependencies = [:]
  def products = [:]
  def parameters = [:]
  public abstract void initialize()
  public abstract void call(Map<String,String> parameters) throws RuntimeException;
  public String toString() {
    def sb = new StringBuilder()
    sb << "[${getProductSignature().join(',')}]"
    if(getNamespace()==DEFAULT_NAMESPACE) {
      sb << "=${getNamespace()}:${getName()}"
    } else {
      sb << "=${getName()}"
    }
    sb << "([${getDependencySignature().join(',')}])"
    return sb.toString()
  }
  public List<String> getProductSignature() {
    def r = []
    this.products.each { k, v -> if(k==TYPE) r<<v }
    return r
  }
  public List<String> getParameterSignature() {
    def r = []
    this.parameters.each { k, v -> if(k==TYPE) r<<v }
    return r
  }
  public List<String> getDependencySignature() {
    def r = []
    this.dependencies.each { k, v -> if(k==TYPE) r<<v }
    return r
  }
  public Map<String, Map<String,String> > getProducts() {
    return this.products
  }
  public Map<String, Map<String,String> > getParameters() {
    return this.parameters
  }
  public Map<String, Map<String,String> > getDependencies() {
    return this.dependencies
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
}

// *EOF*
