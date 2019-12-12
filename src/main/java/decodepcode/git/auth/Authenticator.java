package decodepcode.git.auth;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;

public class Authenticator {

    public static PushCommand authenticatedPushCommand(Git git) {
        return authenticatedPushCommand(git, null, null, true);
    }

    public static PullCommand authenticatedPullCommand(Git git) {
        return authenticatedPullCommand(git, null, null, true);
    }

    public static PushCommand authenticatedPushCommand(Git git, String privateKey, String passPhrase, Boolean disableHostCheck) {
        return git.push().setTransportConfigCallback(new SSHAuth(privateKey, passPhrase, disableHostCheck).getSSHTransportConfig());
    }

    public static PullCommand authenticatedPullCommand(Git git, String privateKey, String passPhrase, Boolean disableHostCheck) {
        return git.pull().setTransportConfigCallback(new SSHAuth(privateKey, passPhrase, disableHostCheck).getSSHTransportConfig());
    }

    public static PushCommand authenticatedPushCommand(Git git, String username, String password) {
        return git.push().setCredentialsProvider(new HTTPsAuth(username, password).getCredentialProvider());
    }

    public static PullCommand authenticatedPullCommand(Git git, String username, String password) {
        return git.pull().setCredentialsProvider(new HTTPsAuth(username, password).getCredentialProvider());
    }
}


