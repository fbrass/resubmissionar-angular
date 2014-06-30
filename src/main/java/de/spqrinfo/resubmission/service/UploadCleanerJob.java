package de.spqrinfo.resubmission.service;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;

@Singleton
@Stateless
class UploadCleanerJob {

    private static final long ONE_MINUTE_IN_MILLIS = 60000; // ms

    @Inject
    private UploadFacade uploadFacade;

    @Schedule(minute = "*/5", hour = "*")
    public void cleanUploads() {
        System.out.println("cleanUploads");
        final Date minutesAgo = new Date(new Date().getTime() - 5 * ONE_MINUTE_IN_MILLIS);
        this.uploadFacade.cleanUploadsAgo(minutesAgo);
    }
}
