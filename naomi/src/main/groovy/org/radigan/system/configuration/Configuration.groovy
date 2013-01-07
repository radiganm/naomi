// @file     Configuration.groovy
// @author   Mac Radigan
// @version  $Id: Configuration.groovy 79 2012-04-04 07:46:41Z mac.radigan $

package org.radigan.system.configuration

import org.radigan.system.utilities.ResourceManager
import org.apache.log4j.xml.DOMConfigurator
import org.apache.log4j.PropertyConfigurator
import groovy.util.ConfigSlurper
import java.net.InetAddress
//import javax.naming.ConfigurationException

public class Configuration {

  public ConfigObject config
  protected static String SYSTEM_NAME = "naomi"
  protected static Configuration ref = null
  protected ResourceManager resourceManager = new ResourceManager()
  protected String environment = null
  protected List libPath
  protected List configPath
  protected List resPath
  protected List binPath
  protected List homePath
  protected String root

  private Configuration(File systemHome) {
    initialize(systemHome)
  }

  public static Configuration getInstance(File systemHome=null) {
     if(null==ref) { ref = new Configuration(systemHome) }
     return ref
  }

  public ResourceManager getResourceManager() {
     return resourceManager
  }

  public File getRootDirectory() {
    return new File(root)
  }

  private initialize(File systemHome=null) {
    //root = resourceManager.getRootDirectory()
    def skelPath = []
    def skelPathVar = System.getenv("PATH")
    if(skelPathVar) skelPath = skelPathVar.tokenize(':')
    homePath = ["./target", "./lib"]
    configPath = [".", "./config"]
    libPath = ["./lib"]
    binPath = ["./bin"] + skelPath
    resPath = ["res/system"]
    if(!systemHome) systemHome = new File(System.getenv("${SYSTEM_NAME.toUpperCase()}_HOME")) // added for OSGi support
    root = "${systemHome}"
    if(!systemHome) {
      install()
      configureExecutables()
    }
    configureClasspath()
    configureSettings()
    configureLogging()
  }

  private install() {
    resPath.each { path -> 
      resourceManager.extractResource("${path}", new File("${root}")) 
    }
  }

  private File searchBin(File file) {
    def result = null
    binPath.each { path -> 
      def testFile = new File("${root}/${path}/${file}")
      if(testFile.exists()) {
        result = testFile
        return
      }
    }
    binPath.each { path -> 
      def testFile = new File("${path}/${file}")
      if(testFile.exists()) {
        result = testFile
        return
      }
    }
    return result
  }

  private File searchConfig(File file) {
    def result = null
    configPath.each { path -> 
      def testFile = new File("${root}/${path}/${file}")
      if(testFile.exists()) {
        result = testFile
        return
      }
    }
    return result
  }

  private configureLogging() {
    //def configFile = searchConfig(new File("logging.xml"))
    def configFile = searchConfig(new File("logging.properties"))
    try {
      //if(configFile) DOMConfigurator.configure(configFile.toURL())
      if(configFile) PropertyConfigurator.configure(configFile.toURL())
    } catch(Exception e) {
      e.printStackTrace()
    }
  }

  public ConfigObject getConfiguration(String environment, File file) {
    def configFile = searchConfig(file)
    //if(!configFile) throw new ConfigurationException("""Configuration file not found: "${file}".""")
    if(!configFile) throw new Exception("""Configuration file not found: "${file}".""")
    def configSlurper = null
    if(environment) {
      configSlurper = new ConfigSlurper(environment)
    } else {
      configSlurper = new ConfigSlurper()
    }
    configSlurper.classLoader = resourceManager.getClassLoader()
    return configSlurper.parse(configFile.toURL())
  }

  public static ConfigObject getConfiguration() {
    return getInstance().config
  }

  private configureSettings() {
    environment = InetAddress.getLocalHost().getHostName()
    config = getConfiguration(environment, new File("${SYSTEM_NAME.toLowerCase()}.conf"))
  }

  private configureClasspath() {
    libPath.each { path ->
      def dir = new File("${root}/${path}")
      if(dir.exists()) resourceManager.addClasspath(dir)
    }
  }

  private configureExecutables() {
    binPath.each { path ->
      new File("${root}/${path}").eachFileRecurse() { file ->
        if(file.canWrite()) file.setExecutable(true, false)
      }
    }
  }


}

// *EOF*
