package fr.upmc.datacenter.providers.resources.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation signifiant que la méthode fait l'objet d'un appel à un anneau de composant.
 * 
 * @author Daniel RADEAU
 *
 */

@Documented
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD})
public @interface Ring {
	
}