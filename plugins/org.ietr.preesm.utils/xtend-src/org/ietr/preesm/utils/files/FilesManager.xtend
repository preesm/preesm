/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.ietr.preesm.utils.files;

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.Arrays
import java.util.Collections
import java.util.jar.JarEntry
import java.util.jar.JarFile
import org.eclipse.core.runtime.Assert

import static org.ietr.preesm.utils.files.Result.*

/**
 * Utility class to manipulate files. It brings everything needed to extract files
 * from a jar plugin to the filesystem, check if 2 files are identical, read/write
 * files, etc.
 *
 * Code adapted from ORCC (net.sf.orcc.util, https://github.com/orcc/orcc)
 * @author Antoine Lorence
 *
 */
class FilesManager {

	static val BUFFER_SIZE = 1024

	/**
	 * Returns the File found at the given path
	 *
	 * @param path
	 * 			path to the file
	 * @param bundleFilter
	 * 			A filter to indicate in which bundle to look for
	 */
	static def File getFile(String path, String bundleFilter) {
		val sanitizedPath = path.sanitize
		val file = new File(sanitizedPath)
		if(file !== null && file.exists) return file else return null
	}

	/**
	 * <p>Copy the file or the folder at given <em>path</em> to the given
	 * <em>target folder</em>.</p>
	 *
	 * <p>It is important to understand that the resource (file or folder) at the given path
	 * will be copied <b>into</b> the target folder. For example,
	 * <code>extract("/path/to/file.txt", "/home/johndoe")</code> will copy <em>file.txt</em>
	 * into <em>/home/johndoe</em>, and <code>extract("/path/to/MyFolder", "/home/johndoe")</code>
	 * will create <em>MyFolder</em> directory in <em>/home/johndoe</em> and copy all files
	 * from the source folder into it.
	 * </p>
	 *
	 * @param path The path of the source (folder or file) to copy
	 * @param targetFolder The directory where to copy the source element
	 * @param bundleFilter
	 * 			A filter to indicate in which bundle to look for
	 * @return A Result object counting exactly how many files have been really
	 * 		written, and how many haven't because they were already up-to-date
	 * @throws FileNotFoundException If not resource have been found at the given path
	 */
	def static Result extract(String path, String targetFolder, String bundleFilter) throws IOException , URISyntaxException {
		val targetF = new File(targetFolder.sanitize)
		val url = getUrl(path, bundleFilter)

		if (url === null) {
			throw new FileNotFoundException(path)
		}
		if (url.protocol.equals("jar")) {
			val splittedURL = Arrays.asList(url.file.split("!"))
			val fileUri = new URI(splittedURL.head)
			val jar = new JarFile(new File(fileUri))
			return jarExtract(jar, splittedURL.last, targetF)
		} else {
			return fsExtract(new File(url.toURI), targetF)
		}
	}

	/**
	 * Copy the given <i>source</i> file to the given <em>targetFile</em>
	 * path.
	 *
	 * @param source
	 * 			An existing File instance
	 * @param targetFolder
	 * 			The target folder to copy the file
	 */
	private def static Result fsExtract(File source, File targetFolder) throws IOException {
		if (!source.exists) {
			throw new FileNotFoundException(source.path)
		}
		val target = new File(targetFolder, source.name)
		if (source.file) {
			if (source.isContentEqual(target)) {
				return newCachedInstance
			} else {
				return new FileInputStream(source).streamExtract(target)
			}
		} else if (source.directory)
			return source.fsDirectoryExtract(target)
	}

	/**
	 * Copy the given <i>source</i> directory and its content into
	 * the given <em>targetFolder</em> directory.
	 *
	 * @param source
	 * 			The source path of an existing file
	 * @param targetFolder
	 * 			Path to the folder where source will be copied
	 */
	private def static fsDirectoryExtract(File source, File targetFolder) throws IOException {
		Assert.isTrue(source.directory)
		if (!targetFolder.exists)
			Assert.isTrue(targetFolder.mkdirs)
		else
			Assert.isTrue(targetFolder.directory)

		val result = newInstance
		for (file : source.listFiles) {
			result.merge(
				file.fsExtract(targetFolder)
			)
		}
		return result
	}

