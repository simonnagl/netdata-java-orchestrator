package org.firehol.netdata.testutils;

/**
 * A Security Manager that allowes everything but throws an ExitException on calls to System.exit()
 * 
 * @author Simon Nagl
 *
 */
public class NoExitSecurityManager extends SecurityManager {
    @Override
    public void checkExit(int status) 
    {
        super.checkExit(status);
			throw new ExitException(status);
    }

}
