package omar.download

// import com.wordnik.swagger.annotations.Api
// import com.wordnik.swagger.annotations.ApiImplicitParam
// import com.wordnik.swagger.annotations.ApiImplicitParams
// import com.wordnik.swagger.annotations.ApiOperation

import io.swagger.annotations.*

import groovy.json.JsonSlurper
import omar.core.BindUtil

import javax.xml.ws.Response
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Api(
        value = "archive",
        description = "API operations for Download IO",
        produces = 'application/json',
        consumes = 'application/json'
)

class ArchiveController {

    static allowedMethods = [
                              download:["POST"]
                            ]
    def archiveService

    @ApiOperation(
            value = "Download files",
            consumes= 'application/json',
            produces='application/json',
            httpMethod="POST",
            notes="""
- Currently, only the download type and the zip archive options type are supported.
- The zip file name is optional and will use a preset file name if one is not entered. 
  When entering a zip file name, be sure to enter a ".zip" extension (ex. myimages.zip).
- Enter a file groups root directory if you wish to keep the directory structure when you
  unzip your zip file.  File group files is a list of paths to files or folders that contain the inage information.
  here can be single or multiple file groups.

Example of a accepted single and multiple file groups
```
        "fileGroups":
            [
                {
                    "rootDirectory":"",
                    "files":["","",...]
                }
            ]

            OR

        "fileGroups":
            [
                {
                    "rootDirectory":"",
                    "files":["","",...]
                }
                {
                    "rootDirectory":"",
                    "files":["","",...]
                }
            ]
```
Example:
```
{
    "type":"Download",
    "zipFileName": "",
    "archiveOptions":
    {
        "type": "zip"
    },
    "fileGroups":
    [
        {
            "rootDirectory":"",
            "files":["",""]
        }
    ]
}
```
""")

    @ApiImplicitParams([
        @ApiImplicitParam(
                name = 'body',
                value = "General Message for querying recommendations",
                defaultValue = """
""",
                paramType = 'body',
                dataType = 'string'
            )
    ])

    def download() {
        def jsonData = request.JSON?request.JSON as HashMap:null
        def fileInfoParams = params.fileInfo?params.fileInfo:null
        def requestParams = params - params.subMap(['fileInfo','controller', 'format', 'action'])
        def cmd = new FileDownloadCommand()
        if (fileInfoParams)
        {
            def slurper = new groovy.json.JsonSlurper()
            jsonData = slurper.parseText(fileInfoParams)
        }

        if(jsonData) requestParams << jsonData
        BindUtil.fixParamNames( FileDownloadCommand, requestParams )
        bindData( cmd, requestParams )

        archiveService.download(response, cmd)

        null
    }
}
