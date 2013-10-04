package Show;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class Filtre extends FileFilter{
	private String[] extensions;
	private String description;
	
	public Filtre( String[] extensions, String description ){
		this.extensions =   extensions;	
		this.description = description;
		}
	
	public String getDescription(){
		return this.description;
		}
	
	public boolean appartient( String extension ){
		for (int i = 0; i < extensions.length; i++ )
		if ( extension.equals( extensions[ i ] ) )
		return true;

		return false;
		}

	public boolean accept( File fichier ){
		String suffixe, fileName;
		int i;

		if ( fichier.isDirectory() )
		return true;

		suffixe = null;
		fileName = fichier.getName();
		i = fileName.lastIndexOf('.');

		if ( i > 0 && i < fileName.length() - 1 )
		suffixe = fileName.substring( i + 1 ).toLowerCase();

		return suffixe != null && appartient( suffixe );
		}
}
