package com.hvilela.server;

import javax.security.auth.login.LoginException;

import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.kernel.KernelAppContext;

/**
 * @author Henrique
 *
 */
public class Authenticator implements IdentityAuthenticator {
	@Override
	public void assignContext(KernelAppContext kernelAppContext) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public Identity authenticateIdentity(IdentityCredentials identityCredentials) throws LoginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSupportedCredentialTypes() {
		// TODO Auto-generated method stub
		return null;
	}
}