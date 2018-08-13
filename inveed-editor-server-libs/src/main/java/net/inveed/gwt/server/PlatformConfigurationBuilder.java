package net.inveed.gwt.server;

import net.inveed.gwt.editor.shared.PlatformConfigurationDTO;

public class PlatformConfigurationBuilder {
	public String[] entities;
	public String[] enums;
	public String dateFormat;
	public String timestampFormat;
	
	public final PlatformConfigurationDTO build() {
		return new PlatformConfigurationDTO(entities, enums, dateFormat, timestampFormat);
	}
}
