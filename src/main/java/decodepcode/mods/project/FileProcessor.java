package decodepcode.mods.project;
import org.eclipse.jgit.annotations.Nullable;

import java.io.*;

public class FileProcessor {

    String rootDir;
    String fileName;
    @Nullable String dirName;
    public static String recentFileName = "recent.txt";

    public FileProcessor(String rootDir, String fileName, String dirName) {
        this.rootDir = rootDir;
        this.fileName = fileName;
        this.dirName = dirName;
    }

    public FileProcessor(String rootDir, String fileName) {
        this.rootDir = rootDir;
        this.fileName = fileName;
    }

    private String getDirPath() {
        if (dirName != null) {
            return rootDir + File.separator + dirName;
        }
        return rootDir;
    }

    private String getFilePath() {
        return getDirPath() + File.separator + fileName;
    }

    private String getRecentFilePath() {
        return getDirPath() + File.separator + recentFileName;
    }

    public void createProjectsChangedDir() {
        File file = new File(getDirPath());
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }

    public void saveFile(String[] content) throws IOException {
        createProjectsChangedDir();
        FileWriter writer = new FileWriter(getFilePath());
        Revision revision = new Revision(fileName.split("\\.")[0], content);
        writer.write(revision.toString());
        writer.close();

        if (dirName != null) {
            FileWriter recentWriter = new FileWriter(getRecentFilePath());
            recentWriter.write(fileName);
            recentWriter.close();
        }
    }
}
