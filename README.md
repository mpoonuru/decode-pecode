# DECODE PCODE
[ ![Download](https://api.bintray.com/packages/sachinsh76/Maven/Decode-Pcode/images/download.svg) ](https://bintray.com/sachinsh76/Maven/Decode-Pcode/_latestVersion)
- This repository was taken and modified from https://sourceforge.net/projects/decodepcode/, which is maintained by Eric H.
- The current version is a gradle project with all dependencies pre-installed, to be used from a command line, to extract PeopleCode and SQL text from either a PeopleTools project file or directly from PeopleTools tables (using direct, read-only database access).
- You can either download fat-jar or generate executable fat jar using

```cmd
    ./gradlew build
    ./gradlew fatJar
```

- It will generate jar file under build/libs directory. 
- Before running the jar file, make sure properties file is under the same directory as jar file

```cmd
    java -jar DecodePcode-0.0.1-fat.jar since-days 30
```

- For more information about tool and properties file, go through DecodePC.properties and README.txt.

# Miscellaneous:
- Minimum permissions for the SQL user that you specify in the .properties file:
select rights on PSPCMPROG PSPCMNAME PSSQLDEFN PSSQLTEXTDEFN PSPROJECTITEM PSPACKAGEDEFN and (ptools >= 8.52) PSPCMTXT; 
- No insert/delete/alter permissions required.
- To retrieve or browse the source code, please use the Code link on the SourceForge project (https://sourceforge.net/projects/decodepcode).