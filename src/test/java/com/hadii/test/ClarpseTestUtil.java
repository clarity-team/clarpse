package com.hadii.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.compiler.ProjectFiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.hadii.clarpse.compiler.ClarpseProject;
import com.hadii.clarpse.compiler.Lang;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;

public class ClarpseTestUtil {

    public static String unzipArchive(File archivedFile) throws IOException {
        Path tmpPath = Paths.get(FileUtils.getTempDirectory().getAbsolutePath(),
                           UUID.randomUUID().toString());
        String tmpdir = Files.createDirectories(tmpPath).toFile().getAbsolutePath();
        tmpPath.toFile().deleteOnExit();
        try (ZipFile zipFile = new ZipFile(archivedFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(tmpdir, entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryDestination)) {
                        IOUtils.copy(in, out);
                    }
                }
            }
        }
        return tmpdir;
    }

    public static OOPSourceCodeModel sourceCodeModel(String testResourceZip, Lang language) throws Exception {
        final ProjectFiles projectFiles = new ProjectFiles(
            language, ClarpseTestUtil.class.getResourceAsStream(testResourceZip));
        return new ClarpseProject(projectFiles.files(), projectFiles.lang()).result().model();
    }
}