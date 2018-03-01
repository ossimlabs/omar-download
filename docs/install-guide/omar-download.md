# OMAR Download

## Purpose
The OMAR-download service enables downloading imagery files and zip archives.

## Installation in Openshift

**Assumption:** The omar-download-app docker image is pushed into the OpenShift server's internal docker registry and available to the project.

### Persistent Volumes

OMAR-download requires shared access to OSSIM imagery data. This data is assumed to be accessible from the local filesystem of the pod. Therefore, a volume mount must be mapped into the container. A PersistentVolumeClaim should be mounted to `/data`, but can be configured using the deploymentConfig.

Use the `BUCKET` environment variable to set the desired S3 bucket under the mounted imagery volume.

### Environment variables

|Variable|Value|
|------|------|
|SPRING_PROFILES_ACTIVE|Comma separated profile tags (*e.g. production, dev*)|
|SPRING_CLOUD_CONFIG_LABEL|The Git branch from which to pull config files (*e.g. master*)|
|BUCKETS|The S3 to mount for direct image access (*e.g. o2-test-data*)|

## Using the service

The download service provides an endpoint for downloading imagery which can be accessed on the [API Swagger page](https://omar-dev.ossim.io/omar-download/api)

Notes for `<api>/archive/download`:
>Currently, only the download type and the zip archive options type are supported.
>The zip file name is optional and will use a preset file name if one is not entered.
>When entering a zip file name, be sure to enter a “.zip” extension (ex. myimages.zip).
>Enter a file groups root directory if you wish to keep the directory structure when you
>unzip your zip file. File group files is a list of paths to files or folders that contain the image information.
>here can be single or multiple file groups.

_[More examples](https://omar-dev.ossim.io/omar-download/api)_