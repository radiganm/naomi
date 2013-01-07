// @file     Command2.groovy
// @author   Mac Radigan

package org.radigan.system.tools

import org.apache.commons.discovery.tools.DiscoverSingleton
import org.apache.commons.discovery.resource.ClassLoaders
import org.apache.commons.discovery.resource.classes.DiscoverClasses
import org.apache.commons.discovery.tools.DiscoverClass
import org.apache.commons.discovery.ResourceClassIterator
import org.apache.commons.discovery.ResourceClass
import org.apache.commons.discovery.tools.SPInterface
import org.apache.commons.discovery.ResourceClass
//import static org.apache.commons.discovery.tools.SPInterface.newSPInterface
//import static org.apache.commons.discovery.tools.Service.providers
import org.radigan.system.test.TestTool
import org.radigan.system.utilities.ResourceManager
import org.radigan.system.tools.AbstractTool
import org.radigan.system.tools.Tool
import org.radigan.system.configuration.Configuration
import org.apache.log4j.Logger

public class Command2 {

  public static void main(String[] args) {
    def log = Logger.getLogger(Command.class)
    def configuration = Configuration.getInstance()
    def resourceManager = configuration.getResourceManager()
    def loaders = ClassLoaders.getAppLoaders(Tool.class, getClass(), false)
    def providerClasses = []
    def buffer = resourceManager.getStream("/META-INF/services/org.radigan.system.tools.AbstractTool")
    buffer.eachLine { line -> if(!line.startsWith("#")) providerClasses << line }
    def discoverClasses = new DiscoverClasses<Tool>(loaders)
    //def classList = []
    def providers = []
    providerClasses.each { className ->
      def classIterator = discoverClasses.findResourceClasses(className)
      while(classIterator.hasNext()) {
        def resource = classIterator.nextResourceClass()
        def clazz = resource.loadClass()
        providers << clazz.newInstance()
      }
    }
    if(args.size()) {
      try {
        providers.each() { tool ->
          if(tool.getName()==args[0]) {
            def newargs = new String[args.size()-1]
	    for(int argIndex=0; argIndex<args.size()-1; argIndex++) {
	      newargs[argIndex] = args[argIndex+1]
	    }
            int returnCode = tool.process(newargs)
            System.exit(returnCode)
          }
        }
        usage(providers)
        println "no such command:  ${args[0]}"
        System.exit(1)
      } catch(e) {
        e.printStackTrace()
      }
    } else {
      usage(providers)
      System.exit(0)
    }
  }

  public static void usage(providers) {
    try {
      println "tools:"
      def index = 0
      providers.each() { tool ->
        println "    ${++index}) ${tool.getName()} - ${tool.getDescription()}"
      }
    } catch(e) {
      e.printStackTrace()
    }
  }

}
// *EOF*
