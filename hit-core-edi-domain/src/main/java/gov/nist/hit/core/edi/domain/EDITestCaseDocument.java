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
  protected boolean xmlConstraintsPresent;



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

  public boolean isXmlConstraintsPresent() {
    return xmlConstraintsPresent;
  }

  public void setXmlConstraintsPresent(boolean xmlConstraintsPresent) {
    this.xmlConstraintsPresent = xmlConstraintsPresent;
  }

}
