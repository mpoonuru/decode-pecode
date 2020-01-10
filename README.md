# DECODE PCODE
[ ![Download](https://api.bintray.com/packages/sachinsh76/Maven/Decode-Pcode/images/download.svg) ](https://bintray.com/sachinsh76/Maven/Decode-Pcode/_latestVersion)
- This repository was taken and modified from https://sourceforge.net/projects/decodepcode/, which is maintained by Eric H.
- The current version is a gradle project with all dependencies pre-installed, to be used from a command line, to extract PeopleCode and SQL text from either a PeopleTools project file or directly from PeopleTools tables (using direct, read-only database access).
- You can either download fat-jar or generate executable fat jar using
* Recommended java version = 1.8

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

# Feature Enhancements:
## 1. Gradle Project
- Added gradle build tool to java project
- Written tasks to generate lib-jar and fat-jar files for the project.
- Added auto-publishing functionality to bintray public repository.

## 2. Environment Variable Support 
- Added environment variable passing functionality from the shell
- Made in co-ordination with properties file.
i.e. Priority will be given to variables passed from environment.

## 3. Custom Git Committer
- Added custom Git committer to commit extra necessary files, that needs to be versioned.
- Added last-time commiting as a part of git custom committer.

## 4.  Optional Mods - Last Changed Projects Support (In development)
- Added extra optional mods as a additional feature.
- Able to detect changed projects since last run for a batch job.

## 5. Git Auto Push Support (In development)
- Added git push support to the project.
- Auto git pull-rebase enabled. 
- Added username/password & SSH based authentication through JGit.


# Bug Fixes
- Fixed empty commits issue.

#### Note - For enhancements or bugs tracking, look at issues section :)
