package decodepcode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeopleCodeContainerFromFile extends PeopleCodeContainer implements  PeopleToolsObject
{
	File binFile;
	String key;
	Map<Integer, String>references = new HashMap<>();
	String[] keys;
	int objType;
	static Logger logger = Logger.getLogger(PeopleCodeContainerFromFile.class.getName());
	final static String BYTEARRAYTEXT = "_bytearray.txt";

	private static File makeProperBinFile(File f1) throws FileNotFoundException, IOException {
		if (f1 == null || !f1.getName().contains(BYTEARRAYTEXT))
			throw new IllegalArgumentException("" + f1 + " does not contain " + BYTEARRAYTEXT);
		if (!f1.exists())
			throw new IllegalArgumentException(f1.getAbsolutePath() + " not found");
		File ff1 = new File(f1.getParentFile(), f1.getName().replace(BYTEARRAYTEXT, ".bin"));
		try (FileInputStream fis = new FileInputStream(f1);
				FileOutputStream fos = new FileOutputStream(ff1);
				BufferedReader r = new BufferedReader(new InputStreamReader(fis, "utf-8"))) {
			String l;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((l = r.readLine()) != null) {
				l = l.replaceAll("\\[", "").replaceAll("\\]", "").trim();
				if (l.endsWith(",")) {
					l = l.substring(0, l.length() - 2);
				}
				String[] ss = l.split(",");
				if (ss.length < 2)
					throw new IllegalArgumentException("Can't parse " + f1);
				for (int i = 0; i < ss.length; i++) {
					try {
						int bb = Integer.parseInt(ss[i].trim());
						baos.write(bb);
					} catch (NumberFormatException e) {
						logger.log(Level.INFO, "cannot parse", e);
					}
				}
			}
			baos.flush();
			fos.write(baos.toByteArray());
		}
		return ff1;
	}

	public PeopleCodeContainerFromFile( File _binFile) throws IOException
	{
		this(_binFile, null);
	}
	public PeopleCodeContainerFromFile( File _binFile, PeopleToolsObject obj) throws IOException
	{
		binFile = _binFile;
		String binFileName = binFile.getName();
		if (binFileName.endsWith(BYTEARRAYTEXT)) {
			binFile = makeProperBinFile(binFile);
			binFileName = binFile.getName();
		}
		InputStream io = new FileInputStream(binFile);
		logger.fine("Reading byte code from " + binFile);
		bytes = new byte[(int) binFile.length()];
		io.read( bytes);
		io.close();
		if (binFileName.endsWith(".bin"))
		{
			key = binFileName.substring(0, binFileName.length() - 4);
			String txtFileName = key + ".txt";
			File txtFile = new File( binFile.getParent(), txtFileName);
			if (txtFile.exists())
			{
				logger.fine("Trying to read PeopleCode from " + txtFile);
				readPeopleCodeTextFromFile(txtFile);
			}
			File refFile = new File( binFile.getParent(), key + ".references");
			if (refFile.exists())
			{
				logger.fine("Trying to read References from " + refFile);
				Properties p = new Properties();
				p.load(new FileInputStream(refFile));
				for ( Object r: p.keySet())
				{
					references.put( new Integer((String) r), (String) p.get(r));
				}
			}
			File stampFile = new File( binFile.getParent(), key + ".last_update");
			if (stampFile.exists())
			{
				logger.fine("Trying to read last-update info from " + stampFile);
				BufferedReader br = new BufferedReader(new FileReader(stampFile));
				String line = br.readLine();
				try {
					setLastChangedDtTm(ProjectReader.df2.parse(line));
				} catch ( ParseException e) {}
				setLastChangedBy(br.readLine());
				br.close();
			}
			if (obj == null)
			{
				try
				{
					parseFileName();
				}
				catch (IllegalArgumentException ia)
				{
					logger.severe("Not setting pcode type and keys: cannot parse file name");
					logger.severe(ia.getMessage());
				}
			}
			else
			{
				objType = obj.getPeopleCodeType();
				keys = obj.getKeys();
				key = PeopleCodeContainer.objectTypeStr(objType);
				for (String k: keys)
					if (k != null && k.length() > 0) {
						key += "-" + k;
					}
			}
		} else {
			key = binFileName;
		}
	}

	void parseFileName()
	{
		// e.g. App_Package_PeopleCode-PT_ANALYTICMODELDEFN-RuleExpressions-Assignment-OnExecute
		String[] parts = key.split("-");
		if (parts.length < 2)
			throw new IllegalArgumentException("Name convention for PeopleCode file '" + key + "' not used");

		objType = PeopleCodeContainer.objectTypeFromString(parts[0]);
		if (objType < 0)
			throw new IllegalArgumentException("Don't recognize PeopleCode type '" + parts[0] + "'");
		keys = new String[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			keys[i-1] = parts[i];
		}

	}

	@Override
	public String getCompositeKey() {
		return key;
	}

	@Override
	String getReference(int nameNum) {
		return references.get(nameNum);
	}

	public static void main(String[] a) throws IOException
{
		Writer w = new OutputStreamWriter(new FileOutputStream("test.pcode"), "utf-8");
		try {
		PeopleCodeContainerFromFile c = new PeopleCodeContainerFromFile(
					new File("./trunk/src/resources/",
							"Application_Engine_PeopleCode-BI_GTA_EXP-EXPORT-GBL-default-1900-01-01-Step01-OnExecute.bytecode"));
			new PeopleCodeParser().parse(c, w);
			w.close();
			c.readPeopleCodeTextFromFile(
					new File("./trunk/src/resources/",
							"Application_Engine_PeopleCode-BI_GTA_EXP-EXPORT-GBL-default-1900-01-01-Step01-OnExecute.pcode"));
		new PeopleCodeParser().reverseEngineer(c);
	} catch (Exception e) {
			w.flush();
		e.printStackTrace();
	}
}

@Override
void writeReferencesInDirectory(File f) throws IOException {
	throw new IllegalArgumentException("Class " + getClass().getName() + " can not write its contents back to the file system");
}

@Override
public String[] getKeys() {
	return keys;
}

@Override
public int getPeopleCodeType() {
	return objType;
}
@Override
public int[] getKeyTypes() {
	return CreateProjectDefProcessor.getObjTypesFromPCType(objType, keys);
}

}
