package controllers.api;

import authenticators.AdminAuthenticator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import services.SchemaDefService;
import services.SessionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * @author fabiomazzone
 */
@Singleton
public class SchemaDefController extends Controller {
    private final SchemaDefService schemaDefService;
    private final HttpExecutionContext httpExecutionContext;
    private final SessionService sessionService;

    @Inject
    public SchemaDefController(
            SchemaDefService schemaDefService,
            HttpExecutionContext httpExecutionContext,
            SessionService sessionService) {

        this.schemaDefService = schemaDefService;
        this.httpExecutionContext = httpExecutionContext;
        this.sessionService = sessionService;
    }

    /**
     * API Endpoint for POST /api/schemaDef
     *
     * @return returns the status of the action
     */
    @Security.Authenticated(AdminAuthenticator.class)
    public CompletionStage<Result> create() {
        return CompletableFuture
                .supplyAsync(this.schemaDefService::create, this.httpExecutionContext.current())
                .thenApply(schemaDefForm -> {
                    if(schemaDefForm.hasErrors()) {
                        Logger.warn(schemaDefForm.errorsAsJson().toString());
                        return badRequest(schemaDefForm.errorsAsJson());
                    }
                    SchemaDef schemaDef = this.schemaDefService.read(schemaDefForm.get().getId());
                    return ok(transform(schemaDef));
                });
    }

    @Security.Authenticated(AdminAuthenticator.class)
    public Result readAll() {
        Session session = this.sessionService.getSession(request());

        if(session == null) {
            return unauthorized();
        }

        List<SchemaDef> schemaDefList = this.schemaDefService.readAll();

        List<JsonNode> jsonNodeList = schemaDefList.stream().map(this::transformBase).collect(Collectors.toList());

        return ok(Json.toJson(jsonNodeList));
    }

    public CompletionStage<Result> read(Long id) {
        return CompletableFuture
                .supplyAsync(() -> this.schemaDefService.read(id), this.httpExecutionContext.current())
                .thenApply(schemaDef -> {
                    if(schemaDef == null) {
                        return notFound();
                    }
                    Session session = this.sessionService.getSession(Http.Context.current().request());
                    if (session != null && sessionService.isAdmin(session)) {
                        return ok(transform(schemaDef));
                    }
                    return ok(Json.toJson(schemaDef));
                });
    }

    public CompletionStage<Result> update(Long id) {
        return CompletableFuture
                .supplyAsync(() -> this.schemaDefService.update(id), this.httpExecutionContext.current())
                .thenApply(schemaDefForm -> {
                   if(schemaDefForm.hasErrors()) {
                       Logger.warn(schemaDefForm.errorsAsJson().toString());
                       return badRequest(schemaDefForm.errorsAsJson());
                   }
                   SchemaDef schemaDef = this.schemaDefService.read(id);
                   return ok(transform(schemaDef));
                });
    }

    @Security.Authenticated(AdminAuthenticator.class)
    public CompletionStage<Result> delete(Long id) {
        return CompletableFuture
                .supplyAsync(() -> this.schemaDefService.delete(id), this.httpExecutionContext.current())
                .thenApply(schemaDefForm -> {
                    if(schemaDefForm.hasErrors()) {
                        return badRequest(schemaDefForm.errorsAsJson());
                    }
                    return ok();
                });
    }

    private ObjectNode transformBase(SchemaDef schemaDef) {
        ObjectNode schemaDefNode = Json.newObject();

        schemaDefNode.put("id", schemaDef.getId());
        schemaDefNode.put("name", schemaDef.getName());
        schemaDefNode.put("available", schemaDef.isAvailable());

        // schemaDefNode.set("reactions", reactionNode());

        schemaDefNode.put("createdAt", schemaDef.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
        schemaDefNode.put("modifiedAt", schemaDef.getModifiedAt().format(DateTimeFormatter.ISO_DATE_TIME));

        return schemaDefNode;
    }

    private ObjectNode transformTableDefBase(TableDef tableDef) {
        ObjectNode tableDefNode = Json.newObject();

        tableDefNode.put("id", tableDef.getId());
        tableDefNode.put("name", tableDef.getName());

        return tableDefNode;
    }

    private ObjectNode transform(SchemaDef schemaDef) {
        ObjectNode schemaDefNode = transformBase(schemaDef);
        ObjectNode relationNode = Json.newObject();

        ArrayNode tableDefIds = Json.newArray();
        ArrayNode foreignKeyIds = Json.newArray();
        ArrayNode taskIds = Json.newArray();

        schemaDef.getTableDefList().stream().map(this::transformTableDefBase).forEach(tableDefIds::add);
        schemaDef.getForeignKeyList().stream().map(ForeignKey::getId).forEach(foreignKeyIds::add);
        schemaDef.getTaskList().stream().map(Task::getId).forEach(taskIds::add);

        relationNode.set("tableDefList", tableDefIds);
        relationNode.set("foreignKeyList", foreignKeyIds);
        relationNode.set("taskList", taskIds);

        schemaDefNode.set("relations", relationNode);

        return schemaDefNode;
    }
}
