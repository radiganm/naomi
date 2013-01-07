// @file     AbstractReport.java
// @author   Mac Radigan

package org.radigan.naomi.nyancat.service

import org.radigan.naomi.service.Module
import org.radigan.naomi.nyancat.service.Report
import org.radigan.system.configuration.Configuration
import org.radigan.system.utilities.ResourceManager
import org.radigan.naomi.utilities.ServiceFactory
import org.radigan.system.utilities.Xsp
import org.radigan.system.utilities.Shell
import java.io.File

public abstract class AbstractReport implements Report, Module {
  protected ServiceFactory serviceFactory = null
  protected Configuration configuration = null
  protected ResourceManager resourceManager = null
  protected Xsp xsp = new Xsp()
  protected Shell shell = new Shell()
  public AbstractReport() {
    serviceFactory = ServiceFactory.getInstance()
    configuration = Configuration.getInstance()
    resourceManager = configuration.getResourceManager()
  }
  public abstract void initialize()
  public abstract String toString()
  public abstract void save(File filename);
  public void save(String filename) {
    save(new File(filename));
  }
}

// *EOF*
