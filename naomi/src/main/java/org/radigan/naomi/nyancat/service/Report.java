// @file     Report.java
// @author   Mac Radigan

package org.radigan.naomi.nyancat.service;

import java.util.List;
import java.io.File;

public interface Report {
  public String toString();
  public void save(File filename);
  public void save(String filename);
}

// *EOF*
