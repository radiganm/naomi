// @file     TestTool
// @author   Mac Radigan

package org.radigan.system.test

//import org.apache.commons.mail.*
import org.radigan.system.tools.AbstractTool
import org.radigan.ycoa.scan.service.*

public class TestTool extends AbstractTool {

  public String getName() {
    return "test"
  }

  public String getDescription() {
    return "test"
  }

  public void initialize() {
    cli.usage = "${getName()} -f <filename> [-h]"
    cli.with {
        h longOpt: 'help',   'show usage information'
        g longOpt: 'debug',  'turn debugging on'
        f longOpt: 'file',   'file',  args:1, argName:'file', required:true
    }
  }

  public int run() {
    return 0
  }

}

/* *EOF* */
