package decodepcode.git.auth;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;

class SSHAuth {

    String privateKeyPath;
    String passPhrase;
    Boolean disableHostKeyCheck;

    public SSHAuth(@Nullable String privateKeyPath, @Nullable String passPhrase, Boolean disableHostKeyCheck) {
        this.privateKeyPath = privateKeyPath;
        this.passPhrase = passPhrase;
        this.disableHostKeyCheck = disableHostKeyCheck;
    }

    public SshTransportConfigCallback getSSHTransportConfig () {
        return new SshTransportConfigCallback();
    }

    public class SshTransportConfigCallback implements TransportConfigCallback {
        private final SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {

            @Override
            protected void configure(OpenSshConfig.Host hc, Session session) {
                if (SSHAuth.this.disableHostKeyCheck) {
                    session.setConfig("StrictHostKeyChecking", "no");
                }
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                if  (privateKeyPath != null) {
                    defaultJSch.addIdentity(privateKeyPath, passPhrase);
                }
                return defaultJSch;
            }
        };

        @Override
        public void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        }
    }
}

