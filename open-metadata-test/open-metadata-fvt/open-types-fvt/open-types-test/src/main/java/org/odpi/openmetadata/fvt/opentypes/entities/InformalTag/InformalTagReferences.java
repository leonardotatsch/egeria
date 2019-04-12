/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

// This is a generated file - do not edit - changes should be made to the templates amd/or generator to generate this file with changes.

package org.odpi.openmetadata.fvt.opentypes.entities.InformalTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;

import  org.odpi.openmetadata.fvt.opentypes.references.InformalTagToReferenceable.TaggedElementReference;
import org.odpi.openmetadata.fvt.opentypes.relationships.AttachedTag.AttachedTag;
import org.odpi.openmetadata.fvt.opentypes.relationships.AttachedTag.AttachedTagMapper;

import java.io.Serializable;
import java.util.*;
import org.odpi.openmetadata.fvt.opentypes.common.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * References for entity  InformalTag. References are relationships represented as an attribute. Exposing relationships information
 * as references can make the relationships information more digestable.
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class InformalTagReferences implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(InformalTagReferences.class);
    private static final String className = InformalTagReferences.class.getName();

    public static final String[] REFERENCE_NAMES_SET_VALUES = new String[] {
             "taggedElement",
             // Terminate the list
             null
    };

     public static final String[] RELATIONSHIP_NAMES_SET_VALUES = new String[] {
             "AttachedTag",
              // Terminate the list
              null
     };
     /**
       * We are passed a set of omrs relationships that are associated with a entity InformalTag
       * Each of these relationships should map to a reference (a uniquely named attribute in InformalTag).
       *
       * Relationships have cardinality. There are 2 sorts of cardinality relevant here, whether the relationships can be related to one or many
       * entities.
       *
       * @param lines the relationships lines.
       * @param entityGuid The GUID of the entity.
       */
     public InformalTagReferences(String entityGuid, Set<Line> lines)  {

        if (lines !=null) {
         for (Line relationships: lines) {
            for (int i=0;i< RELATIONSHIP_NAMES_SET_VALUES.length; i++) {
               if (relationships.getName().equals(RELATIONSHIP_NAMES_SET_VALUES[i])) {
                    String referenceName = REFERENCE_NAMES_SET_VALUES[i];

                    if ("taggedElement".equals(referenceName)) {
                         AttachedTag attachedTag_relationship = (AttachedTag)relationships;
                         TaggedElementReference taggedElementReference = new TaggedElementReference(entityGuid,attachedTag_relationship);
                         if ( taggedElement== null ) {
                              taggedElement = new HashSet();
                         }
                          taggedElement.add(taggedElementReference);
                    }

                 }
             }
         }
        }
     }

    public static final Set<String> REFERENCE_NAMES_SET = new HashSet(new HashSet<>(Arrays.asList(REFERENCE_NAMES_SET_VALUES)));
    // there may be duplicate strings in RELATIONSHIP_NAMES_SET_VALUES, the following line deduplicates the Strings into a Set.
    public static final Set<String> RELATIONSHIP_NAMES_SET = new HashSet(new HashSet<>(Arrays.asList(RELATIONSHIP_NAMES_SET_VALUES)));
// Singular properties
// Set properties

    private Set<TaggedElementReference> taggedElement;

// List properties

    // setters and setters

// Sets
    public Set<TaggedElementReference> getTaggedElementReferences() {
        return taggedElement;
    }

    public void setTaggedElementReferences(Set<TaggedElementReference> taggedElement) {
        this.taggedElement =taggedElement;
    }

// Lists

 public StringBuilder toString(StringBuilder sb) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append("InformalTagReferences{");
        sb.append("taggedElementReference='").append(taggedElement.toString());

        sb.append('}');

        return sb;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
         InformalTagReferences typedThat = (InformalTagReferences) o;
      // compare single cardinality attributes
         if (this.taggedElement != null && !Objects.equals(this.taggedElement,typedThat.taggedElement)) {
                            return false;
                 }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode()
    ,this.taggedElement
       );
    }

    @Override
    public String toString() {
        return toString(new StringBuilder()).toString();
    }
}