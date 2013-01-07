// @file     NaomiActivator
// @author   Mac Radigan

package org.radigan.naomi.service

import org.radigan.naomi.utilities.ServiceFactory
import org.radigan.naomi.service.Factory

import java.util.Properties
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.ServiceListener
import org.osgi.framework.ServiceEvent
import org.osgi.framework.ServiceRegistration
import org.osgi.framework.ServiceReference
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.cm.Configuration
import org.osgi.util.tracker.ServiceTracker

public class NaomiActivator implements BundleActivator {

  protected ServiceRegistration registration = null
  protected Factory factory = null

  public void start(BundleContext context) {
    try {
      def propertiesId = 'naomi'
      def propertyHome = 'home'
      def tracker = new ServiceTracker(context, ConfigurationAdmin.class.getName(), null)
      tracker.open()
      def configAdmin = (ConfigurationAdmin)tracker.getService()
      def configuration = configAdmin.getConfiguration(propertiesId, null)
      def properties = configuration.getProperties()
      if(!properties) throw new Exception("""No such properties ID "${propertiesId}".""")
      factory = ServiceFactory.getInstance(new File(properties.get(propertyHome)))
      if(factory) {
        factory.getNaomi().start()
      } else {
        throw new Exception("ERROR:  unable to register ${Factory.class.getName()}")
      }
      def props = new Properties()
      props.put(propertyHome, properties.get(propertyHome))
      registration = context.registerService(Factory.class.getName(), factory, props)
    } catch(Exception e) {
      e.printStackTrace()
    }
  }

  public void stop(BundleContext context) {
    try {
      if(factory) {
        factory.getNaomi().stop()
        factory = null
      }
      if(registration) {
        registration.unregister()
        registration = null
      }
    } catch(Exception e) {
      e.printStackTrace()
    }
  }

}

// *EOF*
