package com.example.kitacmu.RecyclerView;

import java.util.List;

public class ApplicationModel {
    private String      documentId;
    private String      applicantUid;
    private String      applicantName;
    private long        dateAppliedMs;
    private String      contact;
    private String      jobTitle;
    private String      location;
    private String      status;
    private List<String> skills;

    public ApplicationModel() { }

    public ApplicationModel(
            String documentId,
            String applicantUid,
            String applicantName,
            long   dateAppliedMs,
            String contact,
            String jobTitle,
            String location,
            String status,
            List<String> skills
    ) {
        this.documentId     = documentId;
        this.applicantUid   = applicantUid;
        this.applicantName  = applicantName;
        this.dateAppliedMs  = dateAppliedMs;
        this.contact        = contact;
        this.jobTitle       = jobTitle;
        this.location       = location;
        this.status         = status;
        this.skills         = skills;
    }

    // --- getters ---
    public String      getDocumentId()    { return documentId;    }
    public String      getApplicantUid()  { return applicantUid;  }
    public String      getApplicantName() { return applicantName; }
    public long        getDateAppliedMs() { return dateAppliedMs; }
    public String      getContact()       { return contact;       }
    public String      getJobTitle()      { return jobTitle;      }
    public String      getLocation()      { return location;      }
    public String      getStatus()        { return status;        }
    public List<String> getSkills()       { return skills;       }

    // --- setters (for Firestore + updates) ---
    public void setDocumentId(String documentId)        { this.documentId    = documentId;    }
    public void setApplicantUid(String applicantUid)    { this.applicantUid  = applicantUid;  }
    public void setApplicantName(String applicantName)  { this.applicantName = applicantName; }
    public void setDateAppliedMs(long dateAppliedMs)    { this.dateAppliedMs = dateAppliedMs; }
    public void setContact(String contact)              { this.contact       = contact;       }
    public void setJobTitle(String jobTitle)            { this.jobTitle      = jobTitle;      }
    public void setLocation(String location)            { this.location      = location;      }
    public void setStatus(String status)                { this.status        = status;        }
    public void setSkills(List<String> skills)          { this.skills        = skills;        }
}
