// @file     RoarServlet.groovy
// @author   Mac Radigan

package org.radigan.naomi.roar.impl

import java.io.*
import javax.servlet.http.*
import static javax.servlet.http.HttpServletResponse.*
import javax.servlet.*
import org.apache.commons.io.IOUtils

public class RoarServlet extends HttpServlet {

  protected RoarImpl roar = null

  public RoarServlet(RoarImpl roar) {
    this.roar = roar
  }

  public void doHead(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    try { 
      if(roar.canDecode(req)) {
        roar.doGet(req, res)
      } else {
        res.sendError(SC_NOT_FOUND)
      }
    } catch(e) {
      e.printStackTrace()
      res.sendError(SC_INTERNAL_SERVER_ERROR)
    } finally {
      //IOUtils.closeQuietly(out)
    }
  }


}

// *EOF*
