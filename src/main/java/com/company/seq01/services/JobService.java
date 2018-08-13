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
public class JobService {

    protected List<com.company.jersey04.models.Job> charities = new ArrayList<com.company.jersey04.models.Job>();

    public JobService() {
        charities.add(new com.company.jersey04.models.Job(1, "Red Cross", "66-555555", "www.redcross.org"));
        charities.add(new com.company.jersey04.models.Job(2, "ASPCA", "99-555555", "www.aspca.org"));
        charities.add(new com.company.jersey04.models.Job(3, "United Way", "33-555555", "www.unitedway.org"));
        charities.add(new com.company.jersey04.models.Job(4, "American Heart Assoc", "55-555555", "www.aha.org"));
        charities.add(new com.company.jersey04.models.Job(5, "Polar Bear Assoc", "45-555555", "www.polarbears.org"));
        charities.add(new com.company.jersey04.models.Job(6, "Stanford University", "37-555555", "www.stanford.edu"));
    }

    public com.company.jersey04.models.Job getCharity(int id) {
        for (com.company.jersey04.models.Job charity : charities) {
            if (charity.getId() == id)
                return charity;
        }
        return null;
    }

    public List<com.company.jersey04.models.Job> getCharities(int limit, int offset, List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.Job> result = applyFilter(filterDescs);

        if (offset > 0 && offset >= result.size())
            result = new ArrayList<com.company.jersey04.models.Job>();
        else if (offset > 0)
            result = result.subList(offset, result.size());

        return result;
    }

    public long getCharitiesCount(List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.Job> result = applyFilter(filterDescs);

        return (long) result.size();
    }

    private List<com.company.jersey04.models.Job> applyFilter(List<FilterDesc> filterDescs) {
        List<com.company.jersey04.models.Job> result = new ArrayList<com.company.jersey04.models.Job>();

        for (com.company.jersey04.models.Job charity : charities) {
            boolean accepted = true;

            if (filterDescs != null) {
                for (FilterDesc filterDesc : filterDescs) {
                    switch (filterDesc.getField().getName()) {
                        case "name":
                            if (!charity.getName().equalsIgnoreCase((String) filterDesc.getValue()))
                                accepted = false;
                            break;
                        case "ein":
                            if (!charity.getEin().equals(filterDesc.getValue()))
                                accepted = false;
                            break;
                        case "website":
                            if (!charity.getWebsite().equalsIgnoreCase((String) filterDesc.getValue()))
                                accepted = false;
                            break;
                    }
                }
            }

            if (accepted)
                result.add(charity);
        }
        return result;
    }
}
