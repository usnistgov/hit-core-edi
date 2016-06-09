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

import gov.nist.healthcare.unified.exceptions.ConversionException;
import gov.nist.healthcare.unified.exceptions.NotFoundException;
import gov.nist.healthcare.unified.model.*;
import gov.nist.healthcare.unified.model.Collection;
import gov.nist.hit.core.edi.domain.EDITestContext;

import gov.nist.healthcare.unified.enums.Context;
import gov.nist.healthcare.unified.proxy.ValidationProxy;
import gov.nist.hit.core.domain.MessageValidationCommand;
import gov.nist.hit.core.domain.MessageValidationResult;
import gov.nist.hit.core.domain.TestContext;

import gov.nist.hit.core.service.MessageValidator;
import gov.nist.hit.core.service.exception.MessageException;
import gov.nist.hit.core.service.exception.MessageValidationException;
import hl7.v2.validation.content.ConformanceContext;
import hl7.v2.validation.content.DefaultConformanceContext;
import hl7.v2.validation.vs.ValueSetLibrary;
import hl7.v2.validation.vs.ValueSetLibraryImpl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public abstract class EDIMessageValidator implements MessageValidator {

  private static Log statLog = LogFactory.getLog("StatLog");

  @Override
  public MessageValidationResult validate(TestContext testContext, MessageValidationCommand command)
          throws MessageValidationException {
    try {
      EnhancedReport report = generateReport(testContext, command);
      if (report != null) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Detections detections = report.getDetections();
        boolean validationSuccess = true;
        StringBuilder validationResult = new StringBuilder();

        for (Classification classification : detections.classes()) {
          if (classification.getName().equals("Affirmative")) {

          } else if (classification.getName().equals("Warning")) {
            int warningCount = classification.keys().size();
            validationResult.append(" - ");
            validationResult.append(warningCount);
            validationResult.append(" warning");
            if (warningCount > 1) {
              validationResult.append("s");
            }
          } else if (classification.getName().equals("Error")) {
            validationSuccess = false;
            int errorCount = classification.keys().size();
            if (errorCount > 0) {
              HashMap<String, Integer> segmentCount = new HashMap<>();
              validationResult.append(" - ");
              validationResult.append(errorCount);
              validationResult.append(" error");
              if (errorCount > 1) {
                validationResult.append("s");
              }
              for (String key : classification.keys()) {
                Collection collection = classification.getArray(key);
                collection.getName();
                for (int i = 0; i < collection.size(); i++) {
                  Section section = collection.getObject(i);
                  String path = section.getString("path");
                  if (path != null && !"".equals(path)) {
                    path = path.split("\\[")[0];
                    int segmentErrorCount = 1;
                    if (segmentCount.containsKey(path)) {
                      segmentErrorCount = segmentCount.get(path) + 1;
                    }
                    segmentCount.put(path, segmentErrorCount);
                  }
                }
              }
              boolean isFirst = true;
              if(segmentCount.size()>0) {
                validationResult.append(" [");
                for (String path : segmentCount.keySet()) {
                  if (!isFirst) {
                    validationResult.append(", ");
                  }
                  validationResult.append(path);
                  validationResult.append(" (");
                  validationResult.append(segmentCount.get(path));
                  validationResult.append(" error");
                  if (segmentCount.get(path) > 1) {
                    validationResult.append("s");
                  }
                  validationResult.append(")");
                  isFirst = false;
                }
                validationResult.append("]");
              }
            }
          }
        }
        StringBuilder validationLog = new StringBuilder();
        validationLog.append(simpleDateFormat.format(new Date()));
        validationLog.append(" [Validation] EDI - ");
        EDITestContext ediTestContext = (EDITestContext)testContext;
        if(ediTestContext.getType()!=null) {
          validationLog.append(ediTestContext.getType());
          validationLog.append(" - ");
        }
        if (validationSuccess)
          validationLog.append("success");
        else
          validationLog.append("fail");
        validationLog.append(validationResult.toString());
        statLog.info(validationLog.toString());
        Map<String, String> nav = command.getNav();
        if (nav != null && !nav.isEmpty()) {
          report.setTestCase(nav.get("testPlan"), nav.get("testGroup"), nav.get("testCase"),
                  nav.get("testStep"));
        }
        return new MessageValidationResult(report.to("json").toString(), report.render("iz-report", null));
      }

    throw new MessageValidationException();
    } catch (MessageException e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    } catch (RuntimeException e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    } catch (Exception e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    }
  }


  public EnhancedReport generateReport(TestContext testContext, MessageValidationCommand command)
          throws MessageValidationException {
    try {
      if (testContext instanceof EDITestContext) {
        EDITestContext v2TestContext = (EDITestContext) testContext;
        String contextType = command.getContextType();
        String message = getMessageContent(command);
        String conformanceProfielId = v2TestContext.getConformanceProfile().getSourceId();
        String integrationProfileXml =
                v2TestContext.getConformanceProfile().getIntegrationProfile().getXml();
        String valueSets = v2TestContext.getVocabularyLibrary().getXml();
        String c1 = v2TestContext.getConstraints().getXml();
        String c2 =
                v2TestContext.getAddditionalConstraints() != null ? v2TestContext
                        .getAddditionalConstraints().getXml() : null;
        InputStream c1Stream = c1 != null ? IOUtils.toInputStream(c1) : null;
        InputStream c2Stream = c2 != null ? IOUtils.toInputStream(c2) : null;
        List<InputStream> cStreams = new ArrayList<InputStream>();
        if (c1Stream != null)
          cStreams.add(c1Stream);
        if (c2Stream != null)
          cStreams.add(c2Stream);
        ConformanceContext c = getConformanceContext(cStreams);
        ValueSetLibrary vsLib =
                valueSets != null ? getValueSetLibrary(IOUtils.toInputStream(valueSets)) : null;
        ValidationProxy vp = new ValidationProxy("NIST Validation Tool", "NIST");
        EnhancedReport report =
                vp.validate(message, integrationProfileXml, c, vsLib, conformanceProfielId,
                        Context.valueOf(contextType));
        if (report != null) {
          Map<String, String> nav = command.getNav();
          if (nav != null && !nav.isEmpty()) {
            report.setTestCase(nav.get("testPlan"), nav.get("testGroup"), nav.get("testCase"),
                    nav.get("testStep"));
          }
        }
        return report;
      }
      throw new MessageValidationException();
    } catch (MessageException e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    } catch (RuntimeException e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    } catch (Exception e) {
      throw new MessageValidationException(e.getLocalizedMessage());
    }
  }


  protected ConformanceContext getConformanceContext(List<InputStream> confContexts) {
    ConformanceContext c = DefaultConformanceContext.apply(confContexts).get();
    return c;
  }

  protected ValueSetLibrary getValueSetLibrary(InputStream vsLibXML) {
    ValueSetLibrary valueSetLibrary = ValueSetLibraryImpl.apply(vsLibXML).get();
    return valueSetLibrary;
  }


  public static String getMessageContent(MessageValidationCommand command) throws MessageException {
    String message = command.getContent();
    if (message == null) {
      throw new MessageException("No message provided");
    }
    return message;
  }



}
