package se.bluebrim.maven.plugin.screenshot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Used to annotate method in test classes that returns a JComponent suitable
 * for screen shot ripping.
 * 
 * @author G Stack
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Screenshot {
	/**
	 * Specify the class that should be associated with the screen shot. Can be omitted if the screen shot 
	 * method returns that class.
	 */
	Class<?> targetClass() default ObjectUtils.Null.class;
	
	/**
	 * Used to generate different file names for several screen shots of the same class 
	 * 
	 */
	String scene() default "";
	
	/**
	 * Set this attribute to false to avoid generating several screenshots of 
	 * components that is not affected by Locale for example components with no
	 * text or money etc.
	 */
	boolean oneForEachLocale() default true; 
}
