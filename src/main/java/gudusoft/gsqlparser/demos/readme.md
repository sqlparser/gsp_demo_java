## GsqlParser Use Guide

### 一、Script running mode

#### 1.Example Modify the java environment directory in setenv/setenv.bat

```bash
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_201
```

#### 2.Compile Class
eg：run the script `src/main/java/demos/checksyntax/compile_checksyntax.bat`

```
compile_checksyntax.bat
```

#### 3.Run Class
eg：run the script `src/main/java/demos/analyzeview/run_analyzeview.bat`

```
run_checksyntax.bat /f C:\data.sql /t oracle
/f sql file path
/t databse type
/d Output results folder address
```

### 二、Maven running mode

#### 1.modify the pom.xml

##### 1.1 Comment parent project

```xml
<!--  <parent>-->
<!--        <groupId>gudusoft</groupId>-->
<!--        <artifactId>gsp_java</artifactId>-->
<!--        <version>1.0-SNAPSHOT</version>-->
<!--    </parent>-->
```

##### 1.2 Annotated build configuration

```xml
<!--    <profiles>-->
<!--        <profile>-->
<!--            <id>local</id>-->
<!--            <activation>-->
<!--                <activeByDefault>true</activeByDefault>-->
<!--            </activation>-->
<!--            <dependencies>-->
<!--                <dependency>-->
<!--                    <groupId>gudusoft</groupId>-->
<!--                    <artifactId>gsqlparser</artifactId>-->
<!--                    <version>${gsp.core.version}</version>-->
<!--                </dependency>-->
<!--            </dependencies>-->
<!--        </profile>-->
<!--        <profile>-->
<!--            <id>remote</id>-->
<!--            <dependencies>-->
<!--                <dependency>-->
<!--                    <groupId>gudusoft</groupId>-->
<!--                    <artifactId>gsqlparser</artifactId>-->
<!--                    <version>${gsp.core.version}</version>-->
<!--                </dependency>-->
<!--            </dependencies>-->
<!--        </profile>-->
<!--    </profiles>-->
```

##### 1.3 Change the dependent jar package directory from lib to external_lib

```xml
  <dependency>
            <groupId>org.simpleframework</groupId>
            <artifactId>simple-xml</artifactId>
            <version>2.6.2</version>
            <scope>system</scope>
<!--    Original path：  <systemPath>${project.basedir}/external_lib/simple-xml-2.6.2.jar</systemPath>  -->
            <systemPath>${project.basedir}/external_lib/simple-xml-2.6.2.jar</systemPath>
        </dependency>

<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.41</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/external_lib/fastjson-1.2.41.jar</systemPath>
        </dependency>
        <!-- https://mvnrepository.com/artifact/com.github.junrar/junrar -->
    <dependency>
        <groupId>com.github.junrar</groupId>
        <artifactId>junrar</artifactId>
        <version>0.7</version>
        <scope>system</scope>
        <systemPath>${project.basedir}/external_lib/junrar-0.7.jar</systemPath>
    </dependency>

    <!-- https://mvnrepository.com/artifact/tk.pratanumandal/expr4j -->
    <dependency>
        <groupId>tk.pratanumandal</groupId>
        <artifactId>expr4j</artifactId>
        <version>0.0.3</version>
                <scope>system</scope>
        <systemPath>${project.basedir}/external_lib/expr4j.jar</systemPath>
    </dependency>
<!-- https://mvnrepository.com/artifact/org.jdom/jdom -->
        <dependency>
            <groupId>org.jdom</groupId>
            <artifactId>jdom</artifactId>
            <version>1.1</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/external_lib/jdom.jar</systemPath>
        </dependency>


        <dependency>
            <groupId>sqlflow</groupId>
            <artifactId>exporter</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/external_lib/sqlflow-exporter.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>sqlflow</groupId>
            <artifactId>library</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/external_lib/sqlflow-library.jar</systemPath>
        </dependency>
```

##### 1.4  Added gsqlparser dependency, which is in the lib path

```xml
<dependency>
    <groupId>sqlflow</groupId>
    <artifactId>gsqlparser</artifactId>
    <version>3.0.1.5</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/gudusoft.gsqlparser-3.0.1.5.jar</systemPath>
</dependency>
```

##### 2.Run
Execute each demo class directly
