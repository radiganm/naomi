// @file     DebugSignalHandler.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import sun.misc.Signal
import sun.misc.SignalHandler
import org.codehaus.groovy.tools.shell.Groovysh
import org.codehaus.groovy.tools.shell.IO
import groovy.lang.Binding

class DebugSignalHandler implements SignalHandler {
  protected oldHandler = null
  protected script = null
  protected engine = null

  public DebugSignalHandler() {
  }

  public void setEngine(Object engine) {
    this.engine = engine
  }

  public void setScript(Object script) {
    this.script = script
  }

  public static install(signal) {
    def debugSignalHandler = new DebugSignalHandler()
    debugSignalHandler.oldHandler = Signal.handle(signal, debugSignalHandler)
  }

  public void handle(Signal signal) {
    println("Debug Signal handler called for signal "+signal)
    def list = []
    Thread.activeCount().each{ list += null }
    Thread[] threadArray = list as Thread[]
    int numThreads = Thread.enumerate(threadArray)
    //for (i in 0..<numThreads) {
    threadArray.each { thread ->
      synchronized(thread) {
        //thread.pleaseWait = true
        println("suspending: ${thread}")
        thread.suspend()
        thread.stop()
        thread.interrupt()
        thread.wait()
      }
    }
    System.setProperty("groovysh.prompt", "system")
    def groosh = new Groovysh(script.getClass().getClassLoader(), script.getBinding(), new IO(System.in, System.out, System.err))
    groosh.renderPrompt()
    groosh.run("")
    //script.stop()
    //script.wait()
    engine.terminate()
    script.sleep(90000)
    //engine.stop()
    //engine.wait()
    engine.sleep(90000)
    threadArray.each { thread ->
      synchronized(thread) {
        //thread.pleaseWait = false
        //thread.resume()
        //thread.notify()
      }
    }
    if ( oldHandler != SIG_DFL && oldHandler != SIG_IGN ) {
      oldHandler.handle(signal)
    }
  }
}

// *EOF*
