package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * Created by invisible on 11/11/16.
 */
@Entity
public class TaskTrial extends BaseModel {
    @Id
    private Long id;

    @ManyToOne
    private Task task;

    @JsonIgnore
    private long databaseCreationSeed;

    private String userStatement;

    private boolean isCorrect;

    @JsonIgnore
    private Date beginDate;

    @JsonIgnore
    private Date submitDate;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public Task getTask() {
        return task;
    }

    @JsonGetter("task")
    public long getTaskId() {
        return this.getTask().getId();
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Long getDatabaseCreationSeed() {
        return databaseCreationSeed;
    }

    public void setDatabaseCreationSeed(Long databaseCreationSeed) {
        this.databaseCreationSeed = databaseCreationSeed;
    }

    public String getUserStatement() {
        return userStatement;
    }

    public void setUserStatement(String userStatement) {
        this.userStatement = userStatement;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }
}
