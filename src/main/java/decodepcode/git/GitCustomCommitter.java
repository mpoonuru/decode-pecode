package decodepcode.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.EmptyCommitException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GitCustomCommitter {

    Git git;

    public GitCustomCommitter(String folderPath) throws IOException {
        this.git = Git.open(new File(folderPath));
    }

    public void commit(Properties props) throws GitAPIException {

        if (props.getProperty("gitdir").equals(props.getProperty("lastTimeFilePath"))) {
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

        if (props.containsKey("logChangedProjects")) {
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
            } catch (EmptyCommitException e) {
                System.out.println("Avoided making an empty commit");
            }
        }
    }
}
