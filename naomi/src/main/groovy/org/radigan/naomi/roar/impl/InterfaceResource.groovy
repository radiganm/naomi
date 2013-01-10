// @file     InterfaceResource.java
// @author   Mac Radigan

package org.radigan.naomi.roar.impl

import org.radigan.naomi.nyancat.service.Report
import org.radigan.naomi.nyancat.impl.GraphvizReport
import org.radigan.naomi.nyancat.impl.WumpusReport
import org.radigan.naomi.nyancat.impl.OctaveReport
import org.radigan.naomi.roar.service.Resource
import org.radigan.naomi.roar.service.AbstractResource
import org.radigan.naomi.wumpus.utilities.WumpusUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.OutputStream
import javax.servlet.*
import javax.servlet.http.*
import static javax.servlet.http.HttpServletResponse.*
import javax.servlet.ServletException

public class InterfaceResource extends AbstractResource {
  protected WumpusUtil wu = null
  protected File cacheDir = null
  public InterfaceResource() {
    super()
  }
  public void setUtilities(Map util) {
    this.util = util
    wu = util['wu']
    cacheDir = util['cache']
  }
  public void initialize() { 
    mapRestGet('resource', '/roar/${name}.${ext}')
    mapRestGet('functor', '/roar/${namespace}/${name}/${category}/${fname}.${ext}')
  }
  public boolean canDecode(URI uri) { 
    def path = uri.toString()
    def map = restGet(path)
    switch(map['T']) {
      case 'resource':
        return true
      case 'functor':
        if(map['name']!=map['fname']) return false
        if(map.size()>0) {
          def index = wu.getFunctorIndex(map['name'], map['namespace'])
          return (null!=index)
        }
        return false
      default:
        return false
    }
  }
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    def uri = new URI(req.getRequestURI())
    def map = restGet(uri.toString())
    switch(map['T']) {
      case 'resource':
        switch(map['ext']) {
          case 'ico':
            OutputStream output = res.getOutputStream()
            def filename = new File(uri.getPath()).getName()
            res.setContentType("image/ico")
            res.setHeader('Content-Transfer-Encoding', 'binary')
            res.setHeader('Content-Disposition','attachment; filename="${filename}"' as String)
            def resourcePath = "/org/radigan/naomi/data/images/${filename}"
            def input = getClass().getResourceAsStream(resourcePath)
            IOUtils.copy(input as InputStream, output)
            IOUtils.closeQuietly(input)
            IOUtils.closeQuietly(output)
            break
          default:
            throw new IllegalStateException("Invalid name: ${map['name']}")
        }
        break
      case 'functor':
        def resDir = new File("${cacheDir}/${map['namespace']}/${map['name']}/${wu.getId()}")
        def fn = wu.getFunctorIndex(map['name'], map['namespace'])
        def wf = wu.filter(fn as int)
        switch(map['ext']) {
          case ['html','xhtml']:
            res.getWriter().println(new WumpusReport(wf).toString())
            break
          case 'm':
            res.getWriter().println(new OctaveReport(wf).toString())
            break
          case 'dot':
            res.getWriter().println(new GraphvizReport(wf).toString())
            break
          case ['gif','png','tif','jpg']:
            OutputStream output = res.getOutputStream()
            def filename = new File(uri.getPath()).getName()
            res.setContentType("image/gif")
            res.setHeader('Content-Transfer-Encoding', 'binary')
            /*
            res.setHeader("""Content-Disposition','attachment; filename=\"${filename}\"""" as String)
            */
            def rptFile = new File("${resDir}/${map['name']}.${map['ext']}")
            if(!rptFile.exists()) {
              FileUtils.forceMkdir(resDir)
              def report = new GraphvizReport(wf)
              report.save(rptFile, map['ext'])
            }
            def input = new FileInputStream(rptFile)
            IOUtils.copy(input, output)
            IOUtils.closeQuietly(output)
            break
          default:
            throw new IllegalStateException("Invalid extension: ${map['ext']}")
        }
        break
      default:
        throw new IllegalStateException("Invalid type: ${map['type']}")
    }
  }
}

// *EOF*
