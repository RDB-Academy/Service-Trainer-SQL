package controllers;

import models.TaskTrial;
import parser.SQLParser;
import parser.SQLParserFactory;
import parser.utils.extensionMaker.ExtensionMaker;
import models.SchemaDef;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repository.SchemaDefRepository;
import services.TaskTrialService;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * @author fabiomazzone
 */
public class TestController extends Controller {
    private final SchemaDefRepository   schemaDefRepository;
    private final SQLParserFactory      sqlParserFactory;
    private final TaskTrialService      taskTrialService;

    @Inject
    public TestController(
            SchemaDefRepository schemaDefRepository,
            SQLParserFactory sqlParserFactory,
            TaskTrialService taskTrialService) {

        this.schemaDefRepository = schemaDefRepository;
        this.sqlParserFactory = sqlParserFactory;
        this.taskTrialService = taskTrialService;
    }

    public Result test() {
        session().clear();
        ExtensionMaker extensionMaker = new ExtensionMaker(12345L);
        SchemaDef schemaDef = this.schemaDefRepository.getById(1L);

        String[][] statements = extensionMaker.buildStatements(schemaDef);

        return ok(Arrays.deepToString(statements));
    }

    public Result parserCreate() {
        SchemaDef schemaDef = schemaDefRepository.getById(1L);
        schemaDef.save();

        TaskTrial taskTrial = this.taskTrialService.getNewTaskTrial(null);

        this.sqlParserFactory.createParser(taskTrial);

        return ok(Json.toJson(taskTrial));
    }

    public Result parserGet(Long id) throws ExecutionException, InterruptedException {
        TaskTrial taskTrial = this.taskTrialService.getById(id);

        if(taskTrial == null) {
            return notFound();
        }

        System.out.println("Test");

        SQLParser sqlParser = this.sqlParserFactory.getParser(taskTrial).get();

        System.out.println("Test");

        return ok();
    }
}
