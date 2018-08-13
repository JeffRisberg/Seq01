package com.company.seq01.endpoints;

import com.company.common.FilterDesc;
import com.company.common.SortDesc;
import com.company.seq01.models.JobExecution;
import com.company.seq01.services.JobExecutionService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("jobExecutions")
public class JobExecutionEndpoint extends AbstractEndpoint {

    protected JobExecutionService jobExecutionService;

    @Inject
    public JobExecutionEndpoint(JobExecutionService jobExecutionService) {
        this.jobExecutionService = jobExecutionService;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetch(@PathParam("id") Integer id) {

        JobExecution data = null;//donorService.getDonor(id);

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

        List<JobExecution> data = null;//donorService.getDonors(limit, offset, filterDescs);
        long totalCount = 0; //donorService.getDonorsCount(filterDescs);

        return createEntityListResponse(data, totalCount, limit, offset, null);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer id) {

        JobExecution data = null;//donorService.getDonor(id);

        return createDeleteResponse(data, null);
    }
}
