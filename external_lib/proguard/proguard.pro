-injars       gudusoft.gsqlparser.jar
-outjars      gudusoft.gsqlparser-new.jar
-libraryjars  /jre/lib/resources.jar
-libraryjars  /jre/lib/rt.jar
-libraryjars  /jre/lib/jsse.jar
-libraryjars  /jre/lib/jce.jar
-libraryjars  /jre/lib/charsets.jar
-libraryjars  /jre/lib/ext/dnsns.jar
-libraryjars  /jre/lib/ext/localedata.jar
-libraryjars  /jre/lib/ext/sunjce_provider.jar
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
