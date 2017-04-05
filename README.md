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

1. Git clone the omar-common repo or git pull the latest version if you already have it.
```
  git clone https://github.com/ossimlabs/omar-common.git
```

2. Set OMAR_COMMON_PROPERTIES environment variable to the omar-common-properties.gradle file you cloned in step one.

3. Git clone the omar-core repo or git pull the latest version if you already have it.

4. Install omar-core-plugin (it is part of the omar-core repo)
```
 cd omar-core/plugins/omar-core-plugin
 ./gradlew clean install
```

5. Install omar-download-plugin
```
 cd omar-download/plugins/omar-download-plugin
 ./gradlew clean install
```

6. Build/Install omar-download-app
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
