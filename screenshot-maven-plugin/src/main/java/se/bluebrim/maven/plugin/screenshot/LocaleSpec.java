package se.bluebrim.maven.plugin.screenshot;

import java.util.Locale;

/**
 * Since Maven's configuration mechanism can't instantiate java.util.Locale we use this
 * class to specify the list of locales that should be used when creating screenshots
 * 
 * @author Goran Stack
 *
 */
public class LocaleSpec
{
	private String language;
	private String country = "";
	private String variant = "";
	
	Locale getLocale()
	{
		return new Locale(language, country, variant);
	}
}