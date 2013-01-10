// @file     AbstractResource.java
// @author   Mac Radigan

package org.radigan.naomi.roar.service

import org.radigan.naomi.nyancat.service.Report
import org.radigan.naomi.service.Module
import javax.servlet.http.HttpServlet

public abstract class AbstractResource extends HttpServlet implements Resource, Module {
  protected Map util = [:]
  protected List restGetList = []
  protected List restPostList = []
  public AbstractResource() { }
  public void setUtilities(Map util) {
    this.util = util
  }
  protected List compile(String pattern) {
    def re = pattern
    def map = [:]
    def match = pattern=~/(\$\{[A-z0-9:]+\})/
    if(match) { match.each { m -> 
      def (name,type) = m[0].replaceAll('[${}]',"").tokenize(':').reverse() 
      def rex = ''
      def clazz = String.class
      switch(type) {
        case ['i']:
          rex = '([0-9]+)'
          clazz = Integer.class
          break
        case [null,'s']:
          rex = '([A-z0-9_:]+)'
          clazz = String.class
          break
        default:
          throw new IllegalArgumentException("Illegal control character: '${type}'")
      } 
      map[name] = clazz
      re = re.replace(m[0], rex)
    } }
    return [re, map]
  }
  protected Map parse(String type, List remap, String path) {
    def rval = [:]
    def (re,map) = remap
    def ind = 1
    def match = path=~/${re}/
    if(match) { 
      rval['T'] = type
      map.each { k, v -> rval[k] = v.newInstance(match[0][ind++]) }
    }
    return rval
  }
  public void mapRestGet(String type, String pattern) {
    restGetList << { path -> parse(type, compile(pattern), path) }
  }
  public void mapRestPost(String type, String pattern) {
    restPostList << { path -> parse(type, compile(pattern), path) }
  }
  public Map restGet(String path) {
    def rval = [:]
    restGetList.each { if(it(path)) rval = it(path) }
    return rval
  }
  public Map restPost(String path) {
    def rval = [:]
    restPostList.each { if(it(path)) rval = it(path) }
    return rval
  }

  public abstract void initialize()
  public abstract boolean canDecode(URI uri)
}

// *EOF*
