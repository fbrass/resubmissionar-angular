package de.spqrinfo.resubmission.web;


import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.persistence.Upload;
import de.spqrinfo.resubmission.service.ResubmissionFacade;
import de.spqrinfo.resubmission.service.UploadFacade;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/image")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10,    // 10 MB
        maxFileSize = 1024 * 1024 * 50,          // 50 MB
        maxRequestSize = 1024 * 1024 * 100)      // 100 MB
public class ImageServlet extends HttpServlet {

    @Inject
    UploadFacade uploadFacade;

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final List<Long> uploadIds = new ArrayList<>();

        for (final Part part : req.getParts()) {
            final String submittedFileName = part.getSubmittedFileName();
            System.out.println(submittedFileName);
            if (submittedFileName == null) {
                continue;
            }

            final String contentType = part.getContentType();
            final long size = part.getSize();

            InputStream inputStream = null;
            try {
                inputStream = part.getInputStream();
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                final byte[] buf = new byte[4096];
                int nb = inputStream.read(buf);
                while (nb > 0) {
                    bout.write(buf, 0, nb);
                    nb = inputStream.read(buf);
                }
                inputStream.close();
                final byte[] data = bout.toByteArray();

                final Upload upload = new Upload();
                upload.setFilename(submittedFileName);
                upload.setContentType(contentType);
                upload.setSize(size);
                upload.setData(data);

                final Upload uploadPersisted = this.uploadFacade.createUpload(upload);
                uploadIds.add(uploadPersisted.getUploadId());

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

        // Create response of type json containing upload IDs
        resp.setContentType("application/json");
        final PrintWriter writer = resp.getWriter();
        writer.print('[');
        for (int i = 0; i < uploadIds.size(); i++) {
            if (i > 0) {
                writer.print(',');
            }
            writer.print(uploadIds.get(i));
        }
        writer.print(']');
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String customerParam = req.getParameter("customer");
        if (null == customerParam) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final Long customerId = Long.valueOf(customerParam);
        final Customer customer = this.resubmissionFacade.getCustomer(customerId);

        if (customer != null && customer.getLogo() != null) {
            final byte[] logo = customer.getLogo();
            resp.setContentLength(logo.length);
            resp.setContentType(customer.getLogoContentType());
            resp.getOutputStream().write(logo);
        }
    }
}
