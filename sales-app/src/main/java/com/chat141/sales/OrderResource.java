package com.chat141.sales;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

  @Inject OrderService service;

  @POST
  @RolesAllowed({"SALES","ADMIN"})
  public Response create(OrderDTO dto, @Context UriInfo uri) {
    Long id = service.create(dto);
    return Response.created(uri.getAbsolutePathBuilder().path(id.toString()).build()).build();
  }

  @POST
  @Path("{id}/pay")
  @RolesAllowed({"SALES","ADMIN"})
  public Response pay(@PathParam("id") Long id) {
    service.markPaid(id);
    return Response.noContent().build();
  }
}
