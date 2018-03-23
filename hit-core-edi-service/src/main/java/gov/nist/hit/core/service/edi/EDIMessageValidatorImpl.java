package gov.nist.hit.core.service.edi;

import gov.nist.hit.core.domain.MessageValidationCommand;
import gov.nist.hit.core.domain.MessageValidationResult;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.service.edi.EDIMessageValidator;
import gov.nist.hit.core.service.exception.MessageValidationException;
 

public class EDIMessageValidatorImpl extends EDIMessageValidator {

  private String organizationName;

  @Override
  public String getProviderName() {
    return organizationName != null ? organizationName : "NIST";
  }

  @Override
  public String getValidationServiceName() {
    return getProviderName() + " Validation Tool";
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  @Override
  public MessageValidationResult validate(TestContext testContext, MessageValidationCommand command)
      throws MessageValidationException {
    return  super.validate(testContext, command);
  }
}
