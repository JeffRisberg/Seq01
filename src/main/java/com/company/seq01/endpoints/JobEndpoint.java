package com.company.seq01.endpoints;

import com.company.common.FilterDesc;
import com.company.common.SortDesc;
import com.company.seq01.models.Job;
import com.company.seq01.services.JobService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("charities")
public class JobEndpoint extends AbstractEndpoint {

    protected JobService jobService;

    @Inject
    public JobEndpoint(JobService jobService) {
        this.jobService = jobService;
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("id") Integer id) {

        Job data = null;// jobService.getCharity(id);

        return createEntityResponse(data, null);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchList(
            @DefaultValue("50") @QueryParam("limit") int limit,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("") @QueryParam("sort") String sortStr,
            @Context UriInfo uriInfo) {

        MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
        List<FilterDesc> filterDescs = this.parseFiltering(queryParams);
        List<SortDesc> sortDescs = this.parseSortStr(sortStr);

        List<Job> data = null;// charityService.getCharities(limit, offset, filterDescs);
        long totalCount = 0;//charityService.getCharitiesCount(filterDescs);

        return createEntityListResponse(data, totalCount, limit, offset, null);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id) {

        Job data = null;// charityService.getCharity(id);

        return createDeleteResponse(data, null);
    }
}
