package fr.upmc.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.upmc.datacenter.software.dispatcher.time.Chronometer;

/**
 * Classe permettant de réprésenter le model à composant du datacenter à la manière d'un graphe de noeuds.
 * Chaque noeud peut contenir des informations sur le composant comme l'uri, ses ports et ses connexions.
 * 
 * But tenter de simplifier les moyens de recherche d'informations sur le datacenter en fournissant des méthodes
 * permettant de retrouver le composant à partir d'une seule information dessus et ce quelque soit son emplacement
 * dans le graphe.
 * 
 * @author Daniel RADEAU
 *
 */

public class ComponentDataNode {

	public Set<ComponentDataNode> parents;
	public Set<ComponentDataNode> children;
	
	public String uri;
	public Set<String> ports;
	public Map<String, String> connections;
	
	public ComponentDataNode(Object uri) {
		this.uri = uri.toString();
		
		parents = new HashSet<>();
		children = new HashSet<>();
		ports = new HashSet<>();
		connections = new HashMap<>();
	}
	
	/**
	 * Méthode moche pour retrouver tous les noeuds connexes du graphe.
	 * 
	 * @return
	 */
	
	private Set<ComponentDataNode> gathering() {
		Set<ComponentDataNode> previous = new HashSet<>();
		Set<ComponentDataNode> result = new HashSet<>();
		Set<ComponentDataNode> rest = new HashSet<>();
		
		previous.add(this);
		result.add(this);
		rest.add(this);
		
		do {
			previous.addAll(rest);
			
			Set<ComponentDataNode> neighbours = new HashSet<>();
			for (ComponentDataNode node : rest) {
				neighbours.addAll(node.parents);
				neighbours.addAll(node.children);
			}
			result.addAll(neighbours);
			rest.addAll(neighbours);
			rest.removeAll(previous);
		} while (previous.size() != result.size());
		
		return result;
	}
	
	/**
	 * Permet de retrouver un noeud à partir de son uri.
	 * 
	 * @param uri
	 * @return
	 */
	
	public ComponentDataNode findByURI(Object uri) {
		ComponentDataNode result = null;
		
		for (ComponentDataNode node : gathering())
			if (node.uri.equals(uri.toString())) {
				result = node;
				break;
			}
		
		return result;
	}
	
	/**
	 * Permet de retourver un noeud à partir d'une uri de ses ports.
	 * 
	 * @param port
	 * @return
	 */
	
