/**
 * Copyright (C) 2011 Akiban Technologies Inc.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 */

package com.akiban.sql.optimizer.plan;

import com.akiban.sql.optimizer.plan.JoinNode.JoinType;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Joins within a single {@link TableGroup} represented as a tree
 * whose structure mirrors that of the group.
 * This is an intermediate form between the original tree of joins based on the
 * original SQL syntax and the <code>Scan</code> and <code>Lookup</code> form
 * once an access path has been chosen.
 */
public class TableGroupJoinTree extends BaseJoinable 
                                implements Iterable<TableGroupJoinTree.TableGroupJoinNode>
{
    public static class TableGroupJoinNode implements Iterable<TableGroupJoinNode> {
        TableSource table;
        TableGroupJoinNode parent, nextSibling, firstChild;
        JoinType parentJoinType;

        public TableGroupJoinNode(TableSource table) {
            this.table = table;
        }

        public TableSource getTable() {
            return table;
        }

        public TableGroupJoinNode getParent() {
            return parent;
        }
        public void setParent(TableGroupJoinNode parent) {
            this.parent = parent;
        }
        public TableGroupJoinNode getNextSibling() {
            return nextSibling;
        }
        public void setNextSibling(TableGroupJoinNode nextSibling) {
            this.nextSibling = nextSibling;
        }
        public TableGroupJoinNode getFirstChild() {
            return firstChild;
        }
        public void setFirstChild(TableGroupJoinNode firstChild) {
            this.firstChild = firstChild;
        }
        public JoinType getParentJoinType() {
            return parentJoinType;
        }
        public void setParentJoinType(JoinType parentJoinType) {
            this.parentJoinType = parentJoinType;
        }

        /** Find the given table in this (sub-)tree. */
        public TableGroupJoinNode findTable(TableSource table) {
            for (TableGroupJoinNode node : this) {
                if (node.getTable() == table) {
                    return node;
                }
            }
            return null;
        }

        @Override
        public Iterator<TableGroupJoinNode> iterator() {
            return new TableGroupJoinIterator(this);
        }
    }

    static class TableGroupJoinIterator implements Iterator<TableGroupJoinNode> {
        TableGroupJoinNode root, next;
        
        TableGroupJoinIterator(TableGroupJoinNode root) {
            this.root = this.next = root;
        }

        @Override
        public boolean hasNext() {
            return (next != null);
        }

        @Override
        public TableGroupJoinNode next() {
            if (next == null)
                throw new NoSuchElementException();
            TableGroupJoinNode node = next;
            advance();
            return node;
        }
        
        protected void advance() {
            TableGroupJoinNode node = next.getFirstChild();
            if (node != null) {
                next = node;
                return;
            }
            while (true) {
                node = next.getNextSibling();
                if (node != null) {
                    next = node;
                    return;
                }
                if (next == root) {
                    next = null;
                    return;
                }
                next = next.getParent();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private TableGroup group;
    private TableGroupJoinNode root;
    
    public TableGroupJoinTree(TableGroupJoinNode root) {
        this.group = root.getTable().getGroup();
        this.root = root;
    }

    public TableGroup getGroup() {
        return group;
    }
    public TableGroupJoinNode getRoot() {
        return root;
    }
    
    @Override
    public Iterator<TableGroupJoinNode> iterator() {
        return new TableGroupJoinIterator(root);
    }

    public boolean accept(PlanVisitor v) {
        if (v.visitEnter(this)) {
            TableGroupJoinNode next = root;
            top:
            while (true) {
                if (v.visitEnter(next.getTable())) {
                    TableGroupJoinNode node = next.getFirstChild();
                    if (node != null) {
                        next = node;
                        continue;
                    }
                }
                while (true) {
                    if (v.visitLeave(next.getTable())) {
                        TableGroupJoinNode node = next.getNextSibling();
                        if (node != null) {
                            next = node;
                            break;
                        }
                    }
                    if (next == root)
                        break top;
                    next = next.getParent();
                }
            }
        }
        return v.visitLeave(this);
    }

    public String summaryString() {
        StringBuilder str = new StringBuilder(super.summaryString());
        str.append("(");
        str.append(group);
        str.append(", ");
        summarizeJoins(str);
        str.append(")");
        return str.toString();
    }

    private void summarizeJoins(StringBuilder str) {
        for (TableGroupJoinNode node : this) {
            if (node != root) {
                str.append(" ");
                str.append(node.getParentJoinType());
                str.append(" ");
            }
            str.append(node.getTable().getTable().getTable().getName().getTableName());
        }
    }

}