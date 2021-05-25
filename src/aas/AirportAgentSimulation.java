/*COPYRIGHT**************************************************************************************
* Copyright (C) 2021                                                                        *
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

package aas;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import aas.controller.AgentController;
import aas.controller.export.CsvExport;
import aas.controller.export.Export;
import aas.controller.export.GeoJsonExport;

/**
 * Main class, loads and initializes the application
 *
 * @author schier
 */
public class AirportAgentSimulation {
	/*
	@startuml
	 */
	
	private static final Logger LOGGER = Logger.getLogger(AirportAgentSimulation.class.getName());
	
	private static AgentController controller;

	/**
	 * main function
	 *
	 * @param args - can be empty or with the config file as argument
	 */
	public static void main(String[] args) {
		
		//1. Determine config file to load
		if ((args.length > 0) && ((new File(args[0])).exists())) {
			loadProperties(new File(args[0]));
		} else {
			final String fileName = getFile();
			loadProperties(new File(fileName));
		}
		
		//2. Check if controller could be initiated
		if (controller == null) {
			LOGGER.severe("Controller could not be initialized");
			System.exit(-1);
		}
		
		//3. Run simulation
		Export[] exports = {new GeoJsonExport("position.geojson"),
				new CsvExport("position.csv", "communication.csv")};
		controller.setExports(exports);
		controller.run();
	}
	
	/**
	 * Loads the properties file
	 * @param file - the xml properties file
	 */
	private static void loadProperties(final File file) {
		Serializer serializer = new Persister();
		try {
			controller = serializer.read(AgentController.class, file);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Config file could not be parsed", e);
			System.exit(-1);
		}
	}
	
	/**
	 * Loads a XML File if necessary
	 * @return A configuration file
	 */
	private static String getFile() {
		final JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        chooser.setFileFilter(new XMLFileFilter());
        final int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) {
        	System.exit(0);
        }
        
		return chooser.getSelectedFile().getAbsolutePath();
	}
	
	/**
	 * Filter for file dialog (only XML Files)
	 * @author schier
	 */
	private static class XMLFileFilter extends FileFilter {

		@Override
		public boolean accept(final File f) {
			if (f.isDirectory()) {
				return true;
			}
			
			return f.getAbsolutePath().endsWith(".xml");
		}

		@Override
		public String getDescription() {
			return "XML-Files";
		}
	}


}
