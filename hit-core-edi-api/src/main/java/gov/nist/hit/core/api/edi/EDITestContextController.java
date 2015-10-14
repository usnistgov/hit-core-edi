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

import gov.nist.hit.core.domain.MessageModel;
import gov.nist.hit.core.domain.MessageParserCommand;
import gov.nist.hit.core.domain.MessageValidationCommand;
import gov.nist.hit.core.domain.MessageValidationResult;
import gov.nist.hit.core.edi.domain.EDITestContext;
import gov.nist.hit.core.edi.repo.EDITestContextRepository;
import gov.nist.hit.core.service.edi.EDIMessageParser;
import gov.nist.hit.core.service.edi.EDIMessageValidator;
import gov.nist.hit.core.service.exception.MessageParserException;
import gov.nist.hit.core.service.exception.MessageValidationException;
import gov.nist.hit.core.service.exception.TestCaseException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
  private EDITestContextRepository testContextRepository;

  @Autowired
  private EDIMessageValidator messageValidator;

  @Autowired
  private EDIMessageParser messageParser;


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
  public MessageModel parse(
      @PathVariable final Long testContextId, @RequestBody final MessageParserCommand command)
      throws MessageParserException {
    logger.info("Parsing message");
    EDITestContext testContext = testContext(testContextId);
    return messageParser.parse(testContext, command);
  }

  @RequestMapping(value = "/{testContextId}/validateMessage", method = RequestMethod.POST)
  public MessageValidationResult validate(@PathVariable final Long testContextId,
      @RequestBody final MessageValidationCommand command) throws MessageValidationException {
    try {
      return messageValidator.validate(testContext(testContextId), command);
    } catch (MessageValidationException e) {
      throw new MessageValidationException(e.getMessage());
    } catch (Exception e) {
      throw new MessageValidationException(e.getMessage());
    }
  }



}
