// @file     AbstractFunctor.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.service
import org.radigan.naomi.service.Module

public abstract class AbstractFunctor implements Functor, Module {
  public static final String DEFAULT_NAMESPACE = "ns1"
  public abstract void initialize()
  public abstract String getNamespace()
  public abstract String getName()
  public abstract List<String> getProducts();
  public abstract List<String> getParameters();
  public abstract List<String> getDependencies();
  public abstract void call(Map<String,String> parameters) throws RuntimeException;
  public String toString() {
    def sb = new StringBuilder()
    sb << "[${getProducts().join(',')}]"
    if(getNamespace()==DEFAULT_NAMESPACE) {
      sb << "=${getNamespace()}:${getName()}"
    } else {
      sb << "=${getName()}"
    }
    sb << "([${getDependencies().join(',')}])"
    return sb.toString()
  }
}

// *EOF*
