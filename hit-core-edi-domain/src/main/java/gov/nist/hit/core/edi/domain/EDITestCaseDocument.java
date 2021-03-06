package gov.nist.hit.core.edi.domain;

import gov.nist.hit.core.domain.TestCaseDocument;

import java.io.Serializable;

public class EDITestCaseDocument extends TestCaseDocument implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected boolean exMsgPresent;
  protected boolean xmlConfProfilePresent;
  protected boolean xmlValueSetLibraryPresent;
  protected boolean xmlConstraintPresent;



  public EDITestCaseDocument() {
    super();
    this.format = "edi";
  }


  public boolean isExMsgPresent() {
    return exMsgPresent;
  }

  public void setExMsgPresent(boolean exMsgPresent) {
    this.exMsgPresent = exMsgPresent;
  }

  public boolean isXmlConfProfilePresent() {
    return xmlConfProfilePresent;
  }

  public void setXmlConfProfilePresent(boolean xmlConfProfilePresent) {
    this.xmlConfProfilePresent = xmlConfProfilePresent;
  }

  public boolean isXmlValueSetLibraryPresent() {
    return xmlValueSetLibraryPresent;
  }

  public void setXmlValueSetLibraryPresent(boolean xmlValueSetLibraryPresent) {
    this.xmlValueSetLibraryPresent = xmlValueSetLibraryPresent;
  }

  public boolean isXmlConstraintPresent() {
    return xmlConstraintPresent;
  }

  public void setXmlConstraintPresent(boolean xmlConstraintsPresent) {
    this.xmlConstraintPresent = xmlConstraintsPresent;
  }

}
