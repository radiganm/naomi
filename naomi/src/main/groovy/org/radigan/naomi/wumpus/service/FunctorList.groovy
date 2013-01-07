// @file     FunctorList.groovy
// @author   Mac Radigan

package org.radigan.naomi.wumpus.service

import org.radigan.naomi.wumpus.service.Functor
import org.radigan.naomi.service.Module
import java.util.ArrayList

public class FunctorList extends ArrayList<Functor> implements Module {
  public void initialize() {}
}

// *EOF*
