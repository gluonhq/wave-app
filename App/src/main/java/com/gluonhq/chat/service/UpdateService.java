package com.gluonhq.chat.service;

import com.gluonhq.chat.model.GithubRelease;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import com.gluonhq.connect.provider.RestClient;

import java.util.Optional;

public class UpdateService {
    
    private String currentVersion = "1.0.0";
    private GluonObservableList<GithubRelease> githubReleases;

    boolean isNewVersionAvailable() {
        final RestClient restClient = RestClient.create()
                .method("GET")
                .host("https://api.github.com/repos/gluonhq/wave-app/releases");

        githubReleases = DataProvider.retrieveList(
                restClient.createListDataReader(GithubRelease.class));

        final Optional<GithubRelease> latestRelease = githubReleases.stream().
                max((o1, o2) -> compareVersions(o1.getTag_version(), o2.getTag_version()));
        return latestRelease.isPresent() &&
            compareVersions(latestRelease.get().getTag_version(), currentVersion) > 0;
    }
    
    void downloadNewVersion() {
        // TODO: Find platform specific
        githubReleases.stream().
    }
    
    private static int compareVersions(String version1, String version2) {
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
}
