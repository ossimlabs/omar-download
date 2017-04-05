# omar-download
[![Build Status](https://jenkins.radiantbluecloud.com/buildStatus/icon?job=omar-download-dev)]()

#### The Download Web Service takes multiple images or image groups specified in a JSON and return a zip archive.

### Required environment variable
- OMAR_COMMON_PROPERTIES

### Optional environment variables
Only required for Jenkins pipelines or if you are running Artifactory and/or Openshift locally

- OPENSHIFT_USERNAME
- OPENSHIFT_PASSWORD
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

## How to Build/Install omar-download-app locally

1. Git clone the following repos or git pull the latest versions if you already have them.
```
  git clone https://github.com/ossimlabs/omar-common.git
  git clone https://github.com/ossimlabs/omar-core.git
  git clone https://github.com/ossimlabs/omar-download.git
```

2. Set OMAR_COMMON_PROPERTIES environment variable to the omar-common-properties.gradle (it is part of the omar-common repo).

3. Install omar-core-plugin (it is part of the omar-core repo).
```
 cd omar-core/plugins/omar-core-plugin
 ./gradlew clean install
```

4. Install omar-download-plugin
```
 cd omar-download/plugins/omar-download-plugin
 ./gradlew clean install
```

5. Build/Install omar-download-app
#### Build:
```
 cd omar-download/apps/omar-download-app
 ./gradlew clean build
 ```
#### Install:
```
 cd omar-download/apps/omar-download-app
 ./gradlew clean install
```
