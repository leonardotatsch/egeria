/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.openconnectors.governancedaemonconnectors.securitysync.rangerconnector.processor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.odpi.openmetadata.accessservices.governanceengine.api.objects.Context;
import org.odpi.openmetadata.accessservices.governanceengine.api.objects.GovernanceClassification;
import org.odpi.openmetadata.accessservices.governanceengine.api.objects.GovernedAsset;
import org.odpi.openmetadata.adminservices.configuration.properties.SecuritySyncConfig;
import org.odpi.openmetadata.openconnectors.governancedaemonconnectors.securitysync.rangerconnector.auditlog.RangerConnectorAuditCode;
import org.odpi.openmetadata.openconnectors.governancedaemonconnectors.securitysync.rangerconnector.model.*;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLogRecordSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

import static org.odpi.openmetadata.openconnectors.governancedaemonconnectors.securitysync.rangerconnector.util.Constants.*;

public class GovernanceEventProcessor {

    private static final Logger log = LoggerFactory.getLogger(GovernanceEventProcessor.class);
    private OMRSAuditLog auditLog;
    private SecuritySyncConfig securitySyncConfig;

    public GovernanceEventProcessor(SecuritySyncConfig securitySyncConfig, OMRSAuditLog auditLog) {
        this.auditLog = auditLog;
        this.securitySyncConfig = securitySyncConfig;
    }

    public void processClassifiedGovernedAssetEvent(GovernedAsset governedAsset) {

        logProcessing("processClassifiedGovernedAssetEvent", RangerConnectorAuditCode.CLASSIFIED_GOVERNED_ASSET_EVENT_REVEICED);

        RangerResource rangerResource = processGovernedAsset(governedAsset);
        mapResourcesToTagsInRangerServer(rangerResource);
    }

    private RangerResource processGovernedAsset(GovernedAsset governedAsset) {
        Map<Long, RangerTag> tags = buildTags(governedAsset.getAssignedGovernanceClassifications());
        List<RangerServiceResource> rangerServiceResources = getRangerServiceResources(governedAsset);
        Map<Long, List<Long>> resourceToTagIds = mapResources(tags, rangerServiceResources);

        return buildRangerResource(rangerServiceResources, tags, resourceToTagIds);
    }

    public void processReClassifiedGovernedAssetEvent(GovernedAsset governedAsset) {
        logProcessing("processReClassifiedGovernedAssetEvent", RangerConnectorAuditCode.RE_CLASSIFIED_GOVERNED_ASSET_EVENT_REVEICED);

        if(governedAsset == null){
            return;
        }
        RangerServiceResource resource = getResourceFromRanger(governedAsset.getGuid());
        if(resource == null){
            return;

        }
        ResourceTagMapper resourceTagMapper = getExistingTagIdAssociatedWithTheResource(resource.getId());
        List<RangerTag> allTags = getAllTags();


        if(resourceTagMapper != null){
            deleteMappingBetweenTagAndResource(resourceTagMapper);
        }

        if(governedAsset.getAssignedGovernanceClassifications() == null || governedAsset.getAssignedGovernanceClassifications().isEmpty()){
            return;
        }

        GovernanceClassification classification = governedAsset.getAssignedGovernanceClassifications().get(0);
        if (!classification.getAttributes().containsKey(LEVEL)) {
            return;
        }

        String newTagGuid = classification.getAttributes().get(LEVEL);
        RangerTag existingTag = existingTag(allTags, newTagGuid);

        if(existingTag == null){
            RangerTag rangerTag = buildTag(classification);
            createNewTag(rangerTag);
        }

       createMappingBetweenTagAndResource(newTagGuid, resource.getGuid());
    }

    private Map<Long, List<Long>> mapResources(Map<Long, RangerTag> tags, List<RangerServiceResource> rangerServiceResources) {
        Map<Long, List<Long>> resourceToTagIds = new HashMap<>();
        for(RangerServiceResource rangerServiceResource :  rangerServiceResources){
            resourceToTagIds.putAll(mapResourcesToTags(rangerServiceResource, tags));
        }
        return resourceToTagIds;
    }

