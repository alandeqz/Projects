package kz.edu.nu.cs.se.hw;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/items")
public class ListItemsService {

    private List<String> list = new CopyOnWriteArrayList<String>();

    public ListItemsService() {
        for (int i = 0; i < 20; i++) {
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            list.add("Entry " + String.valueOf(i) + "<p style=\"font-style: italic\">Created on " + date + " at " + time + "</p>");
        }

        Collections.reverse(list);
    }

    @GET
    public Response getList() {
        Gson gson = new Gson();
        return Response.ok(gson.toJson(list)).build();
    }

    @GET
    @Path("{id: [0-9]+}")
    public Response getListItem(@PathParam("id") String id) {
        int i = Integer.parseInt(id);
        return Response.ok(list.get(i)).build();
    }

    @POST
    public Response postListItem(@FormParam("newEntry") String entry) {
        if (!entry.isEmpty()) {
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            list.add(0, entry + "<p style=\"font-style: italic\">Created on " + date + " at " + time + "</p>");
        }
        return Response.ok().build();
    }

    @DELETE
    public Response deleteItems() {
        list.clear();
        return Response.ok().build();
    }

    @DELETE
    @Path("{id: [0-9]+}")
    public Response deleteItem(@PathParam("id") String id) {
        list.remove(Integer.parseInt(id));
        return Response.ok().build();
    }
}