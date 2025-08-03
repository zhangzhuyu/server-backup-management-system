package com.ly.cloud.backup.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("envelope")
public class OpenvasGetTasksDto extends OpenvasBaseDto{
    @XStreamAlias("get_tasks")
    public GetTasks get_tasks;
    @XStreamAlias("get_tasks")
    public class GetTasks{
        @XStreamAlias("get_tasks_response")
        public GetTasksResponse getTasksResponse;

        @Override
        public String toString() {
            return "GetTasks{" +
                    "getTasksResponse=" + getTasksResponse +
                    '}';
        }
    }
    @XStreamAlias("get_tasks_response")
    public class GetTasksResponse{
        public int apply_overrides;
        @XStreamImplicit
        public List<Task> task;

        @Override
        public String toString() {
            return "GetTasksResponse{" +
                    "apply_overrides=" + apply_overrides +
                    ", task=" + task +
                    '}';
        }
    }
    @XStreamAlias("get_tasks_response")
    public class Task{
        public String name;
        @XStreamAsAttribute()
        public String id;
        public String comment;
        public String creation_time;
        public String modification_time;
        public String writable;
        public String in_use;
        public String alterable;
        public String schedule_periods;
        public String status;
        public String report_count;
        public String severity;
        public String progress;
        public String trend;
        public LastReport last_report;
        public Owner owner;

        @Override
        public String toString() {
            return "Task{" +
                    "name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", comment='" + comment + '\'' +
                    ", creation_time='" + creation_time + '\'' +
                    ", modification_time='" + modification_time + '\'' +
                    ", writable='" + writable + '\'' +
                    ", in_use='" + in_use + '\'' +
                    ", alterable='" + alterable + '\'' +
                    ", schedule_periods='" + schedule_periods + '\'' +
                    ", status='" + status + '\'' +
                    ", report_count='" + report_count + '\'' +
                    ", severity='" + severity + '\'' +
                    ", progress='" + progress + '\'' +
                    ", trend='" + trend + '\'' +
                    ", last_report=" + last_report +
                    ", owner=" + owner +
                    '}';
        }
    }
    @XStreamAlias("owner")
    public class Owner{
        public String name;

        @Override
        public String toString() {
            return "Owner{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
    public class LastReport{
        public Report report;

        @Override
        public String toString() {
            return "LastReport{" +
                    "report=" + report +
                    '}';
        }
    }
    public class Report{
        public String timestamp;
        public String scan_start;
        public String scan_end;
        @XStreamAsAttribute()
        public String id;

        @Override
        public String toString() {
            return "Report{" +
                    "timestamp='" + timestamp + '\'' +
                    ", scan_start='" + scan_start + '\'' +
                    ", scan_end='" + scan_end + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OpenvasGetTasksDto{" +
                "version='" + version + '\'' +
                ", vendor_version='" + vendor_version + '\'' +
                ", token='" + token + '\'' +
                ", time='" + time + '\'' +
                ", timezone='" + timezone + '\'' +
                ", login='" + login + '\'' +
                ", session='" + session + '\'' +
                ", role='" + role + '\'' +
                ", i18n='" + i18n + '\'' +
                ", client_address='" + client_address + '\'' +
                ", backend_operation='" + backend_operation + '\'' +
                ", get_tasks=" + get_tasks +
                '}';
    }
}
