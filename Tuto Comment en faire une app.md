## Tuto : Comment faire d'un .jar une application que l'on peut lancer

# Compiler : Créer le dossier out avec les tout les fichiers en .class

```
cmd :
"D:\JavaPortable\jdk-21.0.7+6\bin\javac.exe" -d out -cp lib/flatlaf-3.6.jar ^client/*.java ^common/*.java ^games/*.java ^server/*.java ^utils/*.java ^utils/command/*.java
```

# Mettre au meme niveau que le dossier out un fichier MANIFEST.MF avec : 
```
Main-Class: client.New
Class-Path: lib/flatlaf-3.6.jar

```
Ligne vide obligatoire à la fin


# Créer le .jar
```
cmd : 
"D:\JavaPortable\jdk-21.0.7+6\bin\jar.exe" cfm ChatMaster.jar MANIFEST.MF -C out .
```

# lancer avec javaw car java.exe est black list 
```
cmd :
"D:\JavaPortable\jdk-21.0.7+6\bin\javaw.exe" -jar ChatMaster.jar
```

ça fonctionne donc faire un .bat avec ça et profiter de la vie
