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

import gov.nist.hit.core.api.TestContextController;
import gov.nist.hit.core.domain.*;
import gov.nist.hit.core.edi.domain.EDITestContext;
import gov.nist.hit.core.edi.repo.EDITestContextRepository;
import gov.nist.hit.core.service.*;
import gov.nist.hit.core.service.edi.EDIMessageParser;
import gov.nist.hit.core.service.edi.EDIMessageValidator;
import gov.nist.hit.core.service.edi.EDIValidationReportConverter;
import gov.nist.hit.core.service.exception.MessageParserException;
import gov.nist.hit.core.service.exception.MessageValidationException;
import gov.nist.hit.core.service.exception.TestCaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Harold Affo (NIST)
 * 
 */

@RequestMapping("/edi/testcontext")
@RestController
public class EDITestContextController extends TestContextController{

  Logger logger = LoggerFactory.getLogger(EDITestContextController.class);

  @Autowired
  private EDITestContextRepository testContextRepository;

  @Autowired
  private EDIMessageValidator messageValidator;

  @Autowired
  private EDIMessageParser messageParser;

  @Autowired
  private EDIValidationReportConverter ediValidationReportConverter;


  @Override
  public MessageValidator getMessageValidator() {
    return messageValidator;
  }

  @Override
  public MessageParser getMessageParser() {
    return messageParser;
  }

  @Override
  public TestContext getTestContext(Long aLong) {
    return testContextRepository.findOne(aLong);
  }

  @Override
  public ValidationReportConverter getValidatioReportConverter() {
    return ediValidationReportConverter;
  }
}
