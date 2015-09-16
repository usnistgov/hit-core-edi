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

package gov.nist.hit.core.api.edi;

import gov.nist.hit.core.domain.EDITestContext;
import gov.nist.hit.core.domain.MessageCommand;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.domain.TestDomain;
import gov.nist.hit.core.repo.EDITestContextRepository;
import gov.nist.hit.core.repo.TestCaseRepository;
import gov.nist.hit.core.repo.TestContextRepository;
import gov.nist.hit.core.repo.TestStepRepository;
import gov.nist.hit.core.repo.XMLTestContextRepository;
import gov.nist.hit.core.service.MessageParser;
import gov.nist.hit.core.service.MessageValidator;
import gov.nist.hit.core.service.ProfileParser;
import gov.nist.hit.core.service.ReportService;
import gov.nist.hit.core.service.exception.MessageException;
import gov.nist.hit.core.service.exception.MessageParserException;
import gov.nist.hit.core.service.exception.MessageValidationException;
import gov.nist.hit.core.service.exception.TestCaseException;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harold Affo (NIST)
 * 
 */

@RequestMapping("/edi/testcontext")
@RestController
public class EDITestContextController {

  Logger logger = LoggerFactory.getLogger(EDITestContextController.class);
 
  @Autowired
  private EDITestContextRepository testContextRepository; //TODO: remove type coupling 
 

  @Autowired
  @Qualifier("ediMessageValidator")
  private MessageValidator messageValidator;

  @Autowired
  @Qualifier("ediMessageParser")
  private MessageParser messageParser;

  @Autowired
  private ProfileParser profileParser;

  @Autowired
  private ReportService reportService;


  @Autowired
  protected TestCaseRepository testCaseRepository;

  @RequestMapping(value = "/{testContextId}")
  public EDITestContext testContext(@PathVariable final Long testContextId) {
    logger.info("Fetching testContext with id=" + testContextId);   
    EDITestContext testContext = testContextRepository.findOne(testContextId);
    if (testContext == null) {
      throw new TestCaseException("No test context available with id=" + testContextId);
    }
    return testContext;
  }

  @RequestMapping(value = "/{testContextId}/parseMessage", method = RequestMethod.POST)
  public List<gov.nist.hit.core.domain.MessageElement> parse(
      @PathVariable final Long testContextId, @RequestBody final MessageCommand command)
      throws MessageParserException {
    try {
      logger.info("Parsing message");
      EDITestContext testContext = testContext(testContextId);
      String message = getMessageContent(command);
      return messageParser.parse( new String[]{message,
         testContext.getConformanceProfile().getIntegrationProfile().getXml(),
         testContext.getConformanceProfile().getSourceId()}).getElements();
    } catch (MessageException e) {
      throw new MessageParserException(e.getMessage());
    }
  }

  @RequestMapping(value = "/{testContextId}/validateMessage", method = RequestMethod.POST)
  public HashMap<String, String> validate(@PathVariable final Long testContextId,
      @RequestBody final MessageCommand command) throws MessageValidationException {
    try {
      EDITestContext testContext = testContext(testContextId);
      String json =
          messageValidator.validate(new String[]{command.getName(), command.getContextType(),
              getMessageContent(command), testContext.getConformanceProfile().getSourceId(),
              testContext.getConformanceProfile().getIntegrationProfile().getXml(), testContext
                  .getVocabularyLibrary().getXml(), testContext.getConstraints().getXml(),
              testContext.getAddditionalConstraints() != null ? testContext
                  .getAddditionalConstraints().getXml() : null});
      return reportService.getReports(json);
    } catch (MessageException e) {
      throw new MessageValidationException(e.getMessage());
    } catch (MessageValidationException e) {
      throw new MessageValidationException(e.getMessage());
    } catch (Exception e) {
      throw new MessageValidationException(e.getMessage());
    }
  }

  public static String getMessageContent(MessageCommand command) throws MessageException {
    String message = command.getContent();
    if (message == null) {
      throw new MessageException("No message provided");
    }
    return message;
  }



}
