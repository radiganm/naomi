// @file     ServiceFactory
// @author   Mac Radigan

package org.radigan.naomi.utilities

import java.util.Map
import org.apache.log4j.Logger

import org.radigan.naomi.service.Factory
import org.radigan.naomi.service.Module
import org.radigan.naomi.wumpus.service.FunctorList
import org.radigan.naomi.wumpus.impl.SimulationFunctor
import org.radigan.system.configuration.Configuration
import org.radigan.system.utilities.ResourceManager
import org.radigan.naomi.service.Naomi
import org.radigan.naomi.impl.NaomiImpl
import org.radigan.naomi.wumpus.service.Wumpus
import org.radigan.naomi.wumpus.impl.WumpusImpl
import org.radigan.naomi.roar.service.Roar
import org.radigan.naomi.roar.impl.RoarImpl
import com.thoughtworks.xstream.*
import com.thoughtworks.xstream.io.xml.DomDriver
import java.net.InetAddress
import java.util.ServiceLoader

public class ServiceFactory implements Factory {

  protected Configuration configuration = null
  protected ConfigObject naomiConfig = null
  protected Naomi naomi = null
  protected Wumpus wumpus = null
  protected Roar roar = null
  protected String environment = null
  protected GroovyClassLoader classloader = null
  protected Logger log = getLogger(this)
  protected List<Module> modules = []
  protected boolean initialized = false

  private static ServiceFactory ref = null
  private ServiceFactory(File systemHome=null) {
    initialize(systemHome)
  }
  public static ServiceFactory getInstance(File systemHome=null) { 
    if(!ref) ref = new ServiceFactory(systemHome) 
    return ref
  }

  private void initialize(File systemHome=null) {
    classloader = new GroovyClassLoader(getClass().getClassLoader())
    configuration = Configuration.getInstance(systemHome)
    environment = InetAddress.getLocalHost().getHostName()
    naomiConfig = configuration.getConfiguration(environment, new File("naomi.conf"))
  }

  public void loadModules() {
    if(initialized) return
    initialized = true
    def path = new File("${getRootDirectory()}/deploy")
    path.eachFileRecurse() { file ->
      if(file.getName().contains(".naomi")) {
        try {
          log.debug "compiling ${file}"
          classloader.parseClass(file)
        } catch(e) {
          log.error "Compilation failed.  Reason: ${e.getMessage()}"
          throw e
        }
      } else if(file.getName().endsWith(".jar") || file.getName().endsWith(".nar")) {
        try {
          log.debug "loading ${file}"
          classloader.addURL(file.toURI().toURL())
        } catch(e) {
          log.error "Failed to load class.  Reason: ${e.getMessage()}"
          throw e
        }
      }
    }
    ServiceLoader.load(Module.class, classloader).each() { m ->
      def clazz = m.getClass()
      if(clazz in Module && !modules.contains(clazz)) {
        log.debug "adding class ${clazz}"
        modules << clazz
      }
    }
    classloader.getLoadedClasses().each { clazz ->
      if(clazz in Module && !modules.contains(clazz)) { 
        log.debug "adding class ${clazz}"
        modules << clazz
      }
    }
  }

  public List<Module> getModules() { return modules }
  public List<Module> getModules(Class clazz) { 
     def rval = []
     modules.each { if(it in clazz) rval<<it }
     return rval
  }
  public void clearModules() { classloader.clearCache() }
  public void addModules(List<Class> classes) { modules.addAll(classes) }

  public ClassLoader getClassLoader() {
    return classloader
  }

  public String getEnvironment() {
    return environment
  }

  public ResourceManager getResourceManager() {
    return configuration.getResourceManager()
  }

  public ConfigObject getConfiguration() {
    return naomiConfig
  }

  public File getRootDirectory() {
    return configuration.getRootDirectory()
  }

  public Logger getLogger(object) {
    return Logger.getLogger(object.class.getName())
  }

  public Naomi getNaomi() {
    if(!this.naomi) this.naomi = new NaomiImpl()
    return this.naomi
  }

  public Wumpus getWumpus() {
    if(!this.wumpus) this.wumpus = new WumpusImpl()
    return this.wumpus
  }

  public Roar getRoar() {
    if(!this.roar) this.roar = new RoarImpl()
    return this.roar
  }

  public void save(File file, Object object) {
    def xstream = new XStream()
    file.withOutputStream { ostream ->
      xstream.toXML(object, ostream)
    }
  }

  public Object load(File file) {
    def className = new XmlSlurper().parseText(file.text).name()
    def object = Class.forName(className)
    def xstream = new XStream()
    file.withInputStream { istream ->
      object = xstream.fromXML(istream)
    }
    return object
  }

}

/* *EOF* */
