// @file     AbstractResource.java
// @author   Mac Radigan

package org.radigan.naomi.roar.service

import org.radigan.naomi.nyancat.service.Report
import org.radigan.naomi.service.Module
import javax.servlet.http.HttpServlet

public abstract class AbstractResource extends HttpServlet implements Resource, Module {
  protected Map util = [:]
  public AbstractResource() { }
  public void setUtilities(Map util) {
    this.util = util
  }
  public abstract void initialize()
  public abstract boolean canDecode(URI uri)
}

// *EOF*
