// @file     Tool.java
// @author   Mac Radigan

package org.radigan.system.tools;

import groovy.util.OptionAccessor;

public interface Tool {
  public OptionAccessor parse(String[] args);
  public int process(String[] args);
  public void initialize();
  public String getName();
  public String getDescription();
  public int run();
}
// *EOF*
