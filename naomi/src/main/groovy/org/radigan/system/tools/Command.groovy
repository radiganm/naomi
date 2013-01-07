// @file     Command.groovy
// @author   Mac Radigan

package org.radigan.system.tools

import java.util.ServiceLoader
import org.radigan.system.tools.AbstractTool
import org.radigan.system.configuration.Configuration
import org.apache.log4j.Logger

public class Command {

  public static void main(String[] args) {
    def log = Logger.getLogger(Command.class)
    Configuration.getInstance()
    def providers = ServiceLoader.load(AbstractTool.class)
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
