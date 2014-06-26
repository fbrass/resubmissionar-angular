package de.spqrinfo.resubmission.web;


import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/image")
public class ImageServlet extends HttpServlet {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String customerParam = req.getParameter("customer");
        if (null == customerParam) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final Long customerId = Long.valueOf(customerParam);
        final Customer customer  = this.resubmissionFacade.getCustomer(customerId);

        if (customer != null && customer.getLogo() != null) {
            final byte[] logo = customer.getLogo();
            resp.setContentLength(logo.length);
            resp.setContentType(customer.getLogoContentType());
            resp.getOutputStream().write(logo);
        }
    }
}
