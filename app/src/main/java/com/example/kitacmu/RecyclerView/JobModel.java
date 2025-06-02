// com/example/kitacmu/RecyclerView/JobModel.java
package com.example.kitacmu.RecyclerView;

public class JobModel {
    private String jobUid;
    private String title;
    private String ownerName;
    private String ownerUid;
    private String salary;
    private String time;
    private String location;
    private String duration;

    /** now eight‐arg constructor */
    public JobModel(String jobUid,
                    String title,
                    String ownerName,
                    String ownerUid,
                    String salary,
                    String time,
                    String location,
                    String duration) {
        this.jobUid     = jobUid;
        this.title      = title;
        this.ownerName  = ownerName;
        this.ownerUid   = ownerUid;
        this.salary     = salary;
        this.time       = time;
        this.location   = location;
        this.duration   = duration;
    }

    // getters
    public String getJobUid()    { return jobUid;    }
    public String getTitle()     { return title;     }
    public String getOwnerName() { return ownerName; }
    public String getOwnerUid()  { return ownerUid;  }
    public String getSalary()    { return salary;    }
    public String getTime()      { return time;      }
    public String getLocation()  { return location;  }
    public String getDuration()  { return duration;  }
}
