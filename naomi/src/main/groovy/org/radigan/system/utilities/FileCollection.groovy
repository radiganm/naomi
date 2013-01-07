// @file     FileCollection.groovy
// @author   Mac Radigan

package org.radigan.system.utilities

import org.apache.log4j.Logger
import java.util.HashMap
import org.radigan.system.utilities.Md5
import java.rmi.server.UID
import javax.swing.text.DateFormatter
import java.util.Date
import java.text.SimpleDateFormat
import java.util.TimeZone
//import groovy.util.AntBuilder
import org.apache.commons.io.FileUtils

public class FileCollection extends HashMap<File,String> {
  protected static log = Logger.getLogger(FileCollection.class.getName())
  protected String collectionId = null
  protected boolean deleteInput = false
  protected String uniqueId = null
  protected File directory = null
  //protected AntBuilder ant = new AntBuilder()

  public FileCollection(File directory) {
    this.uniqueId = new UID().toString().replaceAll("-","").replaceAll(":","")
    this.collectionId = createCollectionId(uniqueId)
    this.directory = new File("${directory}/${collectionId}")
    initialize()
  }

  public String setPrimaryId(File file, boolean deleteInput) {
    setDeleteInput(deleteInput)
    this.uniqueId = Md5.encode(file.text)
    this.collectionId = createCollectionId(uniqueId)
    this.directory = new File("${this.directory}/${collectionId}")
    def newFile = add(file)
    return newFile
  }

  private initialize() {
    if(!directory.exists()) {
      log.info """        creating directory ${directory}"""
      directory.mkdirs()
    }
  }

  protected String getCollectionId() {
    return collectionId
  }

  protected String getUniqueId() {
    return uniqueId
  }

  protected File getDirectory() {
    return directory
  }

  protected String createCollectionId(String uniqueId) {
    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss")
    dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"))
    def date = dateFormatter.format(new Date())
    return "${date}_${uniqueId}"
  }

  public File updateFile(String filename) {
    def file = new File("${directory}/${filename}")
    if(containsKey(filename)) {
      remove(filename)
      put(Md5.encode(file.text), file)
    } else {
      put(filename, file)
    }
    return file
  }

  public void setDeleteInput(boolean deleteInput) {
    this.deleteInput = deleteInput
  }

  public void leftShift(File file) {
    add(file)
  }

  public void cleanup() {
    def sb = new StringBuffer()
    def endl = java.lang.System.getProperty('line.separator')
    sb << """        deleting file collection""" << endl
    sb << toString()
    log.debug sb.toString()
    //ant.delete(dir:"${directory}")
    FileUtils.deleteDirectory(directory)
  }

  public File getFileByExtension(String extensionList) {
    def file = null
    this.each { key, value ->
      extensionList.tokenize(",").each { extension ->
        def ext = value.name.substring(value.name.indexOf(".")+1)
        if(extension.equalsIgnoreCase(ext)) file = value
      }
    }
    return file
  }

  public String add(File file) {
    def newFile = new File("${directory}/${file.getName()}")
    log.info """        copying file ${file} to ${newFile}"""
    //ant.copy(file:"${file}", toFile:"${newFile}")
    FileUtils.copyFile(file, newFile)
    if(deleteInput) {
      log.info """        deleting file ${file}"""
      file.delete()
    }
    def md5 = Md5.encode(newFile.text)
    this.put(md5, newFile)
    return md5
  }

  @Override public String toString() {
    def sb = new StringBuffer()
    def endl = java.lang.System.getProperty('line.separator')
    sb << "collection<" << collectionId << ">" << endl
    this.each { key, value ->
      sb << "  [" << key.padLeft(32) << "] " << value << endl
    }
    return sb.toString()
  }

}

// *EOF*
