package com.notes.portal.model;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String department;
    private String semester;

    public Long   getId()                      { return id; }
    public String getName()                    { return name; }
    public void   setName(String v)            { this.name = v; }
    public String getEmail()                   { return email; }
    public void   setEmail(String v)           { this.email = v; }
    public String getPassword()                { return password; }
    public void   setPassword(String v)        { this.password = v; }
    public String getDepartment()              { return department; }
    public void   setDepartment(String v)      { this.department = v; }
    public String getSemester()                { return semester; }
    public void   setSemester(String v)        { this.semester = v; }
}
