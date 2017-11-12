package omar.download

import groovy.util.logging.Slf4j

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import groovy.json.JsonBuilder

/**
 * Created by nroberts on 7/7/16.
 */
@Slf4j
class ZipFiles {
    static final int BUFFER_SIZE = 2048

    /***********************************************************
     *
     * Function: buildZipFileList
     * Purpose:  Takes a list of files/folders and creates zip
     *           entries for them.  It then returns an ArrayList
     *           of HashMaps containing the full path and the zip
     *           entry for the files
     *
     * @param    varFileList (ArrayList)
     * @return   fileInfo (ArrayList)
     *
     ***********************************************************/
    ArrayList buildZipFileList(String varRootFilePath, ArrayList varFileList)
    {
        File fileToZip
        File filePathName
        File relPath
        String dFileFullPath
        String zipEntryPath
        String zipFileID = UUID.randomUUID().toString();
        ArrayList fileInfo = new ArrayList<HashMap>()

        varFileList.each { ftzPath ->
            try
            {
                if (ftzPath!="")
                {
                    fileToZip = new File(ftzPath)

                    if (varRootFilePath)
                    {
                        filePathName = new File(varRootFilePath)
                    }
                    else
                    {
                        filePathName = new File(fileToZip?.parent)
                    }

                    if (fileToZip.isDirectory())
                    {

                        fileToZip.eachFileRecurse { dir ->

                            if (!dir.isDirectory())
                            {
                                relPath = filePathName.toPath().relativize(dir.toPath()).toFile()
                                dFileFullPath = "${dir.toString()}"
                                zipEntryPath = "${zipFileID}/${relPath.toString()}"

                                fileInfo.add(new HashMap([fileFullPath: "${dFileFullPath}", zipEntryPath: "${zipEntryPath}"]))
                            }
                        }
                    }
                    else
                    {
                        dFileFullPath = "${fileToZip.toString()}"
                        relPath = filePathName.toPath().relativize(fileToZip.toPath()).toFile()
                        zipEntryPath = "${zipFileID}/${relPath.toString()}"

                        fileInfo.add(new HashMap([fileFullPath: "${dFileFullPath}", zipEntryPath: "${zipEntryPath}"]))
                    }
                }
                else
                {
                    log.error("The File Path Is Invalid")
                }
            }
            catch (e)
            {
                log.error(e.message.toString())
            }
        }

        return fileInfo
    }

    /****************************************************************
     * Function: zipSingle
     * Purpose:  Takes a list of files/folders, build their zip
     *           entries, then zip them up
     *
     * @param    varListOfFileInfo (HashMap)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zipSingle(HashMap varListOfFileInfo, OutputStream varOutputStream)
    {
        String rootDir = varListOfFileInfo["rootDirectory"]
        ArrayList fileList = varListOfFileInfo["files"]

        zip(buildZipFileList(rootDir, fileList), varOutputStream)
    }

    /****************************************************************
     * Function: zipMulti
     * Purpose:  Takes multiple list of files/folders, build their
     *           zip entries, then zip them up
     *
     * @param    varListOfFileInfo (ArrayList)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zipMulti(ArrayList varListOfFileInfo, OutputStream varOutputStream)
    {
        ArrayList fileInfo = new ArrayList()

        varListOfFileInfo.each { ftzPath ->

            String rootDir = ftzPath["rootDirectory"]
            ArrayList fileList = ftzPath["files"]

            fileInfo.addAll(buildZipFileList(rootDir, fileList))
        }
        zip(fileInfo, varOutputStream)
    }

    /****************************************************************
     * Function: zip
     * Purpose:  Takes a list of files and their zip entries, then
     *           zip them up
     *
     * @param    varFileInfo (ArrayList)
     * @param    varOutputStream (OutputStream)
     *
     ****************************************************************/
    void zip(ArrayList varFileInfo, OutputStream varOutputStream)
    {
        def requestType = "GET"
        def requestMethod = "Download"

        ZipOutputStream zos = new ZipOutputStream(varOutputStream)

        byte[] readBuffer = new byte[BUFFER_SIZE]
        int bytesIn = 0
        try
        {
            varFileInfo.each { zipFilePath ->
                Date startTime = new Date()

                FileInputStream fis = new FileInputStream( zipFilePath["fileFullPath"] )
                ZipEntry anEntry = new ZipEntry( "${zipFilePath["zipEntryPath"]}" )

                zos.putNextEntry( anEntry )
                while ( ( bytesIn = fis.read( readBuffer ) ) > 0 )
                {
                    zos.write( readBuffer, 0, bytesIn )
                }
                zos.closeEntry()
                fis.close()

                Date endTime = new Date()
                def responseTime = Math.abs(startTime.getTime() - endTime.getTime())
                def requestInfoLog = new JsonBuilder(timestamp: startTime.format("YYYY-MM-DD HH:mm:ss.Ms"),
                        requestType: requestType, requestMethod: requestMethod, endTime: endTime.format("YYYY-MM-DD HH:mm:ss.Ms"),
                        responseTime: responseTime, filename: zipFilePath["fileFullPath"])

                log.info requestInfoLog.toString()
            }
        }
        catch (e)
        {
            log.error(e.toString())
        }
        finally
        {
            zos.close()
            println "closed zos"
        }
    }
}
