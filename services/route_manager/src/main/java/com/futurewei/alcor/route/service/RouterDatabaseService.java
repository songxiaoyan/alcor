/*
Copyright 2019 The Alcor Authors.

Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.futurewei.alcor.route.service;

import com.futurewei.alcor.common.db.CacheException;
import com.futurewei.alcor.common.exception.DatabasePersistenceException;
import com.futurewei.alcor.common.exception.ResourceNotFoundException;
import com.futurewei.alcor.common.exception.ResourcePersistenceException;
import com.futurewei.alcor.web.entity.route.RouteEntry;
import com.futurewei.alcor.web.entity.route.Router;

import java.util.Map;

public interface RouterDatabaseService {

    public Router getByRouterId (String routerId) throws ResourceNotFoundException, ResourcePersistenceException;
    public Map getAllRouters () throws CacheException;
    public Map getAllRouters (Map<String, Object[]> queryParams) throws CacheException;
    public void addRouter (Router router) throws DatabasePersistenceException;
    public void deleteRouter (String id) throws Exception;

}
