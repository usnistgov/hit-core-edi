/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */

package gov.nist.hit.core.service.edi;

import gov.nist.hit.core.domain.*;
import gov.nist.hit.core.edi.domain.EDITestCaseDocument;
import gov.nist.hit.core.edi.domain.EDITestContext;
import gov.nist.hit.core.edi.repo.EDITestContextRepository;
import gov.nist.hit.core.repo.VocabularyLibraryRepository;
import gov.nist.hit.core.service.ResourceLoader;
import gov.nist.hit.core.service.ValueSetLibrarySerializer;
import gov.nist.hit.core.service.exception.ProfileParserException;
import gov.nist.hit.core.service.impl.ValueSetLibrarySerializerImpl;
import gov.nist.hit.core.service.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EDIResourceLoaderImpl extends ResourceLoader {

  static final Logger logger = LoggerFactory.getLogger(EDIResourceLoaderImpl.class);
  static final String FORMAT = "edi";

  @Autowired
  EDITestContextRepository testContextRepository;

  @Autowired
  VocabularyLibraryRepository vocabularyLibraryRepository;

  EDIProfileParser profileParser = new EDIProfileParserImpl();
  ValueSetLibrarySerializer valueSetLibrarySerializer = new ValueSetLibrarySerializerImpl();


  public EDIResourceLoaderImpl() {}

    @Override public List<ResourceUploadStatus> addOrReplaceValueSet() throws IOException {
        System.out.println("AddOrReplace VS");

        List<Resource> resources;
        try {
            resources = this.getApiResources("*.xml");
            if (resources == null || resources.isEmpty()) {
                ResourceUploadStatus result = new ResourceUploadStatus();
                result.setType(ResourceType.VALUESETLIBRARY);
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage("No resource found");
                return Arrays.asList(result);
            }
        } catch (IOException e1) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.VALUESETLIBRARY);
            result.setStatus(ResourceUploadResult.FAILURE);
            result.setMessage("Error while parsing resources");
            return Arrays.asList(result);
        }

        List<ResourceUploadStatus> results = new ArrayList<ResourceUploadStatus>();

        for (Resource resource : resources) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.VALUESETLIBRARY);
            String content = FileUtil.getContent(resource);
            try {
                VocabularyLibrary vocabLibrary = vocabLibrary(content);
                result.setId(vocabLibrary.getSourceId());
                VocabularyLibrary exist = this.getVocabularyLibrary(vocabLibrary.getSourceId());
                if (exist != null) {
                    System.out.println("Replace");
                    result.setAction(ResourceUploadAction.UPDATE);
                    vocabLibrary.setId(exist.getId());
                    vocabLibrary.setSourceId(exist.getSourceId());
                } else {
                    result.setAction(ResourceUploadAction.ADD);
                }

                this.vocabularyLibraryRepository.save(vocabLibrary);
                result.setStatus(ResourceUploadResult.SUCCESS);

            } catch (Exception e) {
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage(e.getMessage());
            }
            results.add(result);
        }
        return results;
    }

    @Override
    public List<ResourceUploadStatus> addOrReplaceConstraints() {
        System.out.println("AddOrReplace Constraints");

        List<Resource> resources;
        try {
            resources = this.getApiResources("*.xml");
            if (resources == null || resources.isEmpty()) {
                ResourceUploadStatus result = new ResourceUploadStatus();
                result.setType(ResourceType.CONSTRAINTS);
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage("No resource found");
                return Arrays.asList(result);
            }
        } catch (IOException e1) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.CONSTRAINTS);
            result.setStatus(ResourceUploadResult.FAILURE);
            result.setMessage("Error while parsing resources");
            return Arrays.asList(result);
        }

        List<ResourceUploadStatus> results = new ArrayList<ResourceUploadStatus>();

        for (Resource resource : resources) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.CONSTRAINTS);
            String content = FileUtil.getContent(resource);
            try {
                Constraints constraint = constraint(content);
                result.setId(constraint.getSourceId());
                Constraints exist = this.getConstraints(constraint.getSourceId());
                if (exist != null) {
                    System.out.println("Replace");
                    result.setAction(ResourceUploadAction.UPDATE);
                    constraint.setId(exist.getId());
                    constraint.setSourceId(exist.getSourceId());
                } else {
                    result.setAction(ResourceUploadAction.ADD);
                    System.out.println("Add");
                }

                this.constraintsRepository.save(constraint);
                result.setStatus(ResourceUploadResult.SUCCESS);

            } catch (Exception e) {
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage(e.getMessage());
            }
            results.add(result);
        }
        return results;
    }

    @Override
    public List<ResourceUploadStatus> addOrReplaceIntegrationProfile() {
        System.out.println("AddOrReplace integration profile");

        List<Resource> resources;
        try {
            resources = this.getApiResources("*.xml");
            if (resources == null || resources.isEmpty()) {
                ResourceUploadStatus result = new ResourceUploadStatus();
                result.setType(ResourceType.PROFILE);
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage("No resource found");
                return Arrays.asList(result);
            }
        } catch (IOException e1) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.PROFILE);
            result.setStatus(ResourceUploadResult.FAILURE);
            result.setMessage("Error while parsing resources");
            return Arrays.asList(result);
        }

        List<ResourceUploadStatus> results = new ArrayList<ResourceUploadStatus>();
        for (Resource resource : resources) {
            ResourceUploadStatus result = new ResourceUploadStatus();
            result.setType(ResourceType.PROFILE);
            String content = FileUtil.getContent(resource);
            try {
                IntegrationProfile integrationP = integrationProfile(content);
                result.setId(integrationP.getSourceId());
                IntegrationProfile exist = this.integrationProfileRepository
                    .findBySourceId(integrationP.getSourceId());
                if (exist != null) {
                    System.out.println("Replace");
                    result.setAction(ResourceUploadAction.UPDATE);
                    integrationP.setId(exist.getId());
                    integrationP.setSourceId(exist.getSourceId());
                } else {
                    result.setAction(ResourceUploadAction.ADD);
                    System.out.println("Add");
                }

                this.integrationProfileRepository.save(integrationP);
                result.setStatus(ResourceUploadResult.SUCCESS);
            } catch (Exception e) {
                result.setStatus(ResourceUploadResult.FAILURE);
                result.setMessage(e.getMessage());
            }
            results.add(result);
        }
        return results;

    }


    @Override
  public TestCaseDocument generateTestCaseDocument(TestContext c) throws IOException {
    EDITestCaseDocument doc = new EDITestCaseDocument();
    if (c != null) {
      EDITestContext context = testContextRepository.findOne(c.getId());
      doc.setExMsgPresent(context.getMessage() != null && context.getMessage().getContent() != null);
      doc.setXmlConfProfilePresent(context.getConformanceProfile() != null
          && context.getConformanceProfile().getJson() != null);
      doc.setXmlValueSetLibraryPresent(context.getVocabularyLibrary() != null
          && context.getVocabularyLibrary().getJson() != null);
        doc.setXmlConstraintPresent(context.getAddditionalConstraints() != null && context.getAddditionalConstraints().getXml() != null);
        ObjectMapper mapper = new ObjectMapper();
        logger.info("TestCaseDocument : "+ mapper.writeValueAsString(doc));
    }
    return doc;
  }



    @Override
    public TestContext testContext(String path, JsonNode formatObj, TestingStage stage, TestStep testStep)
            throws IOException {
        // for backward compatibility
        if (formatObj.findValue(FORMAT) == null){
            return null;
        } else {
            formatObj = formatObj.findValue(FORMAT);

            JsonNode type = formatObj.findValue("messageId");
            JsonNode constraintId = formatObj.findValue("constraintId");
            JsonNode valueSetLibraryId = formatObj.findValue("valueSetLibraryId");

            if (type != null) {

                EDITestContext testContext = new EDITestContext();
                testContext.setTestStep(testStep);
                testContext.setFormat(FORMAT);
                testContext.setStage(stage);
                if(type!=null) {
                    testContext.setType(type.textValue());
                }
                if (valueSetLibraryId != null && !"".equals(valueSetLibraryId.textValue())) {
                    testContext.setVocabularyLibrary((getVocabularyLibrary(valueSetLibraryId.textValue())));
                }
                if (constraintId != null && !"".equals(constraintId.textValue())) {
                    testContext.setConstraints(getConstraints(constraintId.textValue()));
                }
                testContext.setAddditionalConstraints(additionalConstraints(path + CONSTRAINTS_FILE_PATTERN));

                testContext.setMessage(message(FileUtil.getContent(getResource(path + "Message.txt"))));

                // TODO: Ask Woo to change Message.text to Message.txt
                if (testContext.getMessage() == null) {
                    testContext.setMessage(message(FileUtil.getContent(getResource(path + "Message.text"))));
                }

                try {
                    ConformanceProfile conformanceProfile = new ConformanceProfile();
                    IntegrationProfile integrationProfile = getIntegrationProfile(type.textValue());
                    conformanceProfile.setJson(jsonConformanceProfile(integrationProfile.getXml(), type
                            .textValue(), testContext.getConstraints() != null ? testContext.getConstraints()
                            .getXml() : null, testContext.getAddditionalConstraints() != null ? testContext
                            .getAddditionalConstraints().getXml() : null));
                    conformanceProfile.setIntegrationProfile(integrationProfile);
                    conformanceProfile.setSourceId(type.textValue());
                    testContext.setConformanceProfile(conformanceProfile);
                } catch (ProfileParserException e) {
                    logger.info("ERROR",e);
                    throw new RuntimeException("Failed to parse integrationProfile at " + path);
                }
                return testContext;
            }
            return null;
        }
    }

  @Override
  public ProfileModel parseProfile(String integrationProfileXml, String conformanceProfileId,
      String constraintsXml, String additionalConstraintsXml) throws ProfileParserException {
    return profileParser.parse(integrationProfileXml, conformanceProfileId, constraintsXml,
        additionalConstraintsXml);
  }

  @Override
  public VocabularyLibrary vocabLibrary(String content) throws JsonGenerationException,
      JsonMappingException, IOException {
    Document doc = this.stringToDom(content);
    VocabularyLibrary vocabLibrary = new VocabularyLibrary();
    Element valueSetLibraryeElement = (Element) doc.getElementsByTagName("ValueSetLibrary").item(0);
    vocabLibrary.setSourceId(valueSetLibraryeElement.getAttribute("ValueSetLibraryIdentifier"));
    vocabLibrary.setName(valueSetLibraryeElement.getAttribute("Name"));
    vocabLibrary.setDescription(valueSetLibraryeElement.getAttribute("Description"));
    vocabLibrary.setXml(content);
    vocabLibrary.setJson(obm.writeValueAsString(valueSetLibrarySerializer.toObject(content)));
    vocabularyLibraryRepository.save(vocabLibrary);
    return vocabLibrary;
  }



}
