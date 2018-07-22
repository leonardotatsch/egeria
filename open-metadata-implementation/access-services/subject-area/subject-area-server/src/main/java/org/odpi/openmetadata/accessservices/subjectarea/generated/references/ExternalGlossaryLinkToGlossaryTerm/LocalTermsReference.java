/* SPDX-License-Identifier: Apache-2.0 */

// This is a generated file - do not edit - changes should be made to the templates amd/or generator to generate this file with changes.

package org.odpi.openmetadata.accessservices.subjectarea.generated.references.ExternalGlossaryLinkToGlossaryTerm;
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
import org.odpi.openmetadata.accessservices.subjectarea.generated.entities.GlossaryTerm.GlossaryTerm;


import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * This is a reference, which is a view of a relationship from the perspective of one of the ends. A relationship is
 * the link between 2 OMAS entities.
 *
 * A reference is used when working with OMAS entity APIs. An OMAS entity has attributes, classifications
 * and references.
 *
 * This Reference is called LocalTermsReference. It is used in type ExternalGlossaryLink to represent a
 * reference to an OMAS entity of type glossaryTerm. The reference also contains information
 * about the relationship; the relationship guid, name, relationship attributes and a list of unique attributes.
 * Relationship attributes are attributes of the relationship.
 *
 * It is possible to work with the relationship itself using the OMAS API using the relationship guid
 * contained in this reference.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class LocalTermsReference extends Reference implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LocalTermsReference.class);
    private static final String className = LocalTermsReference.class.getName();

    protected String relationship_Type = "IsTaxonomy";
    protected String name = "localTerms";
    protected GlossaryTerm glossaryTerm =null;



    public GlossaryTerm getGlossaryTerm() {
        return  glossaryTerm;
    }
    public void setGlossaryTerm(GlossaryTerm glossaryTerm) {
        this.glossaryTerm = glossaryTerm;
    }
    private String identifier;
    /**
     * Identifier of the corresponding element from the external glossary.
     * @return
     */
    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    private String description;
    /**
     * Description of the corresponding element from the external glossary.
     * @return
     */
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    private String steward;
    /**
     * Person who established the link to the external glossary.
     * @return
     */
    public String getSteward() {
        return this.steward;
    }

    public void setSteward(String steward) {
        this.steward = steward;
    }
    private String lastVerified;
    /**
     * Date when this reference was last checked.
     * @return
     */
    public String getLastVerified() {
        return this.lastVerified;
    }

    public void setLastVerified(String lastVerified) {
        this.lastVerified = lastVerified;
    }


    public LocalTermsReference() {
        this(null, null, (Map<String, Object>) null);
    }

    public LocalTermsReference(String guid) {
        this(guid, null, (Map<String, Object>) null);
    }

    public LocalTermsReference(String guid, String relatedEndType) {
        this(guid, relatedEndType, (Map<String, Object>) null);
    }

    public LocalTermsReference(String relatedEndType, Map<String, Object> uniqueAttributes) {
        this(null, relatedEndType, uniqueAttributes);
    }

    public LocalTermsReference(String relatedEndType, final String attrName, final Object attrValue) {
        this(null, relatedEndType, new HashMap<String, Object>() {{
            put(attrName, attrValue);
        }});
    }

    public LocalTermsReference(String guid, String relatedEndType, Map<String, Object> uniqueAttributes) {
        setRelationshipGuid(guid);
        setRelatedEndType(relatedEndType);
        setUniqueAttributes(uniqueAttributes);
    }

    public LocalTermsReference(Reference other) {
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
    public LocalTermsReference(String entityGuid, Line line) {
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

    public LocalTermsReference(Map objIdMap) {
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
	sb.append("this.identifier ");
	sb.append("this.description ");
	sb.append("this.steward ");
	sb.append("this.lastVerified ");
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
        LocalTermsReference typedThat =(LocalTermsReference)that;
        if (this.identifier != null && !Objects.equals(this.identifier,typedThat.getIdentifier())) {
            return false;
        }
        if (this.description != null && !Objects.equals(this.description,typedThat.getDescription())) {
            return false;
        }
        if (this.steward != null && !Objects.equals(this.steward,typedThat.getSteward())) {
            return false;
        }
        if (this.lastVerified != null && !Objects.equals(this.lastVerified,typedThat.getLastVerified())) {
            return false;
        }

        return Objects.equals(relatedEndType, that.getRelatedEndType()) &&
                Objects.equals(uniqueAttributes, that.getUniqueAttributes());
    }


    @Override
    public int hashCode() {
        return relatedEndGuid != null ? Objects.hash(relatedEndGuid) : Objects.hash(relatedEndType, uniqueAttributes
, this.identifier
, this.description
, this.steward
, this.lastVerified
);
    }
}