    private List<RangerServiceResource> getRangerServiceResources(GovernedAsset governedAsset) {
        List<RangerServiceResource> rangerServiceResources = new ArrayList<>(governedAsset.getContexts().size());
        Long resourceId = 0L;
        for(Context context : governedAsset.getContexts()){
            Map<String, RangerPolicyResource> resourceMap = getRangerPolicyResourceMap(context);
            RangerServiceResource serviceResource = getRangerServiceResource(resourceMap, governedAsset.getGuid(), resourceId++);
            rangerServiceResources.add(serviceResource);
        }
        return rangerServiceResources;
    }

    private void createNewTag(RangerTag rangerTag) {
        Gson gson = new Gson();
        String body = gson.toJson(rangerTag);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, getHttpHeaders());

        try {
            String createTagURL = getRangerURL("{0}/service/tags/");
            restTemplate.exchange(createTagURL, HttpMethod.POST, entity, String.class);
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
    }

    private void logProcessing(String action, RangerConnectorAuditCode auditCode) {

        auditLog.logRecord(action,
                auditCode.getLogMessageId(),
                OMRSAuditLogRecordSeverity.INFO,
                auditCode.getFormattedLogMessage(),
                null,
                auditCode.getSystemAction(),
                auditCode.getUserAction());
    }

    private Map<Long, RangerTag> buildTags(List<GovernanceClassification> classifications) {
        Map<Long, RangerTag> tags = new HashMap<>(classifications.size());
        long index = 0L;

        for (GovernanceClassification classification : classifications) {
            final RangerTag rangerTag = buildTag(classification);
            tags.put(index++, rangerTag);
        }

        return tags;
    }

    private RangerTag buildTag(GovernanceClassification classification) {
        RangerTag tag = new RangerTag();

        tag.setCreatedBy(RANGER_CONNECTOR);
        tag.setType(CONFIDENTIALITY);
        if (classification.getAttributes().containsKey(LEVEL)) {
            tag.setGuid(classification.getAttributes().get(LEVEL));
        }

        return tag;
    }

    private Map<String, RangerPolicyResource> getRangerPolicyResourceMap(Context context) {
        Map<String, RangerPolicyResource> resourceElements = new HashMap<>(3);
        final RangerPolicyResource schemaValues = getListOfPossibleValuesOfElements(DEFAULT_SCHEMA_NAME);
        resourceElements.put(SCHEMA, schemaValues);

        final RangerPolicyResource tableValue = getListOfPossibleValuesOfElements(context.getTable());
        resourceElements.put(TABLE, tableValue);

        if (context.getColumn() != null) {
            final RangerPolicyResource columnValue = getListOfPossibleValuesOfElements(context.getColumn());
            resourceElements.put(COLUMN, columnValue);
        }

        return resourceElements;
    }

    private RangerPolicyResource getListOfPossibleValuesOfElements(String value) {
        RangerPolicyResource resourceValue = new RangerPolicyResource();
        resourceValue.setValues(Collections.singletonList(value));
        return resourceValue;
    }

    private Map<Long, List<Long>> mapResourcesToTags(RangerServiceResource rangerServiceResource, Map<Long, RangerTag> tags) {

        Map<Long, List<Long>> resourceToTagIds = new HashMap<>();
        final Long resourceId = rangerServiceResource.getId();
        List<Long> tagList = new ArrayList<>(tags.keySet());

        resourceToTagIds.put(resourceId, tagList);
        return resourceToTagIds;
    }

    private RangerResource buildRangerResource(List<RangerServiceResource> rangerServiceResource, Map<Long, RangerTag> tags, Map<Long, List<Long>> resourceToTagIds) {
        RangerResource rangerResource = new RangerResource();

        rangerResource.setTags(tags);
        rangerResource.setServiceResources(rangerServiceResource);
        rangerResource.setResourceToTagIds(resourceToTagIds);
        rangerResource.setOp(ADD_OR_UPDATE);
        rangerResource.setServiceName(securitySyncConfig.getTagServiceName());
        rangerResource.setTagVersion(1L);

        return rangerResource;
    }

    private RangerServiceResource getRangerServiceResource(Map<String, RangerPolicyResource> resourceElements, String guid, Long resourceId) {
        RangerServiceResource serviceResource = new RangerServiceResource();

        serviceResource.setId(resourceId);
        serviceResource.setGuid(guid);
        serviceResource.setServiceName(DEFAULT_SCHEMA_NAME);
        serviceResource.setCreatedBy(RANGER_CONNECTOR);
        serviceResource.setResourceElements(resourceElements);

        return serviceResource;
    }

