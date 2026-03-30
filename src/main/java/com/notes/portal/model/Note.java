package com.notes.portal.model;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "notes")
public class Note {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String subject;
    private String department;
    private String semester;
    private String unit;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String authorName;
    private String authorEmail;
    private int    downloads = 0;
    private LocalDate uploadDate = LocalDate.now();

    public Long      getId()                   { return id; }
    public String    getTitle()                { return title; }
    public void      setTitle(String v)        { this.title = v; }
    public String    getSubject()              { return subject; }
    public void      setSubject(String v)      { this.subject = v; }
    public String    getDepartment()           { return department; }
    public void      setDepartment(String v)   { this.department = v; }
    public String    getSemester()             { return semester; }
    public void      setSemester(String v)     { this.semester = v; }
    public String    getUnit()                 { return unit; }
    public void      setUnit(String v)         { this.unit = v; }
    public String    getFileName()             { return fileName; }
    public void      setFileName(String v)     { this.fileName = v; }
    public String    getFilePath()             { return filePath; }
    public void      setFilePath(String v)     { this.filePath = v; }
    public String    getFileSize()             { return fileSize; }
    public void      setFileSize(String v)     { this.fileSize = v; }
    public String    getAuthorName()           { return authorName; }
    public void      setAuthorName(String v)   { this.authorName = v; }
    public String    getAuthorEmail()          { return authorEmail; }
    public void      setAuthorEmail(String v)  { this.authorEmail = v; }
    public int       getDownloads()            { return downloads; }
    public void      setDownloads(int v)       { this.downloads = v; }
    public LocalDate getUploadDate()           { return uploadDate; }
    public void      setUploadDate(LocalDate v){ this.uploadDate = v; }
}
