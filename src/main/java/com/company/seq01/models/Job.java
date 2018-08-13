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
@Table(name = "charities")
@Data
public class Job extends AbstractDatedEntity {
    @Column(name = "name")
    protected String name;

    @Column(name = "ein")
    protected String ein;

    @Column(name = "website")
    protected String website;

    /**
     * Constructor
     */
    public Job(Integer id, String name, String ein, String website) {
        this.id = id;
        this.name = name;
        this.ein = ein;
        this.website = website;
    }
}
