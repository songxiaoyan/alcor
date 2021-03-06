/*
 *
 * Copyright 2019 The Alcor Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 * /
 */

package com.futurewei.alcor.macmanager.dao.api;

import com.futurewei.alcor.common.db.CacheException;
import com.futurewei.alcor.web.entity.mac.MacAllocate;

import java.util.Map;
import java.util.Set;

public interface IRangeMappingRepository {

    long getUsedCapacity(String rangeId) throws CacheException;

    void addItem(String rangeId, Long macLong) throws CacheException;

    Boolean putIfAbsent(String rangeId, Long macLong) throws CacheException;

    Boolean releaseMac(String rangeId, Long macLong) throws CacheException;

    void removeRange(String rangeId) throws CacheException;

    void putAll(String rangeId, Map<Long, String> entries) throws CacheException;

    Set<Long> getAll(String rangeId, Set<Long> macs) throws CacheException;
}
