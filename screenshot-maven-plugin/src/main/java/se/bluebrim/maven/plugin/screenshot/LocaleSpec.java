package se.bluebrim.maven.plugin.screenshot;

import java.util.Locale;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Since Maven's configuration mechanism can't instantiate java.util.Locale we use this
 * class to specify the list of locales that should be used when creating screenshots
 * 
 * @author Goran Stack
 *
 */
public class LocaleSpec
{
	@Parameter
	private String language;
	
	@Parameter
	private String country = "";
	
	@Parameter
	private String variant = "";
	
	Locale getLocale()
	{
		return new Locale(language, country, variant);
	}
}