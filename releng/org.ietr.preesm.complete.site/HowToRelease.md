# How to release Preesm
_authors: Clément Guy, Karol Desnos_

This guide explains the different steps to follow to create and upload a new Preesm Release. Althgouh this guide is public, it is currently only aimed for Preesm maintenance team?



## 1. Clean repositories
For each updated repository (`graphiti`, `dftools`, and/or `preesm`) follow these steps:
  
1. `pull` latest version from remote repositories;
2. checkout the `develop` branch.
3. `commit`, `stash`,or `delete` all your local change;
4. `push` your commited changes if any;
5. Make sure the Jenkins server doesn't fail.

## 2. Update changelog
Update the changelog file at the root of the `preesm` repository. It is important to document all new features, changes and fixed bugs to let Preesm Users and developers know what has changed in the new release.

## 3. Update features
For each updated repository: 

1. Open the _Feature Manifest Editor_ in eclipse:   
```org.ietr.(preesm|dftools|graphiti).feature > feature.xml > Overview tab```  
2. Update the X.Y.Z as follows:
  * X+1 → New version release (major model or ui change)
  * Y+1 → Major release (new features)
  * Z+1 → Minor release (bugfixes)
3. In the `Included Plug-ins` tab: 
  * Make sure newly created plugins are included in the released features.
  * Change version number of all plug-ins by clicking on `Versions...` within the `Plug-ins and fragments` frame. Select _Force feature version into plug-in and fragment manifests_.
  * **/!\ When updating `graphiti`, make sure the `org.jgrapht` plugin version is not updated and is still set to `0.8.2`. /!\\**
4. For each plugin of an updated repository, edit the `pom.xml` file and update the version number coherently. 

## 4. Update site
Follow these steps scrupulously to make sure former versions of Preesm are still available on the update site.

1. In your local `preesm` repository, delete the following files and folders:
  * `preesm/org.ietr.preesm.complete.site/plugins`
  * `preesm/org.ietr.preesm.complete.site/features`
  * `preesm/org.ietr.preesm.complete.site/content.jar`
  * `preesm/org.ietr.preesm.complete.site/artifacts.jar`
2. Using an ftp client, download the current version of the deleted files from preesm update-site (in `htdocs/eclipse/update-site`, requires credentials).
3. In eclipse, open `org.ietr.preesm.complete.site/site.xml`
4. In the `Managing the Site` frame, for each updated repository (`graphiti`, `dftools`, and/or `preesm`), remove the old feature, then add the new one.
5. Click on `Build All`.

## 5. Update the files on the server
1. Connect to `web.sourceforge.net` using `<your sourceforge login>,preesm`
and SFTPSSH protocol (files are under `htdocs/eclipse/update-site`);
2. Create a `backup-<qualifier of the last version>` folder under backup;
3. Move the content of the update-site folder (except the backup folder) to the newly created folder ( `backup-<qualifier of the last version>` );
4. Upload the following files/folders into the `update-site` folder:
  * `preesm/org.ietr.preesm.complete.site/plugins`
  * `preesm/org.ietr.preesm.complete.site/features`
  * `preesm/org.ietr.preesm.complete.site/web`
  * `preesm/org.ietr.preesm.complete.site/artifacts.jar`
  * `preesm/org.ietr.preesm.complete.site/content.jar`
  * `preesm/org.ietr.preesm.complete.site/index.html`
  * `preesm/org.ietr.preesm.complete.site/Logo_preesm_original.svg`
  * `preesm/org.ietr.preesm.complete.site/site.xml`
5. Test the update site!

## 6. Commit and push the changes
1. `commit` the above changes on the `develop` branches of the concerned repositories with a comment “Preparing version X.Y.Z”;
2. `push` the `develop` branch;
3. `merge` the `develop` branch into `master`:
  * `git checkout master`
  * `git merge --no-ff develop`
4. Tag the last commit with version number: `git tag “vX.Y.Z”`
5. `push`the `master` branch and `push` the tag:
  * `git push`
  * `git push origin vX.Y.Z`