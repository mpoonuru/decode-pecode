package decodepcode.mods.project;

import decodepcode.JSONBuilder;

import java.io.Serializable;

public class Revision implements Serializable {
    String uuid;
    String [] projects;

    public Revision(String uuid, String[] projects) {
        this.uuid = uuid;
        this.projects = projects;
    }

    @Override
    public String toString() {
        return JSONBuilder.build().toJson(this);
    }
}
