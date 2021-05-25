/*COPYRIGHT**************************************************************************************
* Copyright (C) 2021                                                                            *
* DLR Deutsches Zentrum fuer Luft- und Raumfahrt e.V./German Aerospace Center e.V.              *
* Institut fuer Flugfuehrung/Institute of Flight Guidance                                       *
* Tel. +49 531 295 2500, Fax: +49 531 295 2550                                                  *
* WWW: http://www.dlr.de/fl/                                                                    *
*                                                                                               *
* These coded instructions, statements, and computer programs contain unpublished proprietary   *
* information of the German Aerospace Center e.V. and are protected by copyright law. They may  *
* not be disclosed to third parties or copied or duplicated in any form, in whole or in part,   *
* without the prior written consent of the German Aerospace Center e.V.                         *
***********************************************************************************END_COPYRIGHT*/

package aas.controller.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import aas.model.Agent;
import aas.model.communication.Message;
import aas.model.util.Point;

/**
 * Writes CSV files with communication and position data.

 * @author schier
 *
 */
public class CsvExport implements Export {

  private static final Logger LOGGER = Logger.getLogger(CsvExport.class.getName());

  private FileWriter positionWriter;
  private FileWriter comWriter;

 /**
 * Constructor.

 * @param comFileName - the name of the communication file
 * @param positionFileName - the name of the position file
 * 
 */
  public CsvExport(final String positionFileName, final String comFileName) {
    try {
    	positionWriter = new FileWriter(positionFileName);
			positionWriter.append("Name");
			positionWriter.append(';');
			positionWriter.append("Time");
			positionWriter.append(';');
			positionWriter.append("X");
			positionWriter.append(';');
			positionWriter.append("Y");
			positionWriter.append('\n');
		} catch(IOException e) {
			throw new IllegalArgumentException("Could not access file " + positionFileName);
		}
		
		try {
			comWriter = new FileWriter(comFileName);
			comWriter.append("Time");
			comWriter.append(';');
			comWriter.append("Sender");
			comWriter.append(';');
			comWriter.append("Receiver");
			comWriter.append(';');
			comWriter.append("Type");
			comWriter.append('\n');
		} catch(IOException e) {
			throw new IllegalArgumentException("Could not access file " + comFileName);
		}
	}
	

	@Override
	public void notify(int time, Agent agent, Message[] messages) {
		this.addPostion(agent.getFootprint().getName(), time, agent.getFootprint().getPosition());
		for(Message message : messages) {
			if((message == null) || (message.getReceiver() == null)) {
				continue;
			}
			this.addMessage(message);
		}	
	}

	private void addMessage(Message message) {
		try {
			comWriter.append(Long.toString(message.getTime()));
			comWriter.append(';');
			comWriter.append(Integer.toString(message.getSender()));
			comWriter.append(';');
			comWriter.append(Integer.toString(message.getReceiver()));
			comWriter.append(';');
			comWriter.append(message.getClass().getSimpleName());
			comWriter.append('\n');
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, "Could not write to file ", e);
		}
	}
	
	public void addPostion(String name, long time, Point point) {
		try {
			positionWriter.append(name);
			positionWriter.append(';');
			positionWriter.append(Long.toString(time));
			positionWriter.append(';');
			positionWriter.append(Double.toString(point.getX()).replace(".", ","));
			positionWriter.append(';');
			positionWriter.append(Double.toString(point.getY()).replace(".", ","));
			positionWriter.append('\n');
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, "Could not write to file ", e);
		}
	}

	@Override
	public void finish() {
		try {
			positionWriter.flush();
			positionWriter.close();
			comWriter.flush();
			comWriter.close();
		} catch(IOException e) {
			LOGGER.log(Level.SEVERE, "Could not write to file ", e);
		}
	}	
		

}
