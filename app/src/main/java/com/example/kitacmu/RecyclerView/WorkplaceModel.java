// com/example/kitacmu/RecyclerView/WorkplaceModel.java
package com.example.kitacmu.RecyclerView;

public class WorkplaceModel {
    private final String id;
    private final String title;
    private final String owner;
    private final String salary;
    private final String time;
    private final String location;
    private final String duration;

    public WorkplaceModel(
            String id,
            String title,
            String owner,
            String salary,
            String time,
            String location,
            String duration
    ) {
        this.id       = id;
        this.title    = title;
        this.owner    = owner;
        this.salary   = salary;
        this.time     = time;
        this.location = location;
        this.duration = duration;
    }

    public String getId()       { return id;       }
    public String getTitle()    { return title;    }
    public String getOwner()    { return owner;    }
    public String getSalary()   { return salary;   }
    public String getTime()     { return time;     }
    public String getLocation() { return location; }
    public String getDuration() { return duration; }
}