	public ComponentDataNode findByOwnedPort(String port) {
		ComponentDataNode result = null;
		
		for (ComponentDataNode node : gathering()) {
			if (node.ports.contains(port)) {
				result = node;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Permet de retrouver un noeud à partir d'une uri d'un des ports auquel le composant est connecté.
	 * 
	 * @param otherPort
	 * @return
	 */
	
	public ComponentDataNode findByConnectedPort(String otherPort) {
		ComponentDataNode result = null;
		
		for (ComponentDataNode node : gathering()) {
			if (node.connections.values().contains(otherPort)) {
				result = node;
				break;
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("URI : \t");
		sb.append(uri);
		sb.append("\n\t");
		sb.append("PARENTS : ");
		for (ComponentDataNode parent : parents) {
			sb.append("\n\t\t(");
			sb.append(parent.uri);
			sb.append(")");
		}
		sb.append("\n\t");
		sb.append("CHILDREN : ");
		for (ComponentDataNode child : children) {
			sb.append("\n\t\t(");
			sb.append(child.uri);
			sb.append(")");
		}
		sb.append("\n\t");
		sb.append("PORTS : ");
		for (String port : ports) {
			sb.append("\n\t\t[");
			sb.append(port);
			sb.append("]");
		}
		sb.append("\n\t");
		sb.append("CONNECTIONS : ");
		for (String port : connections.keySet()) {
			sb.append("\n\t\t[");
			sb.append(port);
			sb.append("] : [");
			sb.append(connections.get(port));
			sb.append("]");
		}
		return sb.toString();
	}
	
	/**
	 * Contruit une string de l'ensemble connexe du graphe et de ses informations contenues. 
	 * 
	 * @return
	 */
	
	public String graphToString() {
		StringBuilder sb = new StringBuilder();
		
		for (ComponentDataNode node : gathering()) {
			sb.append("\n");
			sb.append(node.toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	/**
	 * Ajoute un noeud fils au noeud courant.
	 * 
	 * @param node
	 * @return
	 */
	
	public ComponentDataNode addChild(ComponentDataNode node) {
		this.children.add(node);
		node.parents.add(this);
		return this;
	}
	
	/**
	 * Ajoute un noeud père au noeud courant.
	 * 
	 * @param node
	 * @return
	 */
	
	public ComponentDataNode addParent(ComponentDataNode node) {
		this.parents.add(node);
		node.children.add(this);
		return this;
	}
	
	/**
	 * Supprime un noeud fils au noeud courant.
	 * 
	 * @param node
	 * @return
	 */
	
	public ComponentDataNode removeChild(ComponentDataNode node) {
		this.children.remove(node);
		node.parents.remove(this);
		return this;
	}
	
	/**
	 * Supprime un noeud père au noeud courant.
	 * 
	 * @param node
	 * @return
	 */
	
	public ComponentDataNode removeParent(ComponentDataNode node) {
		this.parents.remove(node);
		node.children.remove(this);
		return this;
	}
	
	/**
	 * Ajoute un port au noeud courant.
	 * 
	 * @param port
	 * @return
	 */
	
	public ComponentDataNode addPort(String port) {
		this.ports.add(port);
		return this;
	}
	
	/**
	 * Supprime un port au noeud courant.
	 * 
	 * @param port
	 * @return
	 */
	
	public ComponentDataNode removePort(String port) {
		if (this.ports.remove(port))
			return this;
		return null;
	}
	
	/**
	 * Retourne un port correspondant au matching avec la sous chaîne parmi les ports possédés ou null si la sous chaine n'a pas été trouvée.
	 * Il est nécéssaire d'utiliser un système d'uri normalisé pour pouvoir utiliser cette méthode efficacement.
	 * 
	 * @param substring
	 * @return
	 */
	
	public String getPortLike(Object substring) {
		for (String port : ports) {
			if (port.contains(substring.toString()))
				return port;
		}
		return null;
	}
	
	public Set<String> getURIsLike(Object substring) {
		Set<String> uris = new HashSet<>();
		
		for (ComponentDataNode cdn : gathering()) {
			if (cdn.uri.contains(substring.toString()))
				uris.add(cdn.uri);
		}
		
		return uris;
	}
	
	/**
	 * Si le port passé en paramètre est connecté à un autre port, alors cet autre port est retourné sinon nul.
	 * 
	 * @param port
	 * @return
	 */
	
	public String getPortConnectedTo(String port) {
		for (String key : connections.keySet()) {
			if (key.equals(port))
				return connections.get(key);
			if (connections.get(key).equals(port))
				return key;
		}
		return null;
	}
	
	/**
	 * Ajoute une connection entre le port possédé et l'autre port (cible).
	 * Attention on ne peut pas connecter un port que l'ont ne possède pas en tant que ownedPort.
	 * 
	 * @param ownedPort
	 * @param otherPort
	 * @return
	 */
	
	public ComponentDataNode connect(String ownedPort, String otherPort) throws Exception {
		assert ports.contains(ownedPort);
				
		if (ports.contains(ownedPort)) {
			ComponentDataNode target = findByOwnedPort(otherPort);
			
			if (target == null)
				throw new Exception("The other port doesn't seems have component. "
						+ "Are you sure that component is linked in children or parents or adding ports ?");
			
			target.connections.put(otherPort, ownedPort);
			connections.put(ownedPort, otherPort);
		}
		else
			throw new Exception("The component doesn't own a port uri like : " + ownedPort);
		
		return this;
	}
	
	public ComponentDataNode disconnect(String port) throws Exception {
		
		if (!ports.contains(port))
			throw new Exception("Unable to disconnect a non owned port : " + port);
		
		String otherPort = connections.get(port);
		
		if (otherPort == null)
			throw new Exception("No connection known on port : " + port);
		
		ComponentDataNode target = findByOwnedPort(otherPort);
		
		if (target == null)
			throw new Exception("The other port doesn't seems have component. Are you sure that component is linked in children or parents ?");
		
		target.connections.remove(otherPort);
		connections.remove(port);
			
		return this;
		
	}
	
	/**
	 * Connecte les ports entre eux en forçant l'ajout de l'ownedPort au {@link ComponentDataNode} appellant.
	 * Suppose qu'au moins l'otherPort appartienne à un {@link ComponentDataNode}.
	 * 
	 * 
	 * @param ownedPort
	 * @param otherPort
	 * @return
	 * @throws Exception
	 */
	
	public ComponentDataNode trustedConnect(String ownedPort, String otherPort) throws Exception {
		return addPort(ownedPort).connect(ownedPort, otherPort);
	}
	
	public static void main (String[] args) {
		hugeChain();
	}
	
	public static void chain() {
		ComponentDataNode A = new ComponentDataNode("A");
		ComponentDataNode B = new ComponentDataNode("B");
		ComponentDataNode C = new ComponentDataNode("C");
		ComponentDataNode D = new ComponentDataNode("D");
		ComponentDataNode E = new ComponentDataNode("E");
		
		A.addChild(B);
		B.addChild(C);
		C.addChild(D);
		D.addChild(E);
		E.addChild(C);
	}
	
	public static void hugeChain() {
		Random rd = new Random();
		List<ComponentDataNode> nodes = new ArrayList<>();
		Chronometer chrono = new Chronometer();
		
		chrono.start();
		for (int i = 0; i < 1000000; i++) {
			ComponentDataNode node = new ComponentDataNode(new String(Integer.toString(rd.nextInt(100))));
			if (nodes.size() > 0)
				nodes.get(nodes.size()-1).addChild(node);
			nodes.add(node);
		}
		System.out.println("TEMPS DE GENERATION : " + chrono.stop().getMilliseconds() + " ms");
		
		chrono.start();
		System.out.println("TAILLE DE LA CUEILLETTE : " + nodes.get(0).gathering().size());
		System.out.println("TEMPS DE CUEILLETTE : " + chrono.stop().getMilliseconds() + " ms");
	}
	
	public static void connecting() {
		ComponentDataNode A = new ComponentDataNode("A");
		ComponentDataNode B = new ComponentDataNode("B");
		
		A.addChild(B);
		
		A.addPort("ap");
		B.addPort("bp");
		
		try { 
			A.connect("ap", "bp");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(A.graphToString());
		
		System.out.println("DISCONNECTING " + "ap" + "...");
		try {
			A.disconnect("ap");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(A.graphToString());
	}
}
