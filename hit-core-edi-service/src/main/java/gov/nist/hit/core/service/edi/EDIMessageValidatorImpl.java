package gov.nist.hit.core.service.edi;

import gov.nist.hit.core.domain.MessageValidationCommand;
import gov.nist.hit.core.domain.MessageValidationResult;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.service.edi.EDIMessageValidator;
import gov.nist.hit.core.service.exception.MessageValidationException;
 

public class EDIMessageValidatorImpl extends EDIMessageValidator {

  @Override
  public MessageValidationResult validate(TestContext testContext, MessageValidationCommand command)
      throws MessageValidationException {
    return  super.validate(testContext, command);
  }
}
