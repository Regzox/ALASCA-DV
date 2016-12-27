package fr.upmc.javassist;

import java.lang.reflect.Method;

import javax.imageio.stream.ImageInputStream;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class DynamicConnectorFactory extends AbstractClassFactory {
	
	public static void main (String[] args) {
		try {
			Class<?> requestNotificationConnector = DynamicConnectorFactory.createConnector(RequestNotificationI.class, RequestNotificationI.class);
			System.out.println(requestNotificationConnector.getInterfaces());
			
			Class<?> requestSubmissionConnector = DynamicConnectorFactory.createConnector(RequestSubmissionI.class, RequestSubmissionI.class);
			System.out.println(requestSubmissionConnector.getInterfaces());
			
			Class<?> someInterface = DynamicConnectorFactory.createConnector(ImageInputStream.class);
			System.out.println(someInterface.getInterfaces());
			
		} catch (NotFoundException | CannotCompileException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creation d'un connecteur en fonction des interfaces requises et offertes
	 * 
	 * @param requiredInterface
	 * @param offeredInterface
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	
	public static Class<?> createConnector( 
			Class<?> requiredInterface, 
			Class<?> offeredInterface) throws NotFoundException, CannotCompileException 
	{
		Class<?> connector = null;
		String canonicalName = null;
		
		canonicalName = canonicalPrefix + "connectors.Dynamic" + requiredInterface.getSimpleName().replace("(I |I$)", "Connector");
		if (!canonicalName.contains("Connector"))
			canonicalName += "Connector";
		
		if (classes.containsKey(canonicalName))
			return classes.get(canonicalName);
		
		ClassPool classPool = ClassPool.getDefault();
		CtClass ctAbstractConnector = classPool.get(AbstractConnector.class.getCanonicalName());
		CtClass ctInterface = classPool.get(requiredInterface.getCanonicalName());
		CtClass ctConnector = classPool.makeClass(canonicalName);
		
		ctConnector.setSuperclass(ctAbstractConnector);
		ctConnector.setInterfaces(new CtClass[] {ctInterface});
		
		for (Method method : requiredInterface.getDeclaredMethods()) {			
			String signature = createSignatureString(method);
			String body = createBodyString(signature, method, offeredInterface);
			
			System.out.println(signature + body + CRLF);
			
			CtMethod ctMethod = CtMethod.make(signature + body, ctConnector);
			ctConnector.addMethod(ctMethod);
		}
		
		System.out.println(ctConnector.toString());
	
		connector = ctConnector.toClass();
	
		ctAbstractConnector.detach();
		ctInterface.detach();
		ctConnector.detach();
		
		classes.put(canonicalName, connector);
		
		return connector;
	}
	
	/**
	 * Création d'un connecteur en fonction de l'interface unique implémentée
	 * 
	 * @param implementedInterface
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	
	public static Class<?> createConnector(Class<?> implementedInterface) throws NotFoundException, CannotCompileException 
	{
		Class<?> connector = null;
		String canonicalName = null;
		
		canonicalName = canonicalPrefix + "connectors.Dynamic" + implementedInterface.getSimpleName().replace("(I |I$)", "Connector");
		if (!canonicalName.contains("Connector"))
			canonicalName += "Connector";
		
		if (classes.containsKey(canonicalName))
			return classes.get(canonicalName);
		
		ClassPool classPool = ClassPool.getDefault();
		CtClass ctAbstractConnector = classPool.get(AbstractConnector.class.getCanonicalName());
		CtClass ctInterface = classPool.get(implementedInterface.getCanonicalName());
		CtClass ctConnector = classPool.makeClass(canonicalName);
		
		ctConnector.setSuperclass(ctAbstractConnector);
		ctConnector.setInterfaces(new CtClass[] {ctInterface});
		
		for (Method method : implementedInterface.getDeclaredMethods()) {			
			String signature = createSignatureString(method);
			String body = createBodyString(signature, method, implementedInterface);
			
			System.out.println(signature + body + CRLF);
			
			CtMethod ctMethod = CtMethod.make(signature + body + CRLF, ctConnector);
			ctConnector.addMethod(ctMethod);
		}
		
		System.out.println(ctConnector.toString());
	
		connector = ctConnector.toClass();
	
		ctAbstractConnector.detach();
		ctInterface.detach();
		ctConnector.detach();
		
		classes.put(canonicalName, connector);
		
		return connector;
	}
	
	/**
	 * Signature de la méthode vers en chaine de caractères
	 * 
	 * @param implementedMethod
	 * @return
	 */
	
	private static String createSignatureString(Method implementedMethod) {
		String signature = null;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(PUBLIC);
		sb.append(SPACE);
		sb.append(implementedMethod.getReturnType().getCanonicalName());
		sb.append(SPACE);
		sb.append(implementedMethod.getName());
		sb.append(LEFT_PARENTHESIS);
		int index = 0;
		for (Class<?> parameterType : implementedMethod.getParameterTypes()) {
			sb.append(parameterType.getCanonicalName());
			sb.append(SPACE);
			sb.append(ARG);
			sb.append(index++);
			sb.append(COMMA);
			sb.append(SPACE);
		}
		sb.append(END);
		sb.append(RIGHT_PARENTHESIS);
		
		Class<?>[] exceptionTypes = implementedMethod.getExceptionTypes();
		if (exceptionTypes != null) {
			if (exceptionTypes.length > 0) {
				sb.append(SPACE);
				sb.append(THROWS);
				sb.append(SPACE);
				for (Class<?> exceptionType : exceptionTypes) {
					sb.append(exceptionType.getCanonicalName());
					sb.append(COMMA);
					sb.append(SPACE);
				}
				sb.append(END);
			}
		}
	
		
		signature = sb.toString().replace(COMMA + SPACE + END, EMPTY);
		signature = signature.replace(END, EMPTY);
		
		return signature;
	}
	
	/**
	 * Corps de la méthode en chaine de caractères
	 * 
	 * @param signature
	 * @param implementedMethod
	 * @param offeredInterface
	 * @return
	 */
	
	private static String createBodyString(String signature, Method implementedMethod, Class<?> offeredInterface) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\n");
		if (!signature.contains("void")) {
			sb.append("\t");
			sb.append("return");
			sb.append(SPACE);
		} else {
			sb.append("\t");
		}
		sb.append("( (");
		sb.append(offeredInterface.getCanonicalName());
		sb.append(") this.offering ).");
		sb.append(implementedMethod.getName());
		sb.append(LEFT_PARENTHESIS);
		for (int i = 0; i < implementedMethod.getParameterCount(); i++) {
			sb.append(ARG);
			sb.append(i);
			sb.append(COMMA);
		}
		sb.append(END);
		sb.append(RIGHT_PARENTHESIS);
		sb.append(";\n");
		sb.append("}");
	
		String body = sb.toString().replace(COMMA + END, EMPTY);
		body = body.replace(END, EMPTY);
		
		return body;
	}

}
