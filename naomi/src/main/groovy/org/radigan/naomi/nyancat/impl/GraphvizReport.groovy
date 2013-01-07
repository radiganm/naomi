// @file     GraphvizReport.java
// @author   Mac Radigan

package org.radigan.naomi.nyancat.impl

import org.radigan.naomi.nyancat.service.AbstractReport
import org.radigan.naomi.wumpus.utilities.WumpusUtil

public class GraphvizReport extends AbstractReport {
  protected final String TEMPLATE_PATH = "/org/radigan/naomi/data/reports/graphviz.dot"
  protected String template = ""
  protected WumpusUtil util = null
  protected final static String DEFAULT_FORMAT = "gif"

  public GraphvizReport() { // SPI support
    super()
  }
  public GraphvizReport(WumpusUtil util) {
    super()
    template = resourceManager.getText(TEMPLATE_PATH)
    this.util = util
  }

  public void initialize() { }

  public String toString() {
    return xsp.process(template, ['util':util])
  }

  public void save(File filename, String format=DEFAULT_FORMAT) {
     def tempFile = File.createTempFile("temp",".dot")
     //def tempFile = new File("/var/tmp/test.dot")
     tempFile.write(toString())
     def exe = configuration.searchBin(new File("dot"))
     def timeout = 5*60*1000
     def cmd = "${exe} -T${format}:cairo -o ${filename} ${tempFile}"
     shell.execute(cmd)
     tempFile.delete()
  }
}

// *EOF*
