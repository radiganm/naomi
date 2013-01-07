// @file     Debug.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

//import groovy.lang.GroovyShell
import org.codehaus.groovy.tools.shell.Groovysh
import org.codehaus.groovy.tools.shell.IO
import groovy.lang.Binding
import sun.misc.SignalHandler
import sun.misc.Signal

public class Debug {
  protected signalHandler = null

  public static Debug ref = null
  private Debug() { }
  public static Debug getInstance() {
    if(!ref) ref = new Debug()
    return ref
  }

  public SignalHandler getSignalHandler() {
    return signalHandler
  }

  public void setEngine(Object engine) {
    this.signalHandler = new DebugSignalHandler()
    signalHandler.setEngine(engine)
  }

  public static void keyboard(Object script) {
    def groosh = new Groovysh(script.getClass().getClassLoader(), script.getBinding(), new IO(System.in, System.out, System.err))
     groosh.run("")
  }

  public static void handle(Object script) {
   def signalHandler = getInstance().getSignalHandler()
   signalHandler.setScript(script)
   Signal.handle(new Signal("USR2"), signalHandler)
   //Signal.handle(new Signal("INT"), debugSignalHandler)
  }

  /*
  public Debug() {
    def shell = new GroovyShell(getClass().getClassLoader())
    def session = true
    System.in.withReader { reader ->
      while(session) {
        print "\ndebug> "
        def input = reader.readLine()
        try {
          shell.evaluate(input)
        } catch(e) {
          print "error: ${e.getMessage()}"
        }
        session != ("exit"==input||"quit"==input)
      } 
    } 
  }
  */

}

// *EOF*
