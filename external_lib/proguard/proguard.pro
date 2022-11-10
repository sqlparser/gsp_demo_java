-injars       gudusoft.gsqlparser.jar
-outjars      gudusoft.gsqlparser-new.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/resources.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/rt.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/jsse.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/jce.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/charsets.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/ext/dnsns.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/ext/localedata.jar
-libraryjars  /usr/lib/jvm/temurin-8-jdk-amd64/jre/lib/ext/sunjce_provider.jar
-overloadaggressively
-defaultpackage ''
-allowaccessmodification
-dontoptimize
-keepattributes Signature
-keepattributes Signature,Exceptions,*Annotation*,
                InnerClasses,PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable
-keep public class *
{
public protected *;
}
-keep class gudusoft.gsqlparser.dlineage.metadata.**{*;}
-keep class gudusoft.gsqlparser.dlineage.dataflow.model.**{*;}