	/**
	 * Starting point for extraction of a file/folder resource from a jar.
	 */
	private def static jarExtract(JarFile jar, String path, File targetFolder) throws IOException {
		val updatedPath = if (path.startsWith("/")) {
				path.substring(1)
			} else {
				path
			}

		val entry = jar.getJarEntry(updatedPath)

		// Remove the last char if it is '/'
		val name = if (entry.name.endsWith("/"))
				entry.name.substring(0, entry.name.length - 1)
			else
				entry.name
		val fileName = if (name.lastIndexOf("/") != -1)
				name.substring(name.lastIndexOf("/"))
			else
				name

		if (entry.directory) {
			return jarDirectoryExtract(jar, entry, new File(targetFolder, fileName))
		} else {
			val entries = Collections::list(jar.entries).filter[name.startsWith(updatedPath)]
			if (entries.size > 1) {
				return jarDirectoryExtract(jar, entry, new File(targetFolder, fileName))
			} else {
				return jarFileExtract(jar, entry, new File(targetFolder, fileName))
			}
		}
	}

	/**
	 * Extract all files in the given <em>entry</em> from the given <em>jar</em> into
	 * the <em>target folder</em>.
	 */
	private def static jarDirectoryExtract(JarFile jar, JarEntry entry, File targetFolder) throws IOException {
		val prefix = entry.name
		val entries = Collections::list(jar.entries).filter[name.startsWith(prefix)]
		val result = newInstance
		for (e : entries) {
			result.merge(
				jarFileExtract(jar, e, new File(targetFolder, e.name.substring(prefix.length)))
			)
		}
		return result
	}

	/**
	 * Extract the file <em>entry</em> from the given <em>jar</em> into the <em>target
	 * file</em>.
	 */
	private def static jarFileExtract(JarFile jar, JarEntry entry, File targetFile) throws IOException {
		targetFile.parentFile.mkdirs
		if (entry.directory) {
			targetFile.mkdir
			return newInstance
		}
		if (jar.getInputStream(entry).isContentEqual(targetFile)) {
			return newCachedInstance
		} else {
			return jar.getInputStream(entry).streamExtract(targetFile)
		}
	}

	/**
	 * Copy the content represented by the given <em>inputStream</em> into the
	 * <em>target file</em>. No checking is performed for equality between input
	 * stream and target file. Data are always written.
	 *
	 * @return A Result object with information about extraction status
	 */
	private def static streamExtract(InputStream inputStream, File targetFile) throws IOException {
		if (!targetFile.parentFile.exists) {
			targetFile.parentFile.mkdirs
		}
		val bufferedInput = new BufferedInputStream(inputStream)

		val fileOutputStream = new FileOutputStream(targetFile)
		val outputStream = new BufferedOutputStream(fileOutputStream)

		val byte[] buffer = newByteArrayOfSize(BUFFER_SIZE)
		var readLength = 0
		while ((readLength = bufferedInput.read(buffer)) != -1) {
			outputStream.write(buffer, 0, readLength)
		}
		bufferedInput.close
		outputStream.close
		fileOutputStream.close


		return newOkInstance
	}

	/**
	 * Search on the file system for a file or folder corresponding to the
	 * given path. If not found, search on the current classpath. If this method
	 * returns an URL, it always represents an existing file.
	 *
	 * @param path
	 * 			A path
	 * @param bundleFilter
	 * 			A filter to indicate in which bundle to look for
	 * @return
	 * 			An URL for an existing file, or null
	 */
	def static URL getUrl(String path, String ... bundleNames) throws MalformedURLException , IOException {
		return URLResolver.findFirstInPluginList(path, bundleNames);
	}

	/**
	 * Check if given a CharSequence have exactly the same content
	 * than file b.
	 */
	static def boolean isContentEqual(CharSequence a, File b) throws IOException {
		return new ByteArrayInputStream(a.toString.bytes).isContentEqual(b)
	}

