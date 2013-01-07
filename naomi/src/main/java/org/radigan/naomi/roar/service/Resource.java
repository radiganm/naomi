// @file     Resource.java
// @author   Mac Radigan

package org.radigan.naomi.roar.service;

import java.util.List;
import java.util.Map;
import java.net.URI;
import java.io.OutputStream;

public interface Resource {
  public void setUtilities(Map utilities);
  public boolean canDecode(URI uri);
}

// *EOF*
