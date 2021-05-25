/**
 * 
 */
package aas.controller.logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;

/**
 * @author schier
 *
 */
//TODO Later flexible configuration via file
public class LoggerController {
	
	private static final Logger EVENT_LOGGER = Logger.getLogger(LoggerType.EVENT.name());

	/**
	 * 
	 */
	public LoggerController() {
		this.initEventLogger();
	}
	
	private void initEventLogger() {
		 ConsoleHandler handler = new ConsoleHandler();
	     handler.setFormatter(new EventLoggerFormat());
	     EVENT_LOGGER.setUseParentHandlers(false);
	     EVENT_LOGGER.addHandler(handler);
	}

}
