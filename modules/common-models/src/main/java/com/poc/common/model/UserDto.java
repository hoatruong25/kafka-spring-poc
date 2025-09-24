package com.poc.common.model;

/**
 * Common UserDto model shared between Producer and Consumer applications
 * This ensures consistency across different microservices
 */
public class UserDto {
    
    private String name;
    private String dept;
    private Long salary;

    // Default constructor for JSON deserialization
    public UserDto() {}

    public UserDto(String name, String dept, Long salary) {
        this.name = name;
        this.dept = dept;
        this.salary = salary;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
    
    @Override
    public String toString() {
        return String.format("UserDto{name='%s', dept='%s', salary=%d}", name, dept, salary);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserDto userDto = (UserDto) obj;
        return (name != null ? name.equals(userDto.name) : userDto.name == null) &&
               (dept != null ? dept.equals(userDto.dept) : userDto.dept == null) &&
               (salary != null ? salary.equals(userDto.salary) : userDto.salary == null);
    }
    
    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (dept != null ? dept.hashCode() : 0);
        result = 31 * result + (salary != null ? salary.hashCode() : 0);
        return result;
    }
}