	/**
	 * Check if given File a have exactly the same content
	 * than File b.
	 */
	static def boolean isContentEqual(File a, File b) throws IOException {
		val fileInputStream = new FileInputStream(a)
		val contentEqual = fileInputStream.isContentEqual(b)
		fileInputStream.close
		return contentEqual
	}

	/**
	 * <p>Compare the content of input stream <em>a</em> and file <em>b</em>.
	 * Returns true if the </p>
	 *
	 * <p><strong>Important</strong>: This method will close the input stream
	 * <em>a</em> before returning the result.</p>
	 *
	 * @param a An input stream
	 * @param b A file
	 * @return true if content in a is equals to content in b
	 */
	static def boolean isContentEqual(InputStream a, File b) throws IOException {
		if(!b.exists) return false
		val inputStreamA = new BufferedInputStream(a)
		val inputStreamB = new BufferedInputStream(new FileInputStream(b))

		var byteA = 0
		var byteB = 0
		do {
			byteA = inputStreamA.read
			byteB = inputStreamB.read
		} while (byteA == byteB && byteA != -1)
		inputStreamA.close
		inputStreamB.close

		return byteA == -1
	}

	/**
	 * Write <em>content</em> to the given <em>targetFolder</em> in a new file called
	 * <em>fileName</em>. This method will write the content to the target file only if
	 * it is empty or if its content is different than that given.
	 *
	 * @param content The text content to write
	 * @param targetFolder The folder where the file should be created
	 * @param fileName The name of the new file
	 */
	static def Result writeFile(CharSequence content, String targetFolder, String fileName) throws IOException {
		return content.writeFile(new File(targetFolder.sanitize, fileName))
	}

	/**
	 * Write <em>content</em> to the given file <em>path</em>. This method will write the
	 * content to the target file only if it is empty or if its content is different than
	 * that given.
	 *
	 * @param content The text content to write
	 * @param path The path of the file to write
	 */
	static def Result writeFile(CharSequence content, String path) throws IOException {
		return content.writeFile(new File(path.sanitize))
	}

	/**
	 * Write the <em>content</em> into the <em>targetFile</em> only if necessary.
	 */
	static def Result writeFile(CharSequence content, File targetFile) throws IOException {
		if (content.isContentEqual(targetFile)) {
			return newCachedInstance
		}

		if (!targetFile.exists) {
			targetFile.parentFile.mkdirs
			targetFile.createNewFile
		}
		val ps = new PrintStream(new FileOutputStream(targetFile))
		ps.print(content)
		ps.close
		return newOkInstance
	}

	/**
	 * Read the file at the given <em>path</em> and returns its content
	 * as a String.
	 *
	 * @param path
	 * 			The path of the file to read
	 * @returns
	 * 			The content of the file
	 * @throws FileNotFoundException
	 * 			If the file doesn't exists
	 */
	static def String readFile(String path, String ... bundleNames) throws IOException , URISyntaxException {
		val url = getUrl(path, bundleNames)
		if (url === null) {
			throw new FileNotFoundException(path)
		}
		var InputStream in = url.openStream()
		var BufferedReader reader = new BufferedReader(new InputStreamReader(in))
		var StringBuilder builder;
		var String line;
		while ((line = reader.readLine()) !== null) {
			builder.append(line+"\n");
		}
		reader.close();
		return builder.toString
	}

	/**
	 * Transform the given path to a valid filesystem one.
	 *
	 * <ul>
	 * <li>It replaces first '~' by the home directory of the current user.</li>
	 * </ul>
	 */
	static def String sanitize(String path) {

		// We use the following construction because Xtend infer '~' as a String instead of a char
		// path.substring(0,1).equals('~')
		if (!path.nullOrEmpty && path.substring(0, 1).equals('~')) {
			val builder = new StringBuilder(System::getProperty("user.home"))
			builder.append(File.separatorChar).append(path.substring(1))
			return builder.toString()
		}

		return path
	}

	/**
	 * Delete the given d directory and all its content
	 */
	static def void recursiveDelete(File d) {
		for (e : d.listFiles) {
			if (e.file) {
				e.delete
			} else if (e.directory) {
				e.recursiveDelete
			}
		}
		d.delete
	}

}
