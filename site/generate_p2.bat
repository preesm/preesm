REM -compress -reusePack200Files 
set SITE=D:/Projets/Preesm/trunk/site
C:/"Program Files"/eclipse/eclipse -application org.eclipse.equinox.p2.metadata.generator.EclipseGenerator -updateSite %SITE% -site file:%SITE%site.xml -metadataRepository file:%SITE% -metadataRepositoryName "PREESM Update Site" -artifactRepository file:%SITE% -artifactRepositoryName "PREESM Artifacts" -noDefaultIUs -vmargs -Xmx256m
