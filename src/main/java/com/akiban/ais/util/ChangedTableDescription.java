/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.akiban.ais.util;

import com.akiban.ais.model.TableName;
import com.akiban.ais.model.UserTable;
import com.akiban.util.ArgumentValidation;

import java.util.Collection;
import java.util.Map;

/**
 * Information describing the state of an altered table
 */
public class ChangedTableDescription{
    public static enum ParentChange { NONE, UPDATE, ADD, DROP }

    private final TableName tableName;
    private final UserTable newDefinition;
    private final Map<String,String> colNames;
    private final ParentChange parentChange;
    private final TableName parentName;
    private final Map<String,String> parentColNames;
    private final Map<String,String> preserveIndexes;
    private final Collection<TableName> droppedSequences;

    /**
     * @param tableName Current name of the table being changed.
     * @param newDefinition New definition of the table.
     * @param preserveIndexes Mapping of new index names to old.
     */
    public ChangedTableDescription(TableName tableName, UserTable newDefinition, Map<String,String> colNames,
                                   ParentChange parentChange, TableName parentName, Map<String,String> parentColNames,
                                   Map<String, String> preserveIndexes, Collection<TableName> droppedSequences) {
        ArgumentValidation.notNull("tableName", tableName);
        ArgumentValidation.notNull("preserveIndexes", preserveIndexes);
        this.tableName = tableName;
        this.newDefinition = newDefinition;
        this.colNames = colNames;
        this.parentChange = parentChange;
        this.parentName = parentName;
        this.parentColNames = parentColNames;
        this.preserveIndexes = preserveIndexes;
        this.droppedSequences = droppedSequences;
    }

    public TableName getOldName() {
        return tableName;
    }

    public TableName getNewName() {
        return (newDefinition != null) ? newDefinition.getName() : tableName;
    }

    public UserTable getNewDefinition() {
        return newDefinition;
    }

    public Map<String,String> getColNames() {
        return colNames;
    }

    public ParentChange getParentChange() {
        return parentChange;
    }

    public TableName getParentName() {
        return parentName;
    }

    public Map<String,String> getParentColNames() {
        return parentColNames;
    }

    public Map<String,String> getPreserveIndexes() {
        return preserveIndexes;
    }

    public Collection<TableName> getDroppedSequences() {
        return droppedSequences;
    }

    public boolean isNewGroup() {
        return (parentChange != ParentChange.NONE);
    }

    @Override
    public String toString() {
        return toString(getOldName(), getNewName(), isNewGroup(), getParentChange(), getPreserveIndexes());
    }

    public static String toString(TableName oldName, TableName newName, boolean newGroup, ParentChange groupChange, Map<String,String> indexMap) {
        return oldName + "=" + newName + "[newGroup=" + newGroup + "][parentChange=" + groupChange + "]" + indexMap.toString();
    }
}
