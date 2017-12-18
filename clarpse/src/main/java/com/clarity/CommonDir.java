package com.clarity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a common directory that is shared amongst a group of directories.
 */
public class CommonDir {

    private String[] dirs;

    public CommonDir(String... dirs) {
        this.dirs = dirs;
    }

    public String string() throws Exception {
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
                    String dirAParts[] = commonDir.split("/");
                    String dirBParts[] = dirs[i].split("/");
                    List<String> matchingParts = new ArrayList<String>();
                    int j = 0;
                    while (dirAParts[j].equals(dirBParts[j])) {
                        matchingParts.add(dirAParts[j]);
                        j++;
                        if (j >= dirAParts.length || j >= dirBParts.length) {
                            break;
                        }
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
