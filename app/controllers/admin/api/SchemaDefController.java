package controllers.admin.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ForeignKey;
import models.SchemaDef;
import models.TableDef;
import models.Task;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.SchemaDefService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * @author fabiomazzone
 */
@Singleton
public class SchemaDefController extends Controller {
    private final SchemaDefService schemaDefService;

    @Inject
    public SchemaDefController(
            SchemaDefService schemaDefService) {
        this.schemaDefService = schemaDefService;
    }

    public CompletionStage<Result> readAll() {
        return CompletableFuture
                .supplyAsync(this.schemaDefService::readAll)
                .thenApply(schemaDefList ->
                    ok(Json.toJson(schemaDefList.parallelStream()
                            .map(SchemaDefTransformer::small)
                            .collect(Collectors.toList())))
                );
    }

    public CompletionStage<Result> read(Long id) {
        return CompletableFuture
                .supplyAsync(() -> this.schemaDefService.read(id))
                .thenApply(schemaDef ->
                    ok(SchemaDefTransformer.full(schemaDef)));
    }
}


class SchemaDefTransformer {
    public static ObjectNode small(SchemaDef schemaDef) {
        ObjectNode schemaDefNode = Json.newObject();

        schemaDefNode.put("id", schemaDef.getId());
        schemaDefNode.put("name", schemaDef.getName());

        schemaDefNode.put("createdAt", schemaDef.getCreatedAt());
        schemaDefNode.put("modifiedAt", schemaDef.getModifiedAt());

        return schemaDefNode;
    }

    public static ObjectNode full(SchemaDef schemaDef) {
        ObjectNode schemaDefNode = small(schemaDef);

        ArrayNode tableDefIds = Json.newArray();
        ArrayNode foreignKeyIds = Json.newArray();
        ArrayNode taskIds = Json.newArray();

        schemaDef.getTableDefList().parallelStream().map(TableDef::getId).forEach(tableDefIds::add);
        schemaDef.getForeignKeyList().parallelStream().map(ForeignKey::getId).forEach(foreignKeyIds::add);
        schemaDef.getTaskList().parallelStream().map(Task::getId).forEach(taskIds::add);

        schemaDefNode.set("tableDefList", tableDefIds);
        schemaDefNode.set("foreignKeyList", foreignKeyIds);
        schemaDefNode.set("taskList", taskIds);

        return schemaDefNode;
    }
}