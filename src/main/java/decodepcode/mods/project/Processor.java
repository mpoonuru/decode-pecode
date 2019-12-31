package decodepcode.mods.project;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Processor {

    Connection connection;
    Timestamp sinceDate;
    Properties properties;
    FileProcessor fileProcessor;

    public Processor(Connection connection, Timestamp sinceDate, Properties properties, FileProcessor fileProcessor) {
        this.connection = connection;
        this.sinceDate = sinceDate;
        this.properties = properties;
        this.fileProcessor = fileProcessor;
    }

    private String [] fetchChangedProjects() throws SQLException {
        String query = "SELECT p.PROJECTNAME FROM SYSADM.PSPROJECTDEFN p WHERE LASTUPDDTTM > ?";
        PreparedStatement st0 = connection.prepareStatement(query);
        st0.setTimestamp(1, sinceDate);
        ResultSet rs = st0.executeQuery();
        List <String> projects = new ArrayList<>();
        while (rs.next())
        {
            projects.add(rs.getString("PROJECTNAME"));
        }
        rs.close();
        st0.close();
        return projects.toArray(new String[0]);
    }

    public void process() throws SQLException, IOException {
        String [] projects = fetchChangedProjects();
        fileProcessor.saveFile(projects);
    }
}
