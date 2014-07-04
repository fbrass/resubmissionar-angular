package de.spqrinfo.resubmission.web;


import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.UploadFile;
import de.spqrinfo.resubmission.service.ResubmissionService;
import de.spqrinfo.resubmission.service.UploadFileService;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet(urlPatterns = "/file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
                 maxFileSize = 1024 * 1024 * 50,          // 50 MB
                 maxRequestSize = 1024 * 1024 * 100)      // 100 MB
public class FileServlet extends HttpServlet {

    private static final String KIND_CUSTOMER_LOGO = "customerLogo";

    private static final String KIND_CUSTOMER_TEMP_LOGO = "customerLogoTemp";

    @Inject
    UploadFileService uploadFileService;

    @Inject
    private ResubmissionService resubmissionService;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final List<UploadFile> uploads = new ArrayList<>();

        for (final Part part : req.getParts()) {
            final String submittedFileName = part.getSubmittedFileName();
            if (submittedFileName == null) {
                continue;
            }

            final String contentType = part.getContentType();
            final long size = part.getSize();

            InputStream inputStream = null;
            try {
                inputStream = part.getInputStream();
                final byte[] data = new byte[(int) part.getSize()];
                final int nb = inputStream.read(data);
                inputStream.close();

                if (nb != part.getSize()) {
                    throw new RuntimeException("I/O error: invariant failed - read not advertised size");
                }

                final UploadFile uploadFile = new UploadFile();
                uploadFile.setFilename(submittedFileName);
                uploadFile.setContentType(contentType);
                uploadFile.setSize(size);
                uploadFile.setData(data);

                final UploadFile uploadFilePersisted = this.uploadFileService.createTemporaryUpload(uploadFile);
                uploads.add(uploadFilePersisted);

            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException ignored) {
                    }
                }
            }
        }

        // Create json response of uploads
        final JsonArrayBuilder builder = Json.createArrayBuilder();
        for (final UploadFile upload : uploads) {
            final JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("logoId", upload.getUploadId());
            if (upload.isTemporary()) {
                objBuilder.add("imageUrl", "file?kind=" + KIND_CUSTOMER_TEMP_LOGO + "&uploadId=" + upload.getUploadId());
            } else {
                throw new RuntimeException("Not yet");
            }
            builder.add(objBuilder);
        }
        resp.setContentType("application/json");

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
            writer.writeArray(builder.build());
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String kindParam = req.getParameter("kind");

        if (null == kindParam) {
            resp.sendError(SC_BAD_REQUEST);
            return;
        }

        switch (kindParam) {
        case KIND_CUSTOMER_LOGO:
            getCustomerLogo(req, resp);
            break;
        case KIND_CUSTOMER_TEMP_LOGO:
            getCustomerTemporaryLogo(req, resp);
            break;
        default:
            resp.sendError(SC_BAD_REQUEST);
            break;
        }
    }

    private void getCustomerTemporaryLogo(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String uploadIdParam = req.getParameter("uploadId");
        if (null == uploadIdParam) {
            resp.sendError(SC_BAD_REQUEST);
            return;
        }

        final Long uploadId = Long.valueOf(uploadIdParam);
        final UploadFile uploadFile = this.uploadFileService.findTemporary(uploadId);

        if (uploadFile == null) {
            resp.sendError(SC_NOT_FOUND);
        } else {
            send(resp, uploadFile);
        }
    }

    private void getCustomerLogo(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String customerParam = req.getParameter("customer");
        if (null == customerParam) {
            resp.sendError(SC_BAD_REQUEST);
            return;
        }

        final Long customerId = Long.valueOf(customerParam);
        final Customer customer = this.resubmissionService.getCustomer(customerId);

        if (customer == null) {
            resp.sendError(SC_BAD_REQUEST);
        } else {
            if (!customer.hasLogo()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                final UploadFile logo = customer.getLogo();
                send(resp, logo);
            }
        }
    }

    private static void send(final HttpServletResponse resp, final UploadFile file) throws IOException {
        resp.setContentLengthLong(file.getSize());
        resp.setContentType(file.getContentType());
        resp.getOutputStream().write(file.getData());
    }
}
