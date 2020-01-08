package decodepcode.git;

import decodepcode.git.auth.Authenticator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.URIish;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Properties;

public class GitPusher {

    Git git;

    public GitPusher(String folderPath) throws IOException {
        this.git = Git.open(new File(folderPath));
    }

    //todo : setup upstream for the master while fetching data for the first time
    public void push(Properties props) throws URISyntaxException, GitAPIException {

        git.remoteAdd().setName("origin").setUri(new URIish(props.getProperty("gitRemoteUrl"))).call();
        PullCommand pullCommand;
        PushCommand pushCommand;

        if (props.containsKey("gitUseSSH")) {
            String key = props.containsKey("gitSSHPrivateKeyPath") ? props.getProperty("gitSSHPrivateKeyPath"): null;
            String passphrase = props.containsKey("gitSSHPassphrase") ? props.getProperty("gitSSHPassphrase") : null;
            Boolean disableHostCheck = !props.containsKey("gitSSHDisableHostCheck") || Boolean.parseBoolean(props.getProperty("gitSSHDisableHostCheck"));

            pullCommand = Authenticator.authenticatedPullCommand(git, key, passphrase, disableHostCheck);
            pushCommand = Authenticator.authenticatedPushCommand(git, key, passphrase, disableHostCheck);
        } else {
            String username = props.getProperty("gitUserName");
            String password = props.getProperty("gitPassword");
            pullCommand = Authenticator.authenticatedPullCommand(git, username, password);
            pushCommand = Authenticator.authenticatedPushCommand(git, username, password);
        }

        try {
            System.out.println("Pulling code from repo ");
            PullResult pullResult = pullCommand.setRebase(true)
                    .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                    .call();
            System.out.println(pullResult.toString());
            System.out.println("Completed pulling code from repo ");

        } catch (GitAPIException e) {
            e.printStackTrace();
        }


        System.out.println("\nPushing code to the repo ");
        Iterable<PushResult> pushResults = pushCommand.setRemote("origin")
                .add("master")
                .setProgressMonitor(new TextProgressMonitor(new PrintWriter(System.out)))
                .call();

        for (PushResult pushResult : pushResults) {
            for (RemoteRefUpdate update : pushResult.getRemoteUpdates()) {
                System.out.println(update.getStatus());
            }
            System.out.println(pushResult.toString());
        }
        System.out.println("Finished pushing code to the repo ");
        git.close();
    }
}