    private void mapResourcesToTagsInRangerServer(RangerResource resource) {
        String body = getBody(resource);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, getHttpHeaders());

        try {
            String url = getRangerURL(SERVICE_TAGS_IMPORT_SERVICE_TAGS);
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
    }


    private Object mapToObject(ResponseEntity<String> result, Class className) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(result.getBody(), className);
        } catch (IOException e) {
            log.error("403", e.getMessage(), e);
        }

        return null;
    }

    private RangerServiceResource getResourceFromRanger(String resourceGuid){
        String rangerBaseURL = securitySyncConfig.getSecurityServerURL();
        String resourceURL =  MessageFormat.format(SERVICE_TAGS_RESOURCE_BY_GUID, rangerBaseURL, resourceGuid);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());

        try {
            ResponseEntity<String> result = restTemplate.exchange(resourceURL, HttpMethod.GET, entity, String.class);
            return (RangerServiceResource)mapToObject(result, RangerServiceResource.class);
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    private ResourceTagMapper getExistingTagIdAssociatedWithTheResource(Long id) {
        List<ResourceTagMapper> mapper = getMappedResources();
        if(mapper == null){
            return null;
        }

        Optional<ResourceTagMapper> mappedResource = mapper.stream().filter(resourceTagMapper -> resourceTagMapper.getResourceId().equals(id)).findFirst();
        return mappedResource.orElse(null);
    }

    private RangerTag existingTag(List<RangerTag> rangerTags, String tagGuid) {
        Optional<RangerTag> any = rangerTags.stream().filter(tag -> tag.getGuid().equals(tagGuid)).findAny();
        return any.orElse(null);
    }

    private void deleteMappingBetweenTagAndResource(ResourceTagMapper resourceTagMapper) {
        String rangerBaseURL = securitySyncConfig.getSecurityServerURL();
        String deleteAssociationURL =  MessageFormat.format("{0}/service/tags/tagresourcemap/{1}", rangerBaseURL,resourceTagMapper.getGuid());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
           restTemplate.delete(deleteAssociationURL, HttpMethod.DELETE, entity);
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
    }

    private ResourceTagMapper createMappingBetweenTagAndResource(String tagGuid, String resourceGuid) {
        String rangerBaseURL = securitySyncConfig.getSecurityServerURL();
        String createAssociation =  MessageFormat.format("{0}/service/tags/tagresourcemaps?tag-guid={1}&resource-guid={2}", rangerBaseURL,tagGuid, resourceGuid);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());
        try {
            ResponseEntity<String> result =  restTemplate.exchange(createAssociation, HttpMethod.POST, entity, String.class);
            return (ResourceTagMapper) mapToObject(result, ResourceTagMapper.class);
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
        return null;
    }

    private String getBody(RangerResource resource) {
        Gson gson = new Gson();
        return gson.toJson(resource);
    }

    private String getRangerURL(String s) {
        String rangerBaseURL = securitySyncConfig.getSecurityServerURL();
        return MessageFormat.format(s, rangerBaseURL);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> accept = new ArrayList<>(1);
        accept.add(MediaType.APPLICATION_JSON);
        headers.setAccept(accept);
        headers.set("Authorization", securitySyncConfig.getSecurityServerAuthorization());

        return headers;
    }

    private List<RangerTag> getAllTags() {
        String allMappedResources = getRangerURL(SERVICE_ALL_TAGS);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());

        try {
            ResponseEntity<List<RangerTag>> response = restTemplate.exchange(allMappedResources, HttpMethod.GET, entity, new ParameterizedTypeReference<List<RangerTag>>(){});
            return response.getBody();
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
        return Collections.emptyList();
    }

    private List<ResourceTagMapper> getMappedResources(){
        String allMappedResources = getRangerURL(SERVICE_TAGS_TAG_RESOURCE_MAPS);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(getHttpHeaders());

        try {
            ResponseEntity<List<ResourceTagMapper>> response = restTemplate.exchange(allMappedResources, HttpMethod.GET, entity, new ParameterizedTypeReference<List<ResourceTagMapper>>(){});
            return response.getBody();
        } catch (HttpStatusCodeException exception) {
            log.error(exception.getMessage());
        }
        return Collections.emptyList();
    }
}
