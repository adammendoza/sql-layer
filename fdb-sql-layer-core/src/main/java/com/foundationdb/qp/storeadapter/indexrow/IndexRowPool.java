/**
 * Copyright (C) 2009-2013 FoundationDB, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.foundationdb.qp.storeadapter.indexrow;

import com.foundationdb.qp.operator.StoreAdapter;
import com.foundationdb.qp.row.IndexRow;
import com.foundationdb.qp.rowtype.IndexRowType;
import com.foundationdb.server.util.LRUCacheMap;

import java.util.*;

public class IndexRowPool
{
    public IndexRow takeIndexRow(StoreAdapter adapter, IndexRowType indexRowType)
    {
        return adapterPool(adapter).takeIndexRow(indexRowType);
    }

    public void returnIndexRow(StoreAdapter adapter, IndexRowType indexRowType, IndexRow indexRow)
    {
        adapterPool(adapter).returnIndexRow(indexRowType, indexRow);
    }

    public IndexRowPool()
    {
    }

    // For use by this class

    private AdapterPool adapterPool(StoreAdapter adapter)
    {
        LRUCacheMap<Long, AdapterPool> adapterPool = threadAdapterPools.get();
        AdapterPool pool = adapterPool.get(adapter.id());
        if (pool == null) {
            pool = new AdapterPool(adapter);
            adapterPool.put(adapter.id(), pool);
        }
        return pool;
    }


    // Class state

    private static final int CAPACITY_PER_THREAD = 10;

    // Object state

    private final ThreadLocal<LRUCacheMap<Long, AdapterPool>> threadAdapterPools =
        new ThreadLocal<LRUCacheMap<Long, AdapterPool>>()
        {
            @Override
            protected LRUCacheMap<Long, AdapterPool> initialValue()
            {
                return new LRUCacheMap<>(CAPACITY_PER_THREAD);
            }
        };

    // Inner classes

    private static class AdapterPool
    {
        public IndexRow takeIndexRow(IndexRowType indexRowType)
        {
            IndexRow indexRow = null;
            Deque<IndexRow> indexRows = indexRowCache.get(indexRowType);
            if (indexRows != null && !indexRows.isEmpty()) {
                indexRow = indexRows.removeLast();
                indexRow.reset();
            }
            if (indexRow == null) {
                indexRow = adapter.newIndexRow(indexRowType);
            }
            return indexRow;
        }

        public void returnIndexRow(IndexRowType indexRowType, IndexRow indexRow)
        {
            Deque<IndexRow> indexRows = indexRowCache.get(indexRowType);
            if (indexRows == null) {
                indexRows = new ArrayDeque<>();
                indexRowCache.put(indexRowType, indexRows);
            }
            assert !indexRows.contains(indexRow);
            indexRows.addLast(indexRow);
        }

        public AdapterPool(StoreAdapter adapter)
        {
            this.adapter = adapter;
        }

        private final StoreAdapter adapter;
        private final Map<IndexRowType, Deque<IndexRow>> indexRowCache = new HashMap<>();
    }
}
