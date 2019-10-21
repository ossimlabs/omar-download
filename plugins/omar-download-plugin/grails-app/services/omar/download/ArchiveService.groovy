package omar.download

import grails.gorm.transactions.Transactional
import omar.core.HttpStatus
import grails.converters.JSON
import java.net.URL
import groovy.json.JsonSlurper
import groovy.io.FileType

@Transactional
class ArchiveService {

    def grailsApplication
    def loadFileGroupsFromIds(def ids)
    {
        String getRasterFilesUrl = grailsApplication.config?.omar?.download?.getRasterFilesUrl;
        Boolean addQuestion = !getRasterFilesUrl?.endsWith("?")
        String question = ""
        def result = []
        if(addQuestion)
        {
            getRasterFilesUrl="${getRasterFilesUrl}?"
        }

        if(getRasterFilesUrl)
        {
            try{
                def slurper = new JsonSlurper()
                ids?.each{
                    URL url = new URL("${getRasterFilesUrl}id=${it}".toString())
                    def obj = slurper.parseText(url.text)
                    if(obj)
                    {
                        HashMap record = [files:[]]
                        obj.results.each{
                            if (it.type.toString().equals("main"))
                            {
                                record.files << it.name
                            }
                        }
                        result << record
                    }
                }

            }
            catch(e)
            {
                log.error(e.toString())
            }
        }
        else
        {
            log.error("getRasterFilesUrl is not specified for omar-download!")
            result = []
        }
        result
    }
    def download(def response, FileDownloadCommand cmd)
    {
        Integer maxFiles = grailsApplication.config?.omar?.download?.maxFiles?:10
        HashMap result = [
                status:HttpStatus.OK,
                message:"Downloading Files"
        ]

        String fileName = cmd.zipFileName
        def fileGroups = cmd.fileGroups
        if ((!fileName) || (fileName == ""))
        {
            fileName = "omar_images.zip"
        }

        if (cmd.validate())
        {
            try
            {
                if(!cmd.fileGroups)
                {
                    if(cmd.ids)
                    {
                        cmd.fileGroups = loadFileGroupsFromIds(cmd.ids)
                    }
                }
                if ((cmd.type?.toLowerCase() == "download") || (cmd.type == null))
                {
                    if(cmd.isZip())
                    {
                        if(cmd.fileGroups?.size()>=1)
                        {
                            response.setContentType("application/octet-stream")
                            response.setHeader("Content-Disposition", "attachment;filename=${fileName}");
                            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
                            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                            if(cmd.fileGroups.size()==1)
                            {
                                Map listOfFilesAsMaps = getAllFiles(cmd.fileGroups)
                                
                                ZipFiles zipFiles = new ZipFiles()
                                zipFiles.zipSingle(listOfFilesAsMaps, response.outputStream)
                            }
                            else
                            {
                                List<Map> listOfFilesAsMaps = []

                                cmd.fileGroups.each {
                                    listOfFilesAsMaps.add(getAllFiles(it))
                                }

                                ZipFiles zipFiles = new ZipFiles()
                                if(listOfFilesAsMaps.size() <= maxFiles)
                                {
                                    zipFiles.zipMulti(listOfFilesAsMaps, response.outputStream)
                                }
                                else
                                {
                                    result.status =  omar.core.HttpStatus.NOT_ACCEPTABLE
                                    result.message = "Too many images passed in.  Max number is ${maxFiles}"
                                }
                            }
                        }
                        else
                        {
                            result.status =  omar.core.HttpStatus.NOT_ACCEPTABLE
                            result.message = "No File Group Specified"
                        }
                    }
                    else
                    {
                        result.status =  omar.core.HttpStatus.UNSUPPORTED_MEDIA_TYPE
                        result.message = "Archive Option Type Not Recognized"
                    }
                }
                else
                {
                    result.status =  omar.core.HttpStatus.NOT_ACCEPTABLE
                    result.message = "Request Type Not Recognized"
                }
            }
            catch (e)
            {
                result.status = omar.core.HttpStatus.BAD_REQUEST
                result.message = e.message
            }
        }
        else {
            def messages = []
            String message = "Invalid parameters"
            result = [status : omar.core.HttpStatus.BAD_REQUEST,
                      message: message.toString()]
        }

        if(result.status != HttpStatus.OK)
        {
            response.setContentType("application/json")
            response.status = result.status
            String jsonData = "${result as JSON}"

            response.outputStream.write(jsonData.bytes)
            log.error(jsonData)
        }

        response.outputStream.close()

        result
    }

    Map getAllFiles(Map fileGroup)
    {
        List<File> files = []
        String imageFilePath = fileGroup["files"][0]
        String imageFileName = imageFilePath.substring(imageFilePath.lastIndexOf("/")+1, imageFilePath.lastIndexOf("."))
        File parentDir

        if (fileGroup["rootDirectory"]) parentDir = new File(fileGroup["rootDirectory"])
        else parentDir = new File(imageFilePath.substring(0,imageFilePath.lastIndexOf("/")))

        Map allFiles = [files:[]]

        parentDir.traverse(type: FileType.FILES, maxDepth: 0) { 
            String currentFile = it.getName()
            if (currentFile.startsWith(imageFileName)) files.add(it)
        }

        files.each {
            allFiles.files.add(it.getPath())
        }

        return allFiles
    }
}
