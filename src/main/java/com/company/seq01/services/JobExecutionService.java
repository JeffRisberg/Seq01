package com.company.seq01.services;

import com.company.common.FilterDesc;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeff Risberg
 * @since 10/26/17
 */
@Singleton
public class JobExecutionService {

    protected List<com.company.jersey04.models.JobExecution> donors = new ArrayList<com.company.jersey04.models.JobExecution>();

    public JobExecutionService() {
    }

    public com.company.jersey04.models.JobExecution getDonor(int id) {
        for (com.company.jersey04.models.JobExecution donor : donors) {
            if (donor.getId() == id)
                return donor;
        }
        return null;
    }

    public List<com.company.jersey04.models.JobExecution> getDonors(int limit, int offset, List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.JobExecution> result = applyFilter(filterDescs);

        if (offset > 0 && offset >= result.size())
            result = new ArrayList<com.company.jersey04.models.JobExecution>();
        else if (offset > 0)
            result = result.subList(offset, result.size());

        return result;
    }

    public long getDonorsCount(List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.JobExecution> result = applyFilter(filterDescs);

        return (long) result.size();
    }

    private List<com.company.jersey04.models.JobExecution> applyFilter(List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.JobExecution> result = new ArrayList<com.company.jersey04.models.JobExecution>();

        for (com.company.jersey04.models.JobExecution donor : donors) {
            boolean accepted = true;

            if (filterDescs != null) {
                for (FilterDesc filterDesc : filterDescs) {
                    switch (filterDesc.getField().getName()) {
                        case "firstName":
                            if (!donor.getFirstName().equalsIgnoreCase((String) filterDesc.getValue()))
                                accepted = false;
                            break;
                        case "lastName":
                            if (!donor.getLastName().equalsIgnoreCase((String) filterDesc.getValue()))
                                accepted = false;
                            break;
                    }
                }
            }

            if (accepted)
                result.add(donor);
        }

        return result;
    }
}
