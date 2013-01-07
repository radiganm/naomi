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
  public void initialize() { }
  protected Map<String,String> parse(URI uri) { 
    def map = [:]
    def path = uri.toString()
    if(path.startsWith("/roar/")) {
      def matcher = path=~/\/roar\/([A-z][A-z0-9]*)\/([A-z][A-z0-9]*)\/([A-z]+)\/([A-z][A-z0-9]*)\.(gif|dot|xml|xsl|xsd|xhtml|html|m)/
      if(matcher.matches()) {
        map['namespace'] = matcher[0][1]
        map['nameDir'] = matcher[0][2]
        map['cat'] = matcher[0][3]
        map['name'] = matcher[0][4]
        map['ext'] = matcher[0][5]
        if(map['name']!=map['nameDir']) return []
        if(!(map['cat'] in ['interface', 'style'])) return []
        return map
      } else {
        return map
      }
    }
    return []
  }
  public boolean canDecode(URI uri) { 
    def map = parse(uri)
    if(map.size()>0) {
      def index = wu.getFunctorIndex(map['name'], map['namespace'])
      return (null!=index)
    }
  }
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    def uri = new URI(req.getRequestURI())
    def map = parse(uri)
    switch(map['cat']) {
      case 'interface':
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
      case 'style':
        break
      default:
        throw new IllegalStateException("Invalid category: ${map['cat']}")
    }
  }
}

// *EOF*
