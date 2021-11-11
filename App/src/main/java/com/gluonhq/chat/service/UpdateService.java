package com.gluonhq.chat.service;

import com.gluonhq.chat.model.GithubRelease;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;
import javafx.application.Platform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateService {

    private static final String TEMP_DOWNLOAD_DIRECTORY = System.getProperty("java.io.tmpdir");
    static Path installerPath;

    static String currentVersion() {
        return "1.0.0";
    }

    static GluonObservableList<GithubRelease> queryReleases() {
        final RestClient restClient = RestClient.create()
                .method("GET")
                .host("https://api.github.com/repos/gluonhq/wave-app/releases");
        return DataProvider.retrieveList(restClient.createListDataReader(GithubRelease.class));
    }

    static void downloadNewVersion(GithubRelease githubRelease) {
        GithubRelease.Asset windowsAsset = githubRelease.getAssets().stream()
                .filter(asset -> asset.getName().endsWith(OSFileExtension()))
                .findFirst().get();
        String download_url = windowsAsset.getBrowser_download_url();
        Path tempFile = Paths.get(TEMP_DOWNLOAD_DIRECTORY + "/" + download_url.substring(download_url.lastIndexOf("/") + 1));
        try {
            if (Files.exists(tempFile) && Files.size(tempFile) == windowsAsset.getSize()) {
                System.out.println("File already found:"  + tempFile);
                installerPath = tempFile;
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        URI uri = URI.create(download_url);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        // use the client to send the asynchronous request
        InputStream is = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream()).thenApply(HttpResponse::body).join();
        try (FileOutputStream out = new FileOutputStream(tempFile.toString())) {
            is.transferTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("New Version downloaded at:"  + tempFile);
        installerPath = tempFile;
    }

    static void installNewVersion() {
        try {
            Runtime.getRuntime().exec("cmd /c start " + installerPath);
            Platform.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int compareVersions(String version1, String version2) {
        int comparisonResult = 0;

        String[] version1Splits = version1.split("\\.");
        String[] version2Splits = version2.split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++){
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult;
    }

    public static void deleteExistingFiles() {
        try {
            Files.list(Paths.get(TEMP_DOWNLOAD_DIRECTORY))
                 .filter(p -> p.toString().contains("WaveApp") && p.toString().endsWith(OSFileExtension()))
                 .forEach((p) -> {
                     try {
                         Files.deleteIfExists(p);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String OSFileExtension() {
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            return "msi";
        } else if (OS.contains("mac")) {
            return "pkg";
        }
        return "";
    }
}
