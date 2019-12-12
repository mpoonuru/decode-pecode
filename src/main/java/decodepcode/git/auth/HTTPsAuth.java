package decodepcode.git.auth;

import org.eclipse.jgit.annotations.NonNull;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

class HTTPsAuth {
    String username;
    String password;

    public HTTPsAuth(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }

    public UsernamePasswordCredentialsProvider getCredentialProvider(){
        return new UsernamePasswordCredentialsProvider(username, password);
    }

}
