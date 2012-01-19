package org.iplantc.muletoolkit.config.builders;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.MuleServer;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.builders.MuleXmlBuilderContextListener;
import org.mule.config.builders.WebappMuleXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public class ConditionalMuleXmlBuilderContextListener implements ServletContextListener {
    private MuleContext muleContext;
    
    private static List<Thread> threadsToCleanUp;
    private static final Object threadsToCleanUpLock = new Object();

    protected transient final Log logger = LogFactory.getLog(MuleXmlBuilderContextListener.class);

    public void contextInitialized(ServletContextEvent event)
    {
        initialize(event.getServletContext());
    }

    public void initialize(ServletContext context)
    {
    	Properties props = new Properties();
    	try {
			props.load(this.getClass().getResourceAsStream(context.getInitParameter("org.iplantc.properties")));
		} catch (IOException e) {
			context.log(e.getMessage(), e);
			e.printStackTrace();
		}
		
        StringBuilder configBuilder = new StringBuilder();
        
        Enumeration<?> parameterNames = context.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
        	String parameter = (String)parameterNames.nextElement();
        	if (parameter.startsWith("mule.")) {
        		String enabled = props.getProperty(parameter);
        		if ("true".equals(enabled) || "yes".equals(enabled)) {
        			configBuilder.append(context.getInitParameter(parameter));
        			configBuilder.append(",");
        		}
        	}
        }

        if (configBuilder.length() == 0) {
        	context.log("No Mule configuration files were enabled by properties");
        	
        	return;
        }
        
        configBuilder.deleteCharAt(configBuilder.length() - 1);
        
        try
        {
            muleContext = createMuleContext(configBuilder.toString(), context);
            muleContext.start();
        }
        catch (MuleException ex)
        {
            context.log(ex.getMessage(), ex);
            // Logging is not configured OOTB for Tomcat, so we'd better make a
            // start-up failure plain to see.
            ex.printStackTrace();
        }
        catch (Error error)
        {
            // WSAD doesn't always report the java.lang.Error, log it
            context.log(error.getMessage(), error);
            // Logging is not configured OOTB for Tomcat, so we'd better make a
            // start-up failure plain to see.
            error.printStackTrace();
            throw error;
        }
    }

    /**
     * Creates the MuleContext based on the configuration resource(s) and possibly 
     * init parameters for the Servlet.
     */
    protected MuleContext createMuleContext(String configResource, ServletContext context)
        throws ConfigurationException, InitialisationException
    {
        WebappMuleXmlConfigurationBuilder builder = new WebappMuleXmlConfigurationBuilder(context, configResource);
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();

        // Support Spring-first configuration in webapps
        final ApplicationContext parentContext = (ApplicationContext) context.getAttribute(
                                                        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        
        if (parentContext != null)
        {
            builder.setParentContext(parentContext);
        }
        return muleContextFactory.createMuleContext(builder);
    }

    public void contextDestroyed(ServletContextEvent event)
    {
        destroy();
    }

    public void destroy()
    {
      cleanUpThreads();
      deregisterSQLDrivers();

      if (muleContext != null)
      {
        if (!muleContext.isDisposing() || !muleContext.isDisposed())
        {
          muleContext.dispose();
        }
      }
    }

	private void deregisterSQLDrivers() {
		// This forcibly unregisters any SQL drivers that don't deregister
		// themselves.
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) 
		{
			Driver driver = drivers.nextElement();
			try 
			{
				DriverManager.deregisterDriver(driver);
				logger.info(String.format("deregistering jdbc driver: %s", driver));
			} 
			catch (SQLException e) 
			{
				logger.error(String.format("Error deregistering driver %s", driver), e);
			}
		}
	}

	private void cleanUpThreads() {
		// Clean up any threads that are floating around.
		synchronized (threadsToCleanUpLock) 
		{
			if (threadsToCleanUp != null) 
			{
				for (Thread thread : threadsToCleanUp) 
				{
					try 
					{
						thread.interrupt();
					} 
					catch (Exception ex) 
					{
						logger.warn("Thread " + thread.getName() + " threw an "
								+ "exception when being interrupted", ex);
					}
				}
			}
		}
	}

	/**
	 * Adds a thread to a list of threads that are cleaned up when the
	 * Servlet is destroyed/unloaded/...
	 * 
	 * @param thread
	 *  Thread to clean up
	 */
	public static void addThreadToCleanupList(Thread thread) {
		synchronized (threadsToCleanUpLock) 
		{
			if (threadsToCleanUp == null) 
			{
				threadsToCleanUp = new LinkedList<Thread>();
			}

			threadsToCleanUp.add(thread);
		}
	}
}
