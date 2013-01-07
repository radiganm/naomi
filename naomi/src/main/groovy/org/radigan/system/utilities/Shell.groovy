// @file     Shell.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import org.apache.log4j.Logger;
import java.util.concurrent.ExecutionException
import org.radigan.system.configuration.Configuration
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

public class Shell {
  protected static log = Logger.getLogger(Shell.class.getName())
  private long timeout = 0
  private long defaultTimeout = 4*60*1000

  public Shell() {
  }

  public Shell(Logger log) {
    this.log = log
  }

  public setLogger(Logger log) {
    this.log = log
  }

  public int execute(String command, long timeout=0) {
    try {
      def exitValue = 0
      def initialSize = 4096
      def outStream = new ByteArrayOutputStream(initialSize)
      def errStream = new ByteArrayOutputStream(initialSize)
      log.debug "[executing] ${command}"
      def env = null
      def environment = []
      def pathVar = null
      pathVar = System.getenv("DYLD_LIBRARY_PATH")  // Mac OS X
      if(pathVar) environment += pathVar.tokenize()
      pathVar = System.getenv("LD_LIBRARY_PATH")    // Linux
      if(pathVar) environment += pathVar.tokenize()
      def proc = command.execute(environment as String[], new File('.'))
      proc.consumeProcessOutput(outStream, errStream)
      if(timeout) {
        proc.waitFor()
      } else {
        proc.waitForOrKill(defaultTimeout)
      }
      exitValue = proc.exitValue()
      log.debug "[stdout] ${outStream.toString()}"
      log.debug "[stderr] ${errStream.toString()}"
      if(exitValue) throw new ExecutionException("Command failed with exit code ${proc.exitValue()}.\n${errStream.toString()}")
      return exitValue
    } catch(e) {
      log.debug """Error executing command ${command}.""", e
      throw new Exception("""Error running "${command}".""", e)
    }
  } 

}

// *EOF*
