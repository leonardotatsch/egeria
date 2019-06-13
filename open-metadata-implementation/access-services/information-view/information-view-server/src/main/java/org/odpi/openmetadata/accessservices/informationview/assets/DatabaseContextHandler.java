/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.informationview.assets;

import org.odpi.openmetadata.accessservices.informationview.contentmanager.OMEntityDao;
import org.odpi.openmetadata.accessservices.informationview.events.DatabaseSource;
import org.odpi.openmetadata.accessservices.informationview.events.TableColumn;
import org.odpi.openmetadata.accessservices.informationview.events.TableContextEvent;
import org.odpi.openmetadata.accessservices.informationview.events.TableSource;
import org.odpi.openmetadata.accessservices.informationview.ffdc.InformationViewErrorCode;
import org.odpi.openmetadata.accessservices.informationview.ffdc.exceptions.runtime.ContextLoadException;
import org.odpi.openmetadata.accessservices.informationview.ffdc.exceptions.runtime.EntityNotFoundException;
import org.odpi.openmetadata.accessservices.informationview.ffdc.exceptions.runtime.IncorrectModelException;
import org.odpi.openmetadata.accessservices.informationview.ffdc.exceptions.runtime.IncorrectTypeException;
import org.odpi.openmetadata.accessservices.informationview.ffdc.exceptions.runtime.RetrieveEntityException;
import org.odpi.openmetadata.accessservices.informationview.utils.Constants;
import org.odpi.openmetadata.accessservices.informationview.views.ColumnContextBuilder;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryConnector;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.EntityNotKnownException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.EntityProxyOnlyException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.FunctionNotSupportedException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.PagingErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.PropertyErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.RepositoryErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.TypeErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseContextHandler {


    private static final Logger log = LoggerFactory.getLogger(DatabaseContextHandler.class);
    private org.odpi.openmetadata.accessservices.informationview.contentmanager.OMEntityDao omEntityDao;
    private OMRSRepositoryHelper repositoryHelper;
    private OMRSAuditLog auditLog;
    private ColumnContextBuilder columnContextBuilder;

    public DatabaseContextHandler(OMRSRepositoryConnector enterpriseConnector, OMEntityDao omEntityDao,
                                  OMRSAuditLog auditLog) {
        this.omEntityDao = omEntityDao;
        this.repositoryHelper = enterpriseConnector.getRepositoryHelper();
        this.auditLog = auditLog;
        this.columnContextBuilder = new ColumnContextBuilder(enterpriseConnector);
    }

    public List<DatabaseSource> getDatabases(int startFrom, int pageSize) {
        InstanceProperties instanceProperties = omEntityDao.buildMatchingInstanceProperties(Collections.emptyMap(), true);
        try {
            List<EntityDetail> entities = omEntityDao.findEntities(instanceProperties, Constants.DATABASE, startFrom, pageSize);
            return buildDatabaseContext(entities);
        } catch (InvalidParameterException | PropertyErrorException | TypeErrorException | FunctionNotSupportedException | PagingErrorException | UserNotAuthorizedException | RepositoryErrorException e) {
            throw new RetrieveEntityException(DatabaseContextHandler.class.getName(),
                    InformationViewErrorCode.GET_ENTITY_EXCEPTION.getFormattedErrorMessage("type", Constants.DATABASE, e.getMessage()), InformationViewErrorCode.GET_ENTITY_EXCEPTION.getSystemAction(),
                    InformationViewErrorCode.GET_ENTITY_EXCEPTION.getUserAction(), e);
        }
    }

    public List<TableSource> getTables(String databaseGuid, int startFrom, int pageSize) {
        EntityDetail database = getEntity(databaseGuid, Constants.DATA_STORE);
        return columnContextBuilder.getTablesForDatabase(database.getGUID(), startFrom, pageSize);
    }

    public List<TableContextEvent> getTableContext(String tableGuid) {
        EntityDetail table = getEntity(tableGuid , Constants.RELATIONAL_TABLE);
        List<Relationship> relationships = columnContextBuilder.getSchemaTypeRelationships(table, Constants.SCHEMA_ATTRIBUTE_TYPE, Constants.START_FROM, Constants.PAGE_SIZE);
        if (relationships != null && !relationships.isEmpty()) {
            return columnContextBuilder.getTableContext(relationships.get(0).getEntityTwoProxy().getGUID(), Constants.START_FROM, Constants.PAGE_SIZE);
        } else {
            throw new ContextLoadException(InformationViewErrorCode.RETRIEVE_CONTEXT_EXCEPTION.getHttpErrorCode(), DatabaseContextHandler.class.getName(), InformationViewErrorCode.RETRIEVE_CONTEXT_EXCEPTION.getFormattedErrorMessage(tableGuid, "Schema attribute type relationship doesn't exist"), InformationViewErrorCode.RETRIEVE_CONTEXT_EXCEPTION.getSystemAction(), InformationViewErrorCode.RETRIEVE_CONTEXT_EXCEPTION.getUserAction(), null);
        }
    }

    private List<DatabaseSource> buildDatabaseContext(List<EntityDetail> entities) {
        List<DatabaseSource> databaseSources = new ArrayList<>();
        if (entities != null && !entities.isEmpty()) {
            for (EntityDetail entityDetail : entities) {
                List<TableContextEvent> contexts;
                contexts = columnContextBuilder.getDatabaseContext(entityDetail.getGUID());
                databaseSources.add(contexts.get(0).getTableSource().getDatabaseSource());
            }
        }
        return databaseSources;
    }

    public List<TableColumn> getTableColumns(String tableGuid, int startFrom, int pageSize) {
        EntityDetail table = getEntity(tableGuid, Constants.RELATIONAL_TABLE);
        List<Relationship> relationships;

        relationships = columnContextBuilder.getSchemaTypeRelationships(table, Constants.SCHEMA_ATTRIBUTE_TYPE, Constants.START_FROM, Constants.PAGE_SIZE);
        if (relationships != null && !relationships.isEmpty()) {
            return columnContextBuilder.getTableColumns(relationships.get(0).getEntityTwoProxy().getGUID(), startFrom, pageSize);
        }
        else{
            throw new IncorrectModelException(DatabaseContextHandler.class.getName(),
                    InformationViewErrorCode.INCORRECT_MODEL_EXCEPTION.getFormattedErrorMessage(tableGuid, "Table Schema Type is missing"),
                    InformationViewErrorCode.INCORRECT_MODEL_EXCEPTION.getSystemAction(),
                    InformationViewErrorCode.INCORRECT_MODEL_EXCEPTION.getUserAction(), null);
        }
    }

    private EntityDetail getEntity(String guid, String typeName) {
        EntityDetail entityDetail;
        try {
            entityDetail = omEntityDao.getEntityByGuid(guid);
        } catch (EntityProxyOnlyException | EntityNotKnownException | UserNotAuthorizedException | InvalidParameterException | RepositoryErrorException e) {
            throw new EntityNotFoundException(InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getHttpErrorCode(), DatabaseContextHandler.class.getName(), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getFormattedErrorMessage(Constants.GUID, guid, typeName), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getSystemAction(), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getUserAction(), e);
        }
        if (entityDetail == null)
            throw new EntityNotFoundException(InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getHttpErrorCode(), DatabaseContextHandler.class.getName(), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getFormattedErrorMessage(Constants.GUID, guid, typeName), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getSystemAction(), InformationViewErrorCode.ENTITY_NOT_FOUND_EXCEPTION.getUserAction(), null);
        if(!repositoryHelper.isTypeOf("getEntity", entityDetail.getType().getTypeDefName(), typeName)){
            throw new IncorrectTypeException(InformationViewErrorCode.INCORRECT_TYPE_EXCEPTION.getHttpErrorCode(), DatabaseContextHandler.class.getName(), InformationViewErrorCode.INCORRECT_TYPE_EXCEPTION.getFormattedErrorMessage(Constants.GUID,  guid, typeName), InformationViewErrorCode.INCORRECT_TYPE_EXCEPTION.getSystemAction(), InformationViewErrorCode.INCORRECT_TYPE_EXCEPTION.getUserAction(), null);
        }
        return entityDetail;
    }
}
