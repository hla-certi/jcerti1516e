/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti.jlc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import hla.rti.RTIinternalError;

public class RtiFactoryFactory {
	public static RtiFactory getRtiFactory(String factoryClassName) throws RTIinternalError {
		try {
			Class cls = Class.forName(factoryClassName);
			return (RtiFactory) cls.newInstance();
		} catch (ClassNotFoundException e) {
			throw new RTIinternalError("Cannot find class " + factoryClassName);
		} catch (InstantiationException e) {
			throw new RTIinternalError("Cannot instantiate class " + factoryClassName);
		} catch (IllegalAccessException e) {
			throw new RTIinternalError("Cannot access class " + factoryClassName);
		}
	}

	public static RtiFactory getRtiFactory() throws RTIinternalError {
		String userHomeDir = System.getProperty("user.home");
		File propertiesFile = new File(userHomeDir, "RTI-list.properties");

		if (propertiesFile.exists()) {
			Properties properties = new Properties();
			try {
				InputStream is = new FileInputStream(propertiesFile);
				properties.load(is);
				is.close();
			} catch (IOException e) {
				throw new RTIinternalError("Error reading Link Compatibility settings file");
			}

			String defaultRTI = properties.getProperty("Default");
			if (defaultRTI != null) {
				String factoryClassName = properties.getProperty(defaultRTI + ".factory");
				if (factoryClassName == null) {
					throw new RTIinternalError("Cannot find factory class setting for default RTI");
				}

				return getRtiFactory(factoryClassName);
			}
		}

		// Provide a reasonable default if no setting found
		return getRtiFactory("certi.rti.jlc.CertiRtiFactory");
	}

	public static Map<String, String> getAvailableRtis() throws RTIinternalError {
		String userHomeDir = System.getProperty("user.home");
		File propertiesFile = new File(userHomeDir, "RTI-list.properties");

		if (!propertiesFile.exists()) {
			throw new RTIinternalError("Cannot find file " + propertiesFile);
		}

		Properties properties = new Properties();
		try {
			InputStream is = new FileInputStream(propertiesFile);
			properties.load(is);
			is.close();
		} catch (IOException e) {
			throw new RTIinternalError("Error reading Link Compatibility settings file");
		}

		Map<String, String> map = new HashMap<>();
		int index = 1;
		while (true) {
			String rtiName = properties.getProperty(index + ".name");
			String rtiFactory = properties.getProperty(index + ".factory");
			if (rtiName == null || rtiFactory == null) {
				break;
			}
			map.put(rtiName, rtiFactory);
			index++;
		}

		return map;
	}
}
