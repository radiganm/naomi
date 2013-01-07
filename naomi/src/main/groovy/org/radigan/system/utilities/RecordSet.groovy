// @file     Recordset.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import java.util.ArrayList
import java.util.ServiceLoader
import org.apache.log4j.Logger

public class Recordset extends ArrayList<Record> {
  private static log = Logger.getLogger(Recordset.class)

  public Recordset() {
  }

  public Recordset filter(closure) {
    def resultRecordset = new Recordset()
    this.each { record ->
      if(closure.call(record)) {
      } else {
        resultRecordset << record
      }
    }
    return resultRecordset
  }

  public List<String> getValues() {
    def list = []
    this.each { record ->
      record.each { key, value ->
        list << value
      }
    }
    return list
  }

  public Recordset groupBy(String expression, int nameIndex, int idIndex) {
    def groups = [:]
    def misc = [:]
    def resultRecordset = new Recordset()
    this.each { record ->
      record.each { key, value ->
        def matcher = (key=~expression)
        def resultRecord = null
        if(matcher.matches()) {
          if(!groups[matcher[0][idIndex]]) groups[matcher[0][idIndex]] = new Record()
          resultRecord = groups[matcher[0][idIndex]]
          resultRecord[matcher[0][nameIndex]] = value
        }
      }
      record.each { key, value ->
        def matcher = (key=~expression)
        def resultRecord = null
        if(!matcher.matches()) {
          groups.each { gkey, gvalue ->
            groups[gkey][key] = value
          }
        }
      }
      groups.each { gkey, gvalue ->
        resultRecordset << gvalue
      }
      misc.each { mkey, mvalue ->
        resultRecordset << mvalue
      }
    }
    return resultRecordset
  }

  public String toShortString() {
    def sb = new StringBuffer()
    def endl = java.lang.System.getProperty('line.separator')
    def border = 1
    def indent = 2
    def headers = [:]
    def max = 0
    each { record ->
      record.each { key, value ->
        headers[key] = Math.max(headers[key]?headers[key]:key.size(),"${value}".size())
        max = Math.max(headers[key],max)
      }
    }
    def index = 0
    each { record ->
      sb << "Record " << ++index << ">" << endl
      record.each { key, value ->
        sb << " ".multiply(indent) << key.padLeft(max+indent) << ": " << value << endl
      }
      sb << endl
    }
    return sb.toString()
  }

  @Override public String toString() {
    def sb = new StringBuffer()
    def endl = java.lang.System.getProperty('line.separator')
    def border = 1
    def headers = [:]
    each { record ->
      record.each { key, value ->
        headers[key] = Math.max(headers[key]?headers[key]:key.size(),"${value}".size())
      }
    }
    if(headers.size()) {
      headers.each { key, length -> sb << key.padRight(length+border) }; sb << endl
      headers.each { key, length -> sb << "=".multiply(length)+" ".multiply(border) }; sb << endl
    }
    each { record ->
      record.each { key, value ->
        if(!headers[key]) {
          sb << " ".padLeft(headers[key]) << " ".multiply(border)
        } else {
          sb << "${value}".padLeft(headers[key]) << " ".multiply(border)
        }
      }
      sb << endl
    }
    return sb.toString()
  }

}

// *EOF*
