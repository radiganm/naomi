// @file     WumpusReport.java
// @author   Mac Radigan

package org.radigan.naomi.nyancat.impl

import org.radigan.naomi.nyancat.service.AbstractReport
import org.radigan.naomi.wumpus.utilities.WumpusUtil

public class WumpusReport extends AbstractReport {
  protected final String TEMPLATE_PATH = "/org/radigan/naomi/data/reports/wumpus.html"
  protected String template = ""
  protected WumpusUtil util = null

  public WumpusReport() { // SPI support
    super()
  }
  public WumpusReport(WumpusUtil util) {
    super()
    template = resourceManager.getText(TEMPLATE_PATH)
    this.util = util
  }

  public void initialize() { }

  public String toString() {
    return xsp.process(template, ['util':util])
  }

  public void save(File filename) {
     filename << toString()
  }

}

// *EOF*
