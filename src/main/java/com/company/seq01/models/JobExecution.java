package com.company.seq01.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Jeff Risberg
 * @since 10/26/17
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "Donors")
@Data
public class JobExecution extends AbstractEntity {

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "last_name")
    protected String lastName;

    /**
     * Constructor
     */
    public JobExecution(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
