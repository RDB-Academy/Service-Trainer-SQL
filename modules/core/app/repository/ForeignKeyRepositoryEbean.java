package repository;

import com.avaje.ebean.Model;
import com.google.inject.Singleton;
import models.ForeignKey;

import java.util.List;

/**
 * @author Fabio Mazzone
 */
@Singleton
public class ForeignKeyRepositoryEbean implements ForeignKeyRepository
{
    private Model.Finder<Long, ForeignKey> find = new Model.Finder<>(ForeignKey.class);

    public List<ForeignKey> getAll()
    {
        return this.find.all();
    }

    public ForeignKey getById(Long id)
    {
        return this.find.byId(id);
    }

    @Override
    public void save(ForeignKey foreignKey)
    {
        this.find.db().save(foreignKey);
    }

    @Override
    public void delete(ForeignKey foreignKey)
    {
        this.find.db().delete(foreignKey);
    }


}
