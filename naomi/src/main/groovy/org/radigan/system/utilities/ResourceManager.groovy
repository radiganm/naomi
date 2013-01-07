// @file:    ResourceManager.groovy
// @author:  Mac Radigan

package org.radigan.system.utilities

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
import groovy.lang.GroovyClassLoader
import java.lang.ClassLoader
//import java.net.URLClassLoader

public class ResourceManager {

  protected static final int BUFFER_SIZE = 2156
  //protected GroovyClassLoader classloader = null
  protected URLClassLoader classloader = null
  protected static String SYSTEM_NAME = "naomi"

  public ResourceManager() {
    classloader = new GroovyClassLoader(getClass().getClassLoader())
    //classloader = new URLClassLoader([] as URL[], getClass().getClassLoader())
  }

  public ClassLoader getClassLoader() {
    return classloader
  }

  public void addClasspath(File path) {
    //def classLoader = new GroovyClassLoader()
    path.eachFileRecurse() { file ->
      if(file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
        println "loading ${file}"
        this.class.getClassLoader().addURL(file.toURI().toURL())
        //classloader.addURL(file.toURI().toURL())
      }
    }
  }

  public File getJarFile() {
    //def protectionDomain = ResourceManager.class.getProtectionDomain()
    def protectionDomain = getClass().getProtectionDomain()
    def location = protectionDomain.getCodeSource().getLocation()
    return new File(location.toURI())
  }

  public File getRootDirectory() {
    def systemHome = System.getenv("${SYSTEM_NAME.toUpperCase()}_HOME") // added for OSGi support
    println "systemHome: ${systemHome}"
    if(systemHome) {
      def file = new File(systemHome)
      if(!file.exists()) throw new Exception("""Environment variable ${SYSTEM_NAME.toUpperCase()}_HOME = "${systemHome}" is not a valid directory.""")
      return file
    }
    def protectionDomain = getClass().getProtectionDomain()
    def location = protectionDomain.getCodeSource().getLocation()
    return new File(location.toURI()).getParentFile().getParentFile()
  }

  protected String reducePath(String resourcePath, JarEntry jarEntry) {
    def resource = jarEntry.getName()
    return new File(resource.substring(resourcePath.length(), resource.length()))
  }

  public InputStream getStream(String resourcePath) {
    //return ResourceManager.class.getResourceAsStream(resourcePath)
    return getClass().getResourceAsStream(resourcePath)
  }

  public String getText(String resourcePath) {
    //def inputStream = ResourceManager.class.getResourceAsStream(resourcePath)
    def inputStream = getClass().getResourceAsStream(resourcePath)
    if(null==inputStream) throw new Exception("Resource not found ${resourcePath}")
    def bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
    def lineSep = System.getProperty("line.separator")
    def sb = new StringBuffer()
    def line = null
    while((line=bufferedReader.readLine())!=null) {
      sb.append(line)
      sb.append(lineSep)
    }
    return sb.toString()
  }

  public void extractResource(String resourcePath, File destinationDirectory) {
    def destination = null
    def jarInputStream = new JarInputStream(new FileInputStream(getJarFile()))
    def jarEntry = null
    while((jarEntry=jarInputStream.getNextJarEntry())!=null) {
      if(jarEntry.getName().startsWith(resourcePath)) {
        def reduced = reducePath(resourcePath, jarEntry)
	if(null!=reduced) {
	  if(jarEntry.isDirectory()) {
	    def dir = new File(destinationDirectory, reduced)
	    if(!dir.exists()) {
	      println "extracting ${dir.getAbsolutePath()}"
	      dir.mkdir()
	      if(jarEntry.getTime()!=-1) {
	        dir.setLastModified(jarEntry.getTime())
	      }
	    }
	  } else { // entry is a file
	    def byteCount = 0
	    def data = new byte[BUFFER_SIZE]
	    def file = new File(destinationDirectory, reduced)
	    if(!file.exists()) {
	      destination = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE)
	      println "extracting ${file.getAbsolutePath()}"
	      while((byteCount=jarInputStream.read(data, 0, BUFFER_SIZE))!=-1) {
	        destination.write(data, 0, byteCount)
	      }
	      destination.flush()
	      destination.close()
	      if(jarEntry.getTime()!=-1) {
	        file.setLastModified(jarEntry.getTime())
	      }
	    }
	  }
	}
      }
    }
    jarInputStream.close()
  }

}

// *EOF*
