package com.onevour.core.applications.servlet;

import com.onevour.core.applications.commons.ValueOf;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.*;

@Slf4j
public class CachedBodyServletRequest extends HttpServletRequestWrapper {

    private final MultipartResolver multipartResolver = new StandardServletMultipartResolver();

    private MultipartHttpServletRequest multipartRequest;

    @Getter
    private boolean multipart;

    @Getter
    private boolean json;

    @Getter
    private Map<String, Object> formData = new HashMap<>();

    @Getter
    private Map<String, List<String>> formDataFile = new HashMap<>();

    private byte[] cachedBody;


    public CachedBodyServletRequest(HttpServletRequest request) throws IOException {
        super(request);
        extractContentType(request);
        if (isMultipart()) {
            multipartRequest = multipartResolver.resolveMultipart(request);
            log.info("is multipart");
            buildMultipartToMap(multipartRequest);
        }
        InputStream requestInputStream = request.getInputStream();
        this.cachedBody = StreamUtils.copyToByteArray(requestInputStream);
    }

    private void buildMultipartToMap(MultipartHttpServletRequest request) {

        // Process form fields
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            formData.put(paramName, request.getParameter(paramName));
        }

        // Process files
        Iterator<String> fileNames = request.getFileNames();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            List<MultipartFile> files = request.getFiles(fileName);
            if (ValueOf.isNull(files)) continue;
            if (files.isEmpty()) continue;
            // Convert file content to String (or save to disk)

            List<String> fileContents = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String base64String = Base64.getEncoder().encodeToString(file.getBytes());
                    fileContents.add(base64String);
                } catch (IOException e) {
                    log.warn("cannot convert multipart to base64");
                }
            }
            formData.put(fileName, files);
            formDataFile.put(fileName, fileContents);
        }
        log.info("map size {}", formData.size());
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new CachedBodyStream(this.cachedBody);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.cachedBody);
        return new BufferedReader(new InputStreamReader(byteArrayInputStream));
    }

    public void extractContentType(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (ValueOf.isNull(contentType)) {
            return;
        }
        if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            multipart = true;
        }
        if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            json = true;
        }
    }

    public void cleanUp() {
        RequestContextHolder.resetRequestAttributes();
        if (ValueOf.isNull(multipartRequest)) {
            return;
        }
        multipartResolver.cleanupMultipart(multipartRequest);
    }
}
