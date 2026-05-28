package org.agty.elfiumexpress.storage.utils;

import org.agty.agtysql.interfaces.SqlRow;
import org.agty.elfiumexpress.storage.entity.UploadedFile;
import org.agty.elfiumexpress.storage.types.FileTypes;
import org.agty.utils.AgtyUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class UploadedFileUtils {
    /**
     * Converting the SqlRow into the UploadedFile.
     * @param sqlRow SqlRow object
     * @return UploadedFile object
     */
    public static UploadedFile rowToUploadedFile(SqlRow sqlRow) {
        UploadedFile uploadedFile = new UploadedFile();

        uploadedFile.setIdFile(sqlRow.getLong("id_file"));
        uploadedFile.setIdUser(sqlRow.getLong("id_user"));
        uploadedFile.setName(sqlRow.getString("name"));
        uploadedFile.setFile(sqlRow.getString("file"));
        uploadedFile.setContentType(sqlRow.getString("content_type"));
        uploadedFile.setSize(sqlRow.getLong("size"));
        uploadedFile.setExtension(sqlRow.getString("ext"));

        return uploadedFile;
    }

    /**
     * Converting the MultipartFile into the UploadedFile.
     * @param file MultipartFile object
     * @return UploadedFile object
     */
    public static UploadedFile multipartFileToUploadedFile(MultipartFile file) {
        String ext = AgtyUtils.getFileExtension(file.getOriginalFilename());

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setName(file.getOriginalFilename());
        uploadedFile.setFile(file.getOriginalFilename());
        uploadedFile.setContentType(file.getContentType());
        uploadedFile.setSize(file.getSize());
        uploadedFile.setExtension(!ext.isEmpty() ? ext : null);

        return uploadedFile;
    }

    /**
     * Get the content-type (mime) from the file
     * @param inputStream \Stream
     * @return Content type (Mime type)
     */
    private static String getContentType(InputStream inputStream) throws IOException {
        Tika tika = new Tika();
        return tika.detect(inputStream);
    }

    /**
     * Converting the MultipartFile into the UploadedFile.
     * Additional checking the content-type and replacing with the right content-type and the right extension.
     * @param file MultipartFile object
     * @param salt Salt for generate a new file name
     * @return UploadedFile object
     */
    public static UploadedFile multipartFileCheckAndConvertToUploadedFile(MultipartFile file, Object salt) {
        UploadedFile uploadedFile = UploadedFileUtils.multipartFileToUploadedFile(file);

        /*System.out.println("FileSystemStorageService.store(Original)");
        System.out.println(uploadedFile);*/

        try {
            String contentType = getContentType(file.getInputStream());

            if (AgtyUtils.stringIsNullOrEmpty(contentType)) {
                contentType = "application/octet-stream";
            }

            if (!uploadedFile.getContentType().equals(contentType)) {

                uploadedFile.setContentType(contentType);

                String detectExt = FileTypes.detectExtensionByContentType(contentType);

                if (
                    FileTypes.isExec(contentType)
                    || FileTypes.isVideo(contentType)
                    || FileTypes.isAudio(contentType)
                    || FileTypes.isImage(contentType)
                    || FileTypes.isPdf(contentType)
                ) {
                    String rightFileName =
                            AgtyUtils.getFileNameWithoutExtension(file.getOriginalFilename())
                                    + "."
                                    + detectExt;

                    uploadedFile.setExtension(detectExt);
                    uploadedFile.setName(rightFileName);
                }
            }

            uploadedFile.setFile( FileUtils.generateUniqueName(uploadedFile, salt) );

            /*System.out.println("FileSystemStorageService.store(Result)");
            System.out.println(uploadedFile);*/

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return uploadedFile;
    }

    public static UploadedFile multipartFileCheckAndConvertToUploadedFile(MultipartFile file) {
        return multipartFileCheckAndConvertToUploadedFile(file, null);
    }
}
