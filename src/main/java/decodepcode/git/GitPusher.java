package decodepcode.git;

import decodepcode.git.auth.Authenticator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.StoredConfig;
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

        // TODO: 30/12/19 separate committer code from pusher code
        // TODO: 16/12/19 add proper author and committer for last-time.txt file
        if(props.getProperty("gitdir").equals(props.getProperty("lastTimeFilePath"))) {
            git.add().addFilepattern("last-time.txt").call();
            try {
                git.commit().setMessage("added last time file to track job scheduling")
                        .setAuthor("DecodePcode", "decodepcode@noorg.com")
                        .setAllowEmpty(false)
                        .call();
            } catch (EmptyCommitException e) {
                System.out.println("Avoided making an empty commit");
            }
        }

        String gitUserName = Utils.getUserName(props.getProperty("gituser"));
        String gitUserEmail = Utils.getUserEmail(props.getProperty("gituser"));

        if(props.containsKey("logChangedProjects")) {
            String fileName = props.getProperty("changedProjectFileName");
            git.add().addFilepattern("changed-projects").call();
            if (fileName != null) {
                git.add().addFilepattern(fileName).call();
            }
            try {
                git.commit().setMessage("updated projects file to track changed projects")
                        .setAuthor(gitUserName, gitUserEmail)
                        .setAllowEmpty(false)
                        .call();
            }  catch (EmptyCommitException e) {
                System.out.println("Avoided making an empty commit");
            }
        }

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
