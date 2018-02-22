package de.persosim.editor.ui.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.utils.PreferenceAccessor;

public class IniPreferenceStoreAccessor implements PreferenceAccessor {

	Properties props = new Properties();
	private Path path;
	
	public IniPreferenceStoreAccessor(Path path) {
		this.path = path;
		if (Files.exists(path)) {
			try {
				props.load(Files.newInputStream(path));
			} catch (IOException e) {
				BasicLogger.log(getClass(), "Failure during load of properties file", LogLevel.WARN);
			}
		} else {
			store();
		}
	}

	private void store() {
		try {
			props.store(Files.newOutputStream(path), null);
		} catch (IOException e) {
			BasicLogger.log(getClass(), "Failure during store of properties file", LogLevel.ERROR);
		}
	}
	
	@Override
	public void set(String key, String value) {
		props.setProperty(key, value);
		store();
	}

	@Override
	public String get(String key) {
		return props.getProperty(key);
	}

}
