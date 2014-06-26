package de.spqrinfo.resubmission.web;

import de.spqrinfo.resubmission.persistence.Customer;
import de.spqrinfo.resubmission.service.ResubmissionFacade;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Named("createCustomer")
@RequestScoped
public class CreateCustomerBean {

    @Inject
    private ResubmissionFacade resubmissionFacade;

    private Customer customer = new Customer();

    private Part logoPart;

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    public Part getLogoPart() {
        return this.logoPart;
    }

    public void setLogoPart(final Part logoPart) {
        this.logoPart = logoPart;
    }

    @SuppressWarnings("UnusedParameters")
    public void validateLogoPart(final FacesContext facesContext, final UIComponent component, final Object value) {
        final List<FacesMessage> msgs = new ArrayList<>();
        if (null == value) {
            return;
        }
        final Part file = (Part) value;
        final int customerLogoMaxSize = Customer.LOGO_MAX_SIZE;
        if (file.getSize() > customerLogoMaxSize) {
            msgs.add(new FacesMessage("file too big")); // TODO i18n
        }
        if (!"image/png".equals(file.getContentType())) { // TODO externalize, not here, more types
            msgs.add(new FacesMessage("not a valid image file")); // TODO i18n
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

    private static class Logo {
        private final byte[] logo;
        private final String contentType;

        private Logo(final byte[] logo, final String contentType) {
            this.logo = logo;
            this.contentType = contentType;
        }

        public String getContentType() {
            return this.contentType;
        }

        public byte[] getLogo() {
            return this.logo;
        }
    }

    private static Logo getLogo(final Part part) {
        InputStream inputStream = null;
        if (part != null) {
            try {
                final String contentType = part.getContentType();
                inputStream = part.getInputStream();
                final ByteArrayOutputStream bout = new ByteArrayOutputStream();
                final byte[] buf = new byte[4096];
                int nb = inputStream.read(buf);
                while (nb > 0) {
                    bout.write(buf, 0, nb);
                    nb = inputStream.read(buf);
                }
                inputStream.close();
                final byte[] logo = bout.toByteArray();
                return new Logo(logo, contentType);
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException ignored) {
                    }
                }
            }
        }
        return null;
    }

    public String createCustomer() {
        final Logo logo = getLogo(this.logoPart);
        if (logo != null) {
            this.customer.setLogo(logo.getLogo());
            this.customer.setLogoContentType(logo.getContentType());
        }
        this.resubmissionFacade.createCustomer(this.customer);
        return "customerList";
    }
}
