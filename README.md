# omar-download
[![Build Status](https://jenkins.radiantbluecloud.com/buildStatus/icon?job=omar-download-dev)]()
The Download Web Service takes multiple images or image groups specified in a JSON and return a zip archive.

Git clone or git pull the omar-common repo.
```
  git clone https://github.com/ossimlabs/omar-common.git
```

### Required environment variable
- OMAR_COMMON_PROPERTIES

### Optional environment variables
#### required by Jenkins or a local Artifactory or a local Openshift

- OPENSHIFT_USERNAME
- OPENSHIFT_PASSWORD
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

Example:
```
  export OMAR_COMMON_PROPERTIES=~/omar-common/omar-common-properties.gradle
```

Git clone or git pull the omar-core repo.

Install the following plugins omar-core-plugin before installing omar-download-plugin
```
 cd omar-core/plugins/omar-core-plugin
 ./gradlew clean install
```

### Install omar-download-plugin before you build/install omar-download-app
```
 cd omar-download/plugins/omar-download-plugin
 ./gradlew clean install
```
