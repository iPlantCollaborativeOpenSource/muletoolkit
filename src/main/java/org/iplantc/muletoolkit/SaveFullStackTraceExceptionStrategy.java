package org.iplantc.muletoolkit;


import java.util.Date;
import org.apache.log4j.Logger;
import org.mule.DefaultExceptionStrategy;
import org.mule.util.ExceptionUtils;


public class SaveFullStackTraceExceptionStrategy extends DefaultExceptionStrategy{

	private static final Logger LOGGER = Logger.getLogger(SaveFullStackTraceExceptionStrategy.class);
	
	public SaveFullStackTraceExceptionStrategy(){
		super();
	}
	
	
	protected void defaultHandler(Throwable t){
		
		
		try{
			
			
			
			LOGGER.error(new Date().toString() +"\n"+ExceptionUtils.getFullStackTrace(t));
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		super.defaultHandler(t);
		
		
	}
	
	
}
