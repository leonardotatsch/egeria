/* SPDX-License-Identifier: Apache-2.0 */

// This is a generated file - do not edit - changes should be made to the templates amd/or generator to generate this file with changes.

package org.odpi.openmetadata.accessservices.subjectarea.generated.references.SoftwareServerCapabilityToSoftwareServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.common.Reference;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.line.Line;
import org.odpi.openmetadata.accessservices.subjectarea.generated.entities.SoftwareServer.SoftwareServer;


import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * This is a reference, which is a view of a relationship from the perspective of one of the ends. A relationship is
 * the link between 2 OMAS entities.
 *
 * A reference is used when working with OMAS entity APIs. An OMAS entity has attributes, classifications
 * and references.
 *
 * This Reference is called ServersReference. It is used in type SoftwareServerCapability to represent a
 * reference to an OMAS entity of type softwareServer. The reference also contains information
 * about the relationship; the relationship guid, name, relationship attributes and a list of unique attributes.
 * Relationship attributes are attributes of the relationship.
 *
 * It is possible to work with the relationship itself using the OMAS API using the relationship guid
 * contained in this reference.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class ServersReference extends Reference implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ServersReference.class);
    private static final String className = ServersReference.class.getName();

    protected String relationship_Type = "ServerSupportedCapability";
    protected String name = "servers";
    protected SoftwareServer softwareServer =null;



    public SoftwareServer getSoftwareServer() {
        return  softwareServer;
    }
    public void setSoftwareServer(SoftwareServer softwareServer) {
        this.softwareServer = softwareServer;
    }
    private String deploymentTime;
    /**
     * Time that the software server capability was deployed to the software server.
     * @return
     */
    public String getDeploymentTime() {
        return this.deploymentTime;
    }

    public void setDeploymentTime(String deploymentTime) {
        this.deploymentTime = deploymentTime;
    }
    private String deployer;
    /**
     * Person, organization or engine that deployed the software server capability.
     * @return
     */
    public String getDeployer() {
        return this.deployer;
    }

    public void setDeployer(String deployer) {
        this.deployer = deployer;
    }
    private String serverCapabilityStatus;
    /**
     * The operational status of the software server capability on this software server.
     * @return
     */
    public String getServerCapabilityStatus() {
        return this.serverCapabilityStatus;
    }

    public void setServerCapabilityStatus(String serverCapabilityStatus) {
        this.serverCapabilityStatus = serverCapabilityStatus;
    }


    public ServersReference() {
        this(null, null, (Map<String, Object>) null);
    }

    public ServersReference(String guid) {
        this(guid, null, (Map<String, Object>) null);
    }

    public ServersReference(String guid, String relatedEndType) {
        this(guid, relatedEndType, (Map<String, Object>) null);
    }

    public ServersReference(String relatedEndType, Map<String, Object> uniqueAttributes) {
        this(null, relatedEndType, uniqueAttributes);
    }

    public ServersReference(String relatedEndType, final String attrName, final Object attrValue) {
        this(null, relatedEndType, new HashMap<String, Object>() {{
            put(attrName, attrValue);
        }});
    }

    public ServersReference(String guid, String relatedEndType, Map<String, Object> uniqueAttributes) {
        setRelationshipGuid(guid);
        setRelatedEndType(relatedEndType);
        setUniqueAttributes(uniqueAttributes);
    }

    public ServersReference(Reference other) {
        if (other != null) {
            setRelationshipGuid(other.getRelationshipGuid());
            setRelatedEndGuid(other.getRelatedEndGuid());
            setRelatedEndType(other.getRelatedEndType());
            setUniqueAttributes(other.getUniqueAttributes());
        }
    }
    
      /**
       * Populate the reference from a relationship
       * @param entityGuid String entity Guid
       * @param line Line
       */
    public ServersReference(String entityGuid, Line line) {
        setRelationshipGuid(line.getGuid());
        if (entityGuid.equals(line.getEntity1Guid())) {
            setRelatedEndGuid(line.getEntity2Guid());
            setRelatedEndType(line.getEntity2Type());
            // TODO UniqueAttributes
            //setUniqueAttributes(line.get
        } else {
            setRelatedEndGuid(line.getEntity1Guid());
            setRelatedEndType(line.getEntity1Type());
            // TODO UniqueAttributes
            //setUniqueAttributes(line.get
        }
    }    

    public ServersReference(Map objIdMap) {
        if (objIdMap != null) {
            Object reg = objIdMap.get(KEY_RELATED_END_GUID);
            Object rg = objIdMap.get(KEY_RELATIONSHIP_GUID);
            Object t = objIdMap.get(KEY_TYPENAME);
            Object u = objIdMap.get(KEY_UNIQUE_ATTRIBUTES);

            if (reg != null) {
                setRelatedEndGuid(reg.toString());
            }
            if (rg != null) {
                setRelationshipGuid(rg.toString());
            }


            if (t != null) {
                setRelatedEndType(t.toString());
            }

            if (u != null && u instanceof Map) {
                setUniqueAttributes((Map) u);
            }
        }
    }

    public StringBuilder toString(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append("Reference{");
        sb.append("relatedEndGuid='").append(getRelatedEndGuid()).append('\'');
        sb.append("relationshipGuid='").append(getRelationshipGuid()).append('\'');
        sb.append("relatedEndType='").append(getRelatedEndType()).append('\'');
        sb.append(", uniqueAttributes={");
//  AtlasBaseTypeDef.dumpObjects(uniqueAttributes, sb);
        sb.append("}");
 	sb.append("{");
	sb.append("this.deploymentTime ");
	sb.append("this.deployer ");
	sb.append("this.serverCapabilityStatus ");
 	sb.append('}');


        sb.append('}');

        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Reference that = (Reference) o;

        if (relatedEndGuid != null && !Objects.equals(relatedEndGuid, that.getRelatedEndGuid())) {
            return false;
        }
        if (relationshipGuid != null && !Objects.equals(relationshipGuid, that.getRelationshipGuid())) {
            return false;
        }
        ServersReference typedThat =(ServersReference)that;
        if (this.deploymentTime != null && !Objects.equals(this.deploymentTime,typedThat.getDeploymentTime())) {
            return false;
        }
        if (this.deployer != null && !Objects.equals(this.deployer,typedThat.getDeployer())) {
            return false;
        }
        if (this.serverCapabilityStatus != null && !Objects.equals(this.serverCapabilityStatus,typedThat.getServerCapabilityStatus())) {
            return false;
        }

        return Objects.equals(relatedEndType, that.getRelatedEndType()) &&
                Objects.equals(uniqueAttributes, that.getUniqueAttributes());
    }


    @Override
    public int hashCode() {
        return relatedEndGuid != null ? Objects.hash(relatedEndGuid) : Objects.hash(relatedEndType, uniqueAttributes
, this.deploymentTime
, this.deployer
, this.serverCapabilityStatus
);
    }
}