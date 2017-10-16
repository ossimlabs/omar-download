package omar.download

import grails.validation.Validateable
import groovy.transform.ToString

/**
 * Created by nroberts on 7/8/16.
 */
@ToString(includeNames = true)
class FileDownloadCommand implements Validateable
{
    String type = "Download"
    def archiveOptions
    def fileGroups
    def zipFileName

    static constraints = {

        type(nullable:true)
        archiveOptions(nullable:true)
        fileGroups()
        zipFileName(nullable:true)
    }

    Boolean isZip()
    {
        Boolean result = true;

        if(archiveOptions)
        {
            String type = archiveOptions["type"]?.toString().toLowerCase()

            if(type!="zip")
            {
                result = false;
            }
        }

        result
    }

}
