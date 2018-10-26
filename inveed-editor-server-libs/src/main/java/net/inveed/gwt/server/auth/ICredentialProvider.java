package net.inveed.gwt.server.auth;

import net.inveed.commons.NumberedException;

public interface ICredentialProvider {

	IUserCredential getUserCredential(String username, String password) throws NumberedException;

}
