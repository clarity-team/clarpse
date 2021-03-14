package com.hadii.clarpse;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a common directory.
 */
public class CommonDir {

    private String[] dirs;

    public CommonDir(String... dirs) {
        this.dirs = dirs;
    }

    public String value() throws Exception {
        if (dirs.length < 1) {
            throw new Exception("No dirs were supplied!");
        } else if (dirs.length < 2) {
            return dirs[0];
        } else {
            String commonDir = dirs[0];

            for (int i = 1; i < dirs.length; i++) {
                if (!commonDir.contains("/") || !dirs[i].contains("/")) {
                    return "";
                } else {
                    String[] dirAParts = commonDir.split("/");
                    String[] dirBParts = dirs[i].split("/");
                    List<String> matchingParts = new ArrayList<String>();
                    int j = 0;
                    while (j < dirAParts.length && j < dirBParts.length && dirAParts[j].equals(dirBParts[j])) {
                        matchingParts.add(dirAParts[j]);
                        j++;
                    }
                    commonDir = StringUtils.join(matchingParts, "/");
                }
            }
            if (!commonDir.endsWith("/")) {
                commonDir += "/";
            }
            return commonDir;
        }
    }
}
