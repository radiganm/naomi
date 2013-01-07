// @file     Xsp.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import groovy.text.GStringTemplateEngine
import groovy.io.PlatformLineWriter

public class Xsp {

  protected GStringTemplateEngine engine = new GStringTemplateEngine()

  public Xsp() {
  }

  public String process(String xsp, Map binding) {
    def template = engine.createTemplate(xsp).make(binding)
    def stringWriter = new StringWriter()
    def platformLineWriter = new PlatformLineWriter(stringWriter)
    template.writeTo(platformLineWriter)
    platformLineWriter.flush()
    return stringWriter.toString()
  }

}

// *EOF*
