package org.jkiss.dbeaver.ext.generic.model;

import org.jkiss.dbeaver.model.anno.Property;
import org.jkiss.dbeaver.model.struct.DBSConstraintCascade;
import org.jkiss.dbeaver.model.struct.DBSConstraintDefferability;
import org.jkiss.dbeaver.model.struct.DBSConstraintType;
import org.jkiss.dbeaver.model.struct.DBSForeignKey;

/**
 * GenericForeignKey
 */
public class GenericForeignKey extends GenericConstraint implements DBSForeignKey<
    GenericDataSource,
    GenericTable>
{
    private GenericConstraint referencedKey;
    private DBSConstraintCascade deleteRule;
    private DBSConstraintCascade updateRule;
    private DBSConstraintDefferability defferability;

    public GenericForeignKey(GenericTable table,
        String name,
        String remarks,
        GenericConstraint referencedKey,
        DBSConstraintCascade deleteRule,
        DBSConstraintCascade updateRule,
        DBSConstraintDefferability defferability)
    {
        super(DBSConstraintType.FOREIGN_KEY, table, name, remarks);
        this.referencedKey = referencedKey;
        this.deleteRule = deleteRule;
        this.updateRule = updateRule;
        this.defferability = defferability;
    }

    @Property(name = "Ref Table", viewable = true, order = 3)
    public GenericTable getReferencedTable()
    {
        return referencedKey.getTable();
    }

    @Property(name = "Ref Constraint", viewable = true, order = 4)
    public GenericConstraint getReferencedKey()
    {
        return referencedKey;
    }

    @Property(name = "On Delete", viewable = true, order = 5)
    public DBSConstraintCascade getDeleteRule()
    {
        return deleteRule;
    }

    @Property(name = "On Update", viewable = true, order = 6)
    public DBSConstraintCascade getUpdateRule()
    {
        return updateRule;
    }

    @Property(name = "Defferability", viewable = true, order = 7)
    public DBSConstraintDefferability getDefferability()
    {
        return defferability;
    }

}
