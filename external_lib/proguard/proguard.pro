-injars       gudusoft.gsqlparser.jar
-outjars      gudusoft.gsqlparser-new.jar
-libraryjars  D:/env/java/1.8/jre/lib/resources.jar
-libraryjars  D:/env/java/1.8/jre/lib/rt.jar
-libraryjars  D:/env/java/1.8/jre/lib/jsse.jar
-libraryjars  D:/env/java/1.8/jre/lib/jce.jar
-libraryjars  D:/env/java/1.8/jre/lib/charsets.jar
-libraryjars  D:/env/java/1.8/jre/lib/ext/dnsns.jar
-libraryjars  D:/env/java/1.8/jre/lib/ext/localedata.jar
-libraryjars  D:/env/java/1.8/jre/lib/ext/sunjce_provider.jar
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