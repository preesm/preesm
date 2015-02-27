/**
 * 
 */
package org.ietr.preesm.utils.paths;

/**
 * Tools to manipulate paths
 * 
 * @author mpelcat
 */
public class PathTools {

	/**
	 * Returns the same path if it started with /projectName or prefixes it with
	 * /projectName/
	 * 
	 * @param relative
	 *            Project absolute or relative path.
	 * @param projectName
	 *            Name of the project prefixing the path.
	 * @return Project absolute path.
	 */
	public static String getAbsolutePath(String relative, String projectName) {
		String absolute = new String();

		if (relative.startsWith("/" + projectName)) {
			return relative;
		} else {
			absolute = "/" + projectName + "/" + relative;
		}

		return absolute;
	}
}
