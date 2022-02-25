package com.hadii.clarpse;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a common directory.
 */
public class CommonDir {

    private String[] dirs;

    public CommonDir(String... dirs) {
        this.dirs = dirs;
        for (int i = 0; i < dirs.length; i++) {
            if (!dirs[i].contains("/")) {
                throw new IllegalArgumentException("Directory path: " + dirs[i] + " is invalid!");
            }
        }
    }

    public String value() throws Exception {
        if (dirs.length < 1) {
            throw new Exception("No dirs were supplied!");
        } else {
            String commonDir = dirs[0];
            for (int i = 0; i < dirs.length; i++) {
                String[] dirAParts = commonDir.split("/");
                String[] dirBParts = dirs[i].split("/");
                List<String> matchingParts = new ArrayList<String>();
                int j = 0;
                while (j < dirAParts.length && j < dirBParts.length && dirAParts[j].equals(dirBParts[j])) {
                    matchingParts.add(dirAParts[j]);
                    j++;
                }
                List<String> filteredMatchingParts =
                    matchingParts.stream().filter(s -> !s.contains(".")).collect(Collectors.toList());
                commonDir = StringUtils.join(filteredMatchingParts, "/");
            }
            if (commonDir.isEmpty()) {
                commonDir = "/";
            }
            return commonDir;
        }
    }
}
