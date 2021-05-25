/**
 * 
 */
package aas.controller.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author schier
 *
 */
public class EventLoggerFormat extends Formatter {

	/**
	 * 
	 */
	public EventLoggerFormat() {
		super();
	}

	@Override
	public String format(LogRecord record) {
		return record.getMessage() + "\n";
	}

}
