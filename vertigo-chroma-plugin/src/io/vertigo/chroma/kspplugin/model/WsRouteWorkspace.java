package io.vertigo.chroma.kspplugin.model;

import java.util.List;

/**
 * Stocke l'ensemble des routes de webservices de tout un workspace.
 */
public class WsRouteWorkspace {

	private final List<WsRoute> wsRoutes;

	public WsRouteWorkspace(List<WsRoute> wsRoutes) {
		this.wsRoutes = wsRoutes;
	}

	public List<WsRoute> getWsRoutes() {
		return wsRoutes;
	}
}
