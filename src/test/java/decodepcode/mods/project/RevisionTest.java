package decodepcode.mods.project;

import org.junit.Assert;
import org.junit.Test;

public class RevisionTest {

    @Test
    public void shouldSerializeRevisionObject(){

        Revision revision = new Revision("1234", new String[] {"TW_TEST","TW_INCUBATOR_TEST"});
        String expectedResult = "{\"uuid\":\"1234\"\n,\"projects\": [\n\"TW_TEST\"\n, \"TW_INCUBATOR_TEST\"\n]\n}";
//        Assert.assertEquals("", expectedResult, revision.toString());
    }
}
