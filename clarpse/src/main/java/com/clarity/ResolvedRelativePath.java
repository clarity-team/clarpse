package com.clarity;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Represents a resolved path.
 */
public class ResolvedRelativePath {

    private String unresolvedRelativePath;
    private String absPath;

    /**
     * @param absolutePath
     *            An absolute Path.
     * @param unresolvedRelativePath
     *            A relative Path to the absolutePath for which an absolute Path is
     *            required.
     * @throws Exception
     */
    public ResolvedRelativePath(String absolutePath, String unresolvedRelativePath) throws Exception {
        this.absPath = absolutePath;
        this.unresolvedRelativePath = unresolvedRelativePath;

        if (unresolvedRelativePath.startsWith("/")) {
            throw new Exception("The given unresolved path cannnot be an absolute path!");
        }
    }

    private String preprocessedPath(String path) throws Exception {
        // pre-processing...
        String processedPath = new TrimmedString(path, "/").value();
        if (processedPath.contains("/") && processedPath.substring(processedPath.lastIndexOf("/")).contains(".")) {
            // path contains a filename, get the containing dir instead.
            processedPath = processedPath.substring(0, processedPath.lastIndexOf("/"));
        } else if (!processedPath.contains("/") && processedPath.contains(".")) {
            processedPath = "/";
        }
        return processedPath;
    }

    /**
     * @see {@link ResolvedRelativePath#ResolvedRelativePath(String, String)}
     */
    public String value() throws Exception {

        String absolutePath = preprocessedPath(this.absPath);
        String relativePath = preprocessedPath(this.unresolvedRelativePath);

        // build absolute path representing the given relative path
        List<String> absoluteParts = new ArrayList<String>(Arrays.asList(absolutePath.split("/")));
        String[] relativeParts = relativePath.split("/");
        for (String relativePart : relativeParts) {
            if (relativePart.equals(".")) {
                // means current directory, do nothing
                continue;
            } else if (relativePart.equalsIgnoreCase("..")) {
                // mean move up a level
                absoluteParts.remove(absoluteParts.size() - 1);
            } else {
                absoluteParts.add(relativePart);
            }
        }
        return "/" + String.join("/", absoluteParts);
    }
}
