package net.inveed.gwt.server.auth;

import net.inveed.gwt.editor.shared.auth.AuthorizationResponse;

public interface IUserCredential {
	AuthorizationResponse authorize();

}
