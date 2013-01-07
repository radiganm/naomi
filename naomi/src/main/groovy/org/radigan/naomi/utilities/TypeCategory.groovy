// @file     TypeCategory.groovy
// @author   Mac Radigan

package org.radigan.naomi.utilities

import org.radigan.naomi.service.TypesDatabase
import org.apache.log4j.Logger

public class TypeCategory {

  public String isCase(String t) {
    println "invoked isCase"
    return TypesDatabase.getInstance().typeOf(t.toString(),this.toString())
  }

}

// *EOF*
