// @file     RoarImpl.groovy
// @author   Mac Radigan

package org.radigan.naomi.roar.impl

import org.radigan.naomi.roar.service.Roar
import org.radigan.naomi.roar.service.Resource
import org.radigan.naomi.utilities.ServiceFactory
import org.radigan.naomi.wumpus.service.Functor
import org.radigan.naomi.wumpus.service.Wumpus
import org.mortbay.jetty.*
import org.mortbay.jetty.nio.*
import org.mortbay.jetty.servlet.*
import org.mortbay.jetty.deployer.*
import groovy.servlet.*
import org.mortbay.jetty.bio.SocketConnector
import groovy.util.ConfigObject
import java.io.OutputStream
import javax.servlet.http.*
import javax.servlet.ServletException

public class RoarImpl implements Roar {
  protected ServiceFactory serviceFactory = null
  protected File webappDir = null
  protected File cache = null
  protected Server server = new org.mortbay.jetty.Server()
  protected ConfigObject config = null
  protected int port = 80
  protected Wumpus wumpus = null
  protected List<Resource> resources = []
  public RoarImpl() {
    serviceFactory = ServiceFactory.getInstance()
    config = serviceFactory.getConfiguration()
    wumpus = serviceFactory.getWumpus()
    webappDir = new File("${serviceFactory.getRootDirectory()}/webapps")
    port = config.roar.port
    cache = new File(config.roar.cache)
    initializeResources()
    initializeServer()
  }
  protected void initializeServer() {
    def connector = new SocketConnector()
    connector.setPort(port as int)
    server.setConnectors([connector] as Connector[])
    def deployer = new WebAppDeployer()
    deployer.setContexts(server)
    deployer.setWebAppDir("${webappDir}")
    deployer.setExtract(true)
    deployer.setParentLoaderPriority(false)
    deployer.start()
    def rootContext = new Context(server, "/", Context.SESSIONS)
    rootContext.setResourceBase(".")
    rootContext.addServlet(new ServletHolder(new TemplateServlet()), "*.html")
    rootContext.addServlet(new ServletHolder(new GroovyServlet()), "*.glet")
    rootContext.addServlet(new ServletHolder(new RoarServlet(this)), "/roar/*")
    server.setStopAtShutdown(true)
    server.start()
  }
  protected initializeResources() {
    serviceFactory.loadModules()
    serviceFactory.getModules(Resource).each { m ->
      def r = m.newInstance()
      r.initialize()
      r.setUtilities([
        'wu':wumpus.getWumpusUtil(),
        'cache':cache
        ])
      resources << r
    }
  }
  public boolean isRunning() {
    return server.isRunning()
  }
  public void start() {
  }
  public void stop() {
    server.start()
  }
  protected Resource findResource(URI uri) { 
    def rval = null
    resources.each { r -> if(r.canDecode(uri)) { rval=r } }
    return rval
  }
  protected Resource findResource(HttpServletRequest req) {
    def uri = new URI(req.getRequestURI())
    return findResource(uri)
  }
  public boolean canDecode(URI uri) { 
    return null!=findResource(uri)
  }
  protected boolean canDecode(HttpServletRequest req) {
    def uri = new URI(req.getRequestURI())
    return canDecode(uri)
  }
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    findResource(req)?.doGet(req, res)
  }
  public void registerFunctor(List<Functor> functors) {
  }
  public void unregisterFunctor(List<Functor> functors) {
  }
  public void registerInterfaces(List<Functor> functors) {
  }
  public void unregisterInterfaces(List<Functor> functors) {
  }
}

// *EOF*
