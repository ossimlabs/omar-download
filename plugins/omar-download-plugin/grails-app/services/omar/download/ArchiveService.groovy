package omar.download

import grails.transaction.Transactional
import omar.core.HttpStatus
import grails.converters.JSON
import java.net.URL
import groovy.json.JsonSlurper

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

        def slurper = new JsonSlurper()
        ids?.each{
            URL url = new URL("${getRasterFilesUrl}id=${it}".toString())
            def obj = slurper.parseText(url.text)
            if(obj)
            {
                HashMap record = [files:[]]
                obj.results.each{
                    record.files << it
                }
                result << record
            }
        }
        println result
        result
    }
    def download(def response, FileDownloadCommand cmd)
    {
        println cmd
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
                                HashMap listOfFilesAsMaps = cmd.fileGroups
                                ZipFiles zipFiles = new ZipFiles()
                                zipFiles.zipSingle(listOfFilesAsMaps, response.outputStream)
                            }
                            else
                            {
                                def listOfFilesAsMaps = cmd.fileGroups

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
        }

        response.outputStream.close()

        result
    }
}
