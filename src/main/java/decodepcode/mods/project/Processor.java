package decodepcode.mods.project;

import java.io.IOException;
import java.sql.*;
import java.util.*;

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

    private Set<String> executeQuery(String query) throws SQLException {
        PreparedStatement st0 = connection.prepareStatement(query);
        st0.setTimestamp(1, sinceDate);
        ResultSet rs = st0.executeQuery();
        Set <String> projects = new HashSet<>();
        while (rs.next())
        {
            projects.add(rs.getString("PROJECTNAME"));
        }
        rs.close();
        st0.close();
        return projects;
    }

    private String [] fetchChangedProjects() throws SQLException {
        String changedProjectsFromDefinitions = "SELECT p.PROJECTNAME FROM SYSADM.PSPROJECTDEFN p WHERE LASTUPDDTTM > ?";
        String changedProjectsFromPeopleCodeAndSql = "SELECT ITEM.PROJECTNAME FROM SYSADM.PSPROJECTITEM item, SYSADM.PSPCMPROG prog WHERE item.OBJECTVALUE1 = PROG.OBJECTVALUE1 AND PROG.LASTUPDDTTM > ? GROUP BY ITEM.PROJECTNAME";
        Set<String> projects1 = executeQuery(changedProjectsFromDefinitions);
        Set<String> projects2 = executeQuery(changedProjectsFromPeopleCodeAndSql);
        projects1.addAll(projects2);
        return projects1.toArray(new String[0]);
    }

    public void process() throws SQLException, IOException {
        String [] projects = fetchChangedProjects();
        fileProcessor.saveFile(projects);
    }
